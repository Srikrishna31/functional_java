package com.util;

import com.functional.Function;
import java.util.function.Supplier;

/**
 * A utility class that defines the Failure and Success of computation. By
 * convention, Right indicates the presence or success of a computation, wherease
 * Left indicates the absence or failure of a computation.
 * @param <E> : Type parameter for Error (could be string or exception)
 * @param <A> : Type parameter of Success value.
 */
public abstract class Either<E,A> {
    private Either() {}

    @Override
    public abstract String toString();

    public abstract <B> Either<E, B> map(Function<A, B> f);

    public abstract <B> Either<E, B> flatMap(Function<A, Either<E, B>> f);

    public abstract A getOrElse(Supplier<A> defaultValue);

    public Either<E, A> orElse(Supplier<Either<E, A>> defaultValue) {
        return map(x -> this).getOrElse(defaultValue);
    }

    private static class Left<E, A> extends Either<E,A> {
        private final E value;

        private Left(E value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return String.format("Left(%S)", value);
        }

        @Override
        @SuppressWarnings("unchecked")
        public <B> Either<E, B> map(Function<A, B> f) {
            return (Either<E, B>)this;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <B> Either<E, B> flatMap(Function<A, Either<E, B>> f) {
            return (Either<E, B>)this;
        }

        @Override
        public  A getOrElse(Supplier<A> defaultValue) {
            return defaultValue.get();
        }
    }

    private static class Right<E, A> extends Either<E,A> {
        private final A value;

        private Right(A value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return String.format("Right(%s)", value);
        }

        @Override
        public <B> Either<E, B> map(Function<A, B> f) {
            return right(f.apply(value));
        }

        @Override
        public <B> Either<E, B> flatMap(Function<A, Either<E, B>> f) {
            return f.apply(value);
        }

        @Override
        public A getOrElse(Supplier<A> defaultValue) {
            return value;
        }
    }

    public static <E,A> Either<E,A> left(E value) {
        return new Left<>(value);
    }

    public static <E,A> Either<E,A> right(A value) {
        return new Right<>(value);
    }
}
