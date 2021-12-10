package com.functional;

import java.util.Objects;

public class Tuple3 <T,U,V> {
    public final T _1;
    public final U _2;
    public final V _3;

    private Tuple3(T t, U u, V v) {
        this._1 = Objects.requireNonNull(t);
        this._2 = Objects.requireNonNull(u);
        this._3 = Objects.requireNonNull(v);
    }

    @Override
    public boolean equals(Object o) {
        if (! (o instanceof Tuple3)) return false;
        else {
            Tuple3 that = (Tuple3) o;

            return _1.equals(that._1) && _2.equals(that._2) && _3.equals(that._3);
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;

        result = prime * result + _1.hashCode();
        result = prime * result + _2.hashCode();
        result = prime * result + _3.hashCode();

        return result;
    }

    @Override
    public String toString() {
        return "( " + _1.toString() + ", " + _2.toString() + ", " + _3.toString() + " )";
    }

    public static <T,U,V> Tuple3<T,U,V> create(T t, U u, V v) {return new Tuple3<>(t,u,v); }
}
