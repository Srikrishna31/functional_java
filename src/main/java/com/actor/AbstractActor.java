package com.actor;

import com.util.Result;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

public abstract class AbstractActor<T> implements Actor<T> {
    private final ActorContext<T> context;
    protected final String id;
    private final ExecutorService executorService;

    public AbstractActor(String id, Actor.Type type) {
        super();
        this.id = id;
        this.executorService = type == Type.SERIAL
                ? Executors.newSingleThreadExecutor(new DaemonThreadFactory())
                : Executors.newCachedThreadPool(new DaemonThreadFactory());
        this.context = new ActorContext<T>() {
            private MessageProcessor<T> behavior = AbstractActor.this::onReceive;

            /**
             * To change its behavior, the ActorContext simply registers the new
             * behavior. This is where the mutation occurs, but it's hidden by
             * the framework.
             * @param behavior
             */
            @Override
            public void become(MessageProcessor<T> behavior) {
                this.behavior = behavior;
            }

            @Override
            public MessageProcessor<T> getBehavior() {
                return behavior;
            }
        };
    }

    /**
     * The onReceive method will hold the business processing and will be
     * implemented by the user of the API.
     * @param message
     * @param sender
     */
    public abstract void onReceive(T message, Result<Actor<T>> sender);

    public Result<Actor<T>> self() {
        return Result.success(this);
    }

    public ActorContext<T> getContext() {
        return context;
    }

    @Override
    public void shutdown() {
        executorService.shutdown();
    }

    /**
     * The tell method is how an actor receives a message. It's synchronized to
     * ensure that messages are processed one at a time.
     * @param message
     * @param sender
     */
    public synchronized void tell(final T message, Result<Actor<T>> sender) {
        executorService.execute(() -> {
            try {
                context.getBehavior().process(message, sender);
            } catch (RejectedExecutionException e) {
                /**
                 * This is probably normal and means all pending tasks were
                 * canceled because the actor was stopped.
                 */
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
