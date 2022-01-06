package com.functional;

import com.functional.Tuple;

import java.util.Objects;

/**
 * A convenience class, which is same as tuple. Instead of _1 and _2 as members,
 * the members are explicitly named as value and state to make the coding of a
 * state machine simple.
 * @param <A>
 * @param <B>
 */
public class StateTuple<A, B> extends Tuple<A,B> {
    public final A value;
    public final B state;

    public StateTuple(A a, B b) {
        super(a, b);
        value = Objects.requireNonNull(a);
        state = Objects.requireNonNull(b);
    }

    @Override
    public String toString() {
        return "(value: " + value + ", state: " + state + ")";
    }
}
