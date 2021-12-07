package com.util;

import com.functional.Effect;
import com.functional.Function;

import java.util.function.Supplier;
import java.io.Serializable;

/**
 * This class handles the results of a computation, and allows to bind further
 * computations on those resutls.
 * It provides two subclasses: Success and Failure, which encapsulate the corresponding
 * cases. The Failure class encapsulates an exception, which can capture the entire
 * error that could have occurred in a computation. A failure message (which could be
 * a string) is also stored in an IllegalStateException.
 * The way to instantiate them is through static methods provided and not directly.
 */
public abstract class Result<T> implements Serializable {
    /**
     * Prevent the clients from extending this class.
     */
    private Result() {}

    @SuppressWarnings("rawtypes")
    private static Result empty = new Empty();

    /**
     * This method handles the effects to be applied to the Result object.
     */
    public abstract void bind(Effect<T> success, Effect<String> failure, Effect<String> empty);

    public T getOrElse(final T defaultValue) {
        return getOrElse(() -> defaultValue);
    }

    public abstract T getOrElse(final  Supplier<T> defaultValue);

    public abstract <U> Result<U> map(Function<T, U> f);

    public abstract <U> Result<U> flatMap(Function<T, Result<U>> f);

    public Result<T> filter(Function<T, Boolean> f) {
        return filter(f, "Condition not matched");
    }

    public Result<T> filter(Function<T, Boolean> f, String message) {
        return flatMap(t -> f.apply(t) ? this: failure(message));
    }

    public boolean exists(Function<T, Boolean> p) {
        return map(p).getOrElse(false);
    }

    public boolean forAll(Function<T, Boolean> p) {
        return exists(p);
    }

    public Result<T> orElse(Supplier<Result<T>> defaultValue) {
        return map(x -> this).getOrElse(defaultValue);
    }

    /**
     * This method returns a failure instance holding the message provided.
     * @param message : Error message describing the condition.
     * @param <T>: Type parameter which represent the Success type, although not used here.
     * @return a Failure<T> object.
     */
    public static <T> Result<T> failure(String message) {
        return new Failure<>(message);
    }

    public static <T> Result<T> failure(Exception e) {
        return new Failure<>(e);
    }

    public static <T> Result<T> failure(RuntimeException e) {
        return new Failure<>(e);
    }

    @SuppressWarnings("unchecked")
    public static <T> Result<T> empty() {
        return empty;
    }

    /**
     * This method returns a Success instance holding the value provided.
     * @param value: Value representing successful computation.
     * @param <T>: The type parameter of the success value.
     * @return a Success<T> object.
     */
    public static <T> Result<T> success(T value) {
        return new Success<>(value);
    }

    private static class Success<T> extends Result<T> {
        private final T value;

        private Success(T t) {
            value = t;
        }

        /**
         * Success implements bind, by applying the success effect to the value.
         * @param success : function to apply on successful computation.
         * @param failure : function to apply on failure, which is ignored here.
         */
        @Override
        public void bind(Effect<T> success, Effect<String> failure, Effect<String> empty) {
            success.apply(value);
        }

        @Override
        public T getOrElse(Supplier<T> defaultValue) {
            return value;
        }

        @Override
        public <U> Result<U> flatMap(Function<T, Result<U>> f) {
            try {
                return f.apply(value);
            } catch(Exception e) {
                return failure(e);
            }
        }

        @Override
        public <U> Result<U> map(Function<T, U> f) {
            try {
                return success(f.apply(value));
            } catch(Exception e) {
                return failure(e);
            }
        }
    }

    private static class Failure<T> extends Empty<T> {
        private final RuntimeException error;

        private Failure(String e) {
            super();
            error = new IllegalStateException(e);
        }

        private Failure(Exception e) {
            super();
            error = new IllegalStateException(e.getMessage(), e);
        }

        private Failure(RuntimeException e) {
            super();
            error = e;
        }
        /**
         * Failure implements bind, by applying the failure effect to the error message.
         * @param success : function to apply on successful computation, which is ignored here.
         * @param failure : function to apply on failure.
         */
        @Override
        public void bind(Effect<T> success, Effect<String> failure, Effect<String> empty) {
            failure.apply(error.getMessage());
        }

        @Override
        public <U> Result<U> flatMap(Function<T, Result<U>> f) {
            return failure(error);
        }

        @Override
        public <U> Result<U> map(Function<T, U> f) {
            return failure(error);
        }

        @Override
        public String toString() {
            return String.format("Failure(%s)", error.getMessage());
        }
    }

    private static class Empty<T> extends Result<T> {

        Empty() {
            super();
        }

        @Override
        public T getOrElse(Supplier<T> defaultValue) {
            return defaultValue.get();
        }

        @Override
        public String toString() {
            return "Empty()";
        }

        @Override
        public void bind(Effect<T> success, Effect<String> failure, Effect<String> empty) {
            empty.apply(toString());
        }

        @Override
        public <U> Result<U> flatMap(Function<T, Result<U>> f) {
            return empty();
        }

        @Override
        public <U> Result<U> map(Function<T, U> f) {
            return empty();
        }
    }
}
