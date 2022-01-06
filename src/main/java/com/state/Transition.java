package com.state;

import com.functional.Function;
import com.functional.StateTuple;

public interface Transition<A, S> extends Function<StateTuple<A, S>, S> {
}
