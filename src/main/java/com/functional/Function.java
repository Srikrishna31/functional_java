package com.functional;

import com.util.CollectionUtilities;

import java.util.List;

import static com.util.CollectionUtilities.reverse;
/**
 * A functional interface encapsulating a computation, which transforms a type T to type U.
 * Also provides utilities to compose functions, and curried versions of them.
 */
public interface Function<T,U> {
    U apply(T arg);

    default <V> Function<V,U> compose (Function<V,T> f) {
        return x -> apply(f.apply(x));
    }

    default <V> Function<T, V> andThen(Function<U,V> f) {
        return x -> f.apply(apply(x));
    }

    static <T> Function<T, T> identity() {
        return t -> t;
    }

    static <V,T,U> Function<V,U> compose(Function<T, U> f,
                                         Function<V, T> g) {
        return x -> f.apply(g.apply(x));
    }

    static <T,U,V> Function<T, V> andThen(Function<T, U> f,
                                      Function<U, V> g) {
        return x -> g.apply(f.apply(x));
    }

    static <V,T,U> Function<Function<T,U>, Function<Function<U,V>, Function<T,V>>> compose() {
        return x -> y -> y.compose(x);
    }

    static <T,U,V> Function<Function<T,U>, Function<Function<V,T>, Function<V,U>>> andThen() {
        return x -> y -> y.andThen(x);
    }

    static <T,U,V> Function<Function<T, U>, Function<Function<U,V>, Function<T,V>>> higherCompose() {
        return x -> y -> z -> y.apply(x.apply(z));
    }

    static <T,U,V> Function<Function<U,V>, Function<Function<T,U>, Function<T,V>>> higherAndThen() {
        return (Function<U,V> x) -> (Function<T,U> y) -> (T z) -> x.apply(y.apply(z));
    }

    static <T> Function<T, T> composeAll(List<Function<T, T>> fs) {
        return composeAllWithFoldLeft(fs);
    }

    static <T> Function<T, T> composeAllWithFoldLeft(List<Function<T, T>> fs) {
        return x -> CollectionUtilities.foldLeft(CollectionUtilities.reverse(fs), x, a -> b -> b.apply(a));
    }

    static <T> Function<T, T> composeAllWithFoldRight(List<Function<T, T>> fs) {
        return x -> CollectionUtilities.foldRight(fs, x, a -> a::apply);
    }

    static <T> Function<T, T> andThenAll(List<Function<T, T>> fs) {
        return andThenAllWithFoldLeft(fs);
    }

    static <T> Function<T, T> andThenAllWithFoldLeft(List<Function<T, T>> fs) {
        return x -> CollectionUtilities.foldLeft(fs, x, a-> b -> b.apply(a));
    }

    static <T> Function<T, T> andThenAllWithFoldRight(List<Function<T, T>> fs) {
        return x -> CollectionUtilities.foldRight(reverse(fs), x, a -> a::apply);
    }
}
