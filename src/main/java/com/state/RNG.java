package com.state;

import com.functional.Tuple;

public interface RNG {

    Tuple<Integer, RNG> nextInt();

    static Tuple<Integer, RNG> integer(RNG rng) {
        return rng.nextInt();
    }
}


