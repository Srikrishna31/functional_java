package com.state;

import com.functional.Function;
import com.functional.Nothing;
import com.functional.StateTuple;
import com.functional.Tuple;
import com.util.List;
import com.util.Result;

public class StateMachine<A, S> {
    Function<A, State<Nothing, S>> function;

    public StateMachine(List<Tuple<Condition<A, S>, Transition<A, S>>> transitions) {
        function = a ->
                State.sequence(m ->
                        Result.success(Tuple.create(a, m))
                                .flatMap(t ->
                                        transitions
                                                .filter((Tuple<Condition<A, S>, Transition<A, S>> x) ->
                                                        x._1.apply(new StateTuple<A, S>(t._1, t._2)))
                                                .headOption().map((Tuple<Condition<A, S>,
                                                        Transition<A, S>> y) ->
                                                        y._2.apply(new StateTuple<A, S>(t._1, t._2)))).getOrElse(m));
    }

    public State<S, S> process(List<A> inputs) {
        List<State<Nothing, S>> a = inputs.map(function);
        State<List<Nothing>, S> b = State.compose(a);
        return b.flatMap(x -> State.get());
    }
}
