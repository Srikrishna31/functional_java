package com.actor.fibonacci;

import com.actor.AbstractActor;
import com.actor.Actor;
import com.actor.MessageProcessor;
import com.functional.Executable;
import com.functional.Function;
import com.functional.Effect;
import com.functional.Tuple;
import com.util.List;
import com.util.Result;

public class Manager extends AbstractActor<Integer> {
    /**
     * The Manager stores the references to its client, to which it will send the
     * result of the computation.
     */
    private final Actor<Result<List<Integer>>> client;
    /**
     * The number of worker actors to create.
     */
    private final int workers;
    /**
     * The initial list will be a list of tuples of integers, holding both the
     * number to process(._1) and the position in the list(._2)
     */
    private final List<Tuple<Integer, Integer>> initial;
    /**
     * The workList is the list of tasks remaining to be executed once all
     * worker actors have been given their first task.
     */
    private final List<Integer> workList;
    /**
     * The result list will hold the results of the computation.
     */
    private final List<Integer> resultList;
    /**
     * The managerFunction is the heart of the Manager, determining what it will
     * be able to do. This function will be applied each time the manager receives
     * a result from a worker.
     */
    private final Function<Manager, Function<Behavior, Effect<Integer>>> managerFunction;

    public Manager(String id, List<Integer> list, Actor<Result<List<Integer>>> client, int workers) {
        super(id, Type.SERIAL);
        this.client = client;
        this.workers = workers;
        /**
         * The list of values to be processed is split at the number of workers
         * in order to obtain a list of initial tasks and a list of remaining
         * tasks.
         */
        Tuple<List<Integer>, List<Integer>> splitLists = list.splitAt(this.workers);
        /**
         * The list of initial tasks(numbers for which the Fibonacci value will be
         * computed) is zipped with the position of its elements. The position
         * (numbers from 0 to n) will only be used to name the worker actors from
         * 0 to n.
         */
        this.initial = splitLists._1.zipWithPosition();
        /**
         * The workList is set to the remaining tasks.
         */
        this.workList = splitLists._2;
        this.resultList = List.list();

        /**
         * The manager function, representing the work of the manager, is a
         * curried function of the manager itself, its behavior, and the
         * received message(i), which will be the result of a subtask.
         */
        managerFunction = manager -> behavior -> i -> {
            /**
             * When a result is received, it's added to the list of results,
             * which is fetched from the manager behavior.
             */
            List<Integer> result = behavior.resultList.cons(i);
            /**
             * If the resultList length is equal to the input list length, the
             * computation is finished, so the result is reversed and sent to the
             * client.
             */
            if(result.length() == list.length()) {
                client.tell(Result.success(result.reverse()));
            } else {
                /**
                 * Otherwise, the become method of the context is called to change
                 * the behavior of the Manager. Here, this change of behavior is
                 * in fact a change of state. The new behavior is created with
                 * the tail of the workList and the current list of results (to
                 * which the received value has been added).
                 */
                manager.getContext().become(new Behavior(behavior.workList.tailOption().getOrElse(List.list()),
                        result));
            }
        };
    }

    class Behavior implements MessageProcessor<Integer> {
        private final List<Integer> workList;
        private final List<Integer> resultList;

        /**
         * The Behavior is constructed with the workList(from which the head
         * has been removed prior to calling the constructor) and the resultList
         * (to which a result has been added).
         * @param workList
         * @param resultList
         */
        private Behavior(List<Integer> workList, List<Integer> resultList) {
            this.workList = workList;
            this.resultList = resultList;
        }

        /**
         * The process method, which will be called upon reception of a message,
         * first applies the managerFunction to the received message. Then it
         * sends the next task(the head of the workList) to the sender( a Worker
         * actor that will process it) or, if the workList is empty, it simply
         * instructs the worker actor to shut down.
         * @param integer
         * @param sender
         */
        @Override
        public void process(Integer integer, Result<Actor<Integer>> sender) {
            managerFunction.apply(Manager.this).apply(Behavior.this).apply(integer);
            sender.forEach(a -> workList.headOption().forEachOrFail(x -> a.tell(x, self())).forEach(x -> a.shutdown()));
        }
    }

    /**
     * In order to start, the Manager sends a message to itself. What the message
     * is makes no difference, because the behavior has yet to be initalized.
     */
    public void start() {
        onReceive(0, self());
        initial.sequence(this::initWorker)
                .forEachOrFail(this::initWorkers)
                .forEach(this::tellClientEmptyResult);
    }

    private Result<Executable> initWorker(Tuple<Integer, Integer> t) {
        return Result.success(() -> new Worker("Worker " + t._2, Type.SERIAL).tell(t._1, self()));
    }

    private void initWorkers(List<Executable> lst) {
        lst.forEach(Executable::exec);
    }

    private void tellClientEmptyResult(String string) {
        client.tell(Result.failure(string + " caused by empty input list."));
    }

    @Override
    public void onReceive(Integer message, Result<Actor<Integer>> sender) {
        /**
         * This is the initial behavior of the Manager. As part of its initialization,
         * it switches behavior, starting with the workList containing the remaining
         * tasks and the empty resultList.
         */
        getContext().become(new Behavior(workList, resultList));
    }
}
