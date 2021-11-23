package com.functional;

/**
 * A utility class holding a pair of arguments. This is pretty handy
 * in functional programming.
 * @param <T>
 * @param <U>
 */
public class Tuple<T,U> {
    public final T _1;
    public final U _2;

    Tuple(T t, U u) {
        this._1 = t;
        this._2 = u;
    }

    /**
     * A factory function to create a tuple instance.
     * @param t : first parameter
     * @param u : second parameter
     * @param <T>: first type parameter
     * @param <U> : second type parameter
     * @return a Tuple<T,U> object holding both the values provided.
     */
    public static <T,U>  Tuple<T,U> create(T t, U u) {
        return new Tuple<>(t, u);
    }
}
