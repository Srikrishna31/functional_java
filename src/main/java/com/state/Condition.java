package com.state;

import com.functional.Function;
import com.functional.StateTuple;

public interface Condition<I, S> extends Function<StateTuple<I, S>, Boolean> {
}
