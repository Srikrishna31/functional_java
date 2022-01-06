package com.state;

import com.functional.Tuple;
import com.util.List;

class Atm {
    static StateMachine<Input, Outcome> createMachine() {
        Condition<Input, Outcome> predicate1 = t -> t.value.isDeposit();
        Transition<Input, Outcome> transition1 = t -> new Outcome(t.state.account + t.value.getAmount(),
                                                                    t.state.operations.cons(t.value.getAmount()));

        Condition<Input, Outcome> predicate2 = t -> t.value.isWithDraw() && t.state.account >= t.value.getAmount();
        Transition<Input, Outcome> transition2 = t -> new Outcome(t.state.account - t.value.getAmount(),
                                                                    t.state.operations.cons(-t.value.getAmount()));

        Condition<Input, Outcome> predicate3 = t -> true;
        Transition<Input, Outcome> transition3 = t -> t.state;

        var transitions = List.list(Tuple.create(predicate1, transition1),
                                                                                    Tuple.create(predicate2, transition2),
                                                                                    Tuple.create(predicate3, transition3));

        return new StateMachine<>(transitions);
    }
}
