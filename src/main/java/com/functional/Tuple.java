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

    /**
     * Override the equals and hashCode methods to ensure that these
     * objects can be stored in a map. This is useful for memoizing the
     * function calls.
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (! (o instanceof Tuple3)) return false;
        else {
            Tuple3 that = (Tuple3) o;

            return _1.equals(that._1) && _2.equals(that._2);
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;

        result = prime * result + _1.hashCode();
        result = prime * result + _2.hashCode();

        return result;
    }

    @Override
    public String toString() {
        return "( " + _1.toString() + ", " + _2.toString() + " )";
    }
}
