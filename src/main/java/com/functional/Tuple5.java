package com.functional;

import java.util.Objects;

public class Tuple5 <A,B,C, D, E> {
    public final A _1;
    public final B _2;
    public final C _3;
    public final D _4;
    public final E _5;

    private Tuple5(A a, B b, C c, D d, E e) {
        this._1 = Objects.requireNonNull(a);
        this._2 = Objects.requireNonNull(b);
        this._3 = Objects.requireNonNull(c);
        this._4 = Objects.requireNonNull(d);
        this._5 = Objects.requireNonNull(e);
    }

    @Override
    public boolean equals(Object o) {
        if (! (o instanceof com.functional.Tuple3)) return false;
        else {
            Tuple5 that = (Tuple5) o;

            return _1.equals(that._1) && _2.equals(that._2) &&
                    _3.equals(that._3) && _4.equals(that._4) &&
                    _5.equals(that._5);
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;

        result = prime * result + _1.hashCode();
        result = prime * result + _2.hashCode();
        result = prime * result + _3.hashCode();
        result = prime * result + _4.hashCode();
        result = prime * result + _5.hashCode();

        return result;
    }

    @Override
    public String toString() {
        return "( " + _1.toString() + ", " + _2.toString() + ", " + _3.toString() + ", " + _4.toString() + ", " + _5.toString() +
        " )";
    }

    public static <A,B,C,D, E> Tuple5<A,B,C,D, E> create(A a, B b, C c, D d, E e) {
        return new Tuple5<>(a, b, c, d, e);
    }
}
