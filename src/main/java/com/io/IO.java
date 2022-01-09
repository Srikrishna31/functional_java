package com.io;

import com.functional.Function;
import com.functional.Nothing;

/**
 * A utility interface to define functional
 * input and output.
 * @param <A>
 */
public interface IO<A> {
    A run();

    default IO<A> add(IO<A> io) {
        return () -> {
            run();
            return io.run();
        };
    }

    /**
     * Wrap the value a in the context of IO.
     * @param a : The value to be wrapped.
     * @param <A> : Type parameter of A.
     * @return the IO object.
     */
    static <A> IO<A> unit(A a) {
        return () -> a;
    }

    IO<Nothing> empty = () -> Nothing.instance;

    default <B> IO<B> map(Function<A, B> f) {
        return () -> f.apply(run());
    }

    default <B> IO<B> flatMap(Function<A, IO<B>> f) {
        return f.apply(run());
    }


}
