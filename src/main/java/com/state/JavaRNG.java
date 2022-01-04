package com.state;

import com.functional.Tuple;

import java.util.Random;

public class JavaRNG implements RNG {
    private final Random random;

    private JavaRNG(long seed) {
        random = new Random(seed);
    }

    @Override
    public Tuple<Integer, RNG> nextInt() {
        return Tuple.create(random.nextInt(), this);
    }

    public static RNG rng(long seed) {
        return new JavaRNG(seed);
    }
}
