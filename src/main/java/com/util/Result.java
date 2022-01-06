package com.util;

import com.functional.Effect;
import com.functional.Function;

import java.util.function.Supplier;
import java.io.Serializable;

/**
 * This class handles the results of a computation, and allows binding further
 * computations on those results.
 * It provides three subclasses: Success, Failure, and Empty which encapsulate the
 * corresponding cases. The Failure class encapsulates an exception, which can
 * capture the entire error that could have occurred in a computation. A failure
 * message (which could be a string) is also stored in an IllegalStateException.
 * Success class encapsulates the result of successful computation, which can
 * then further be combined with other computations.
 * Empty class encapsulates the absence of data, which is not exactly a failure,
 * and it acts akin to Option.none().
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
     * @param success : The effect to be applied if the value is a success object.
     */
    public abstract void forEach(Effect<T> success);

    /**
     * This method applies the effect to the success value, and throws an
     * exception, stored in the failure object and does nothing for an empty
     * object.
     * @param ef : The effect to be applied to the success value.
     */
    public abstract void forEachOrThrow(Effect<T> ef);

    /**
     * This function applies the effect to the successful value, and if the object is
     * of failure it will return a Success<RuntimeException>.
     * If the value is Success or Empty, then an Empty object is returned.
     * @param ef : The effect to be applied to the successful value of the result.
     * @return Empty if the value is Success / Empty, Success<RuntimeException> otherwise.
     */
    public abstract Result<RuntimeException> forEachOrException(Effect<T> ef);


    public T getOrElse(final T defaultValue) {
        return getOrElse(() -> defaultValue);
    }

    /**
     * A method which returns the underlying value if the object is of type
     * Success, otherwise, returns the value returned by the Supplier.
     * The parameter is a Supplier to facilitate lazy evaluation, which happens
     * only when the object is of None type.
     * @param defaultValue : The supplier function which returns a default value.
     * @return the underlying value or the supplied default value as the case may be.
     */
    public abstract T getOrElse(final  Supplier<T> defaultValue);

    /**
     * This method transforms a result of type Result<T> to a result of type Result<U>,
     * by applying the given function to the element of the Result.
     * If the Result object is Empty or Failure, then this method is a no-op.
     * @param f : The function to be applied to each element of the list.
     * @param <U> : The type into which the function maps the element.
     * @return the transformed result containing the element of type U.
     */
    public abstract <U> Result<U> map(Function<T, U> f);

    /**
     * This function is a generalization of map. The parameter function here returns a result
     * of element, rather than a raw value. The flatMap then flattens the result into a single
     * result container. If the Result object is Empty or Failure, then this method is a no-op.
     * @param f : The function which produces a Result<U> for the element of Result<T>.
     * @param <U> : The type parameter of mapped result.
     * @return the result of object of type B.
     */
    public abstract <U> Result<U> flatMap(Function<T, Result<U>> f);


    /**
     * This function applies a predicate to the element and returns the Success
     * object, if the containing element matches the criterion. Otherwise, it
     * returns a Failure object.
     * @param p : The predicate to be applied to the element in Success object.
     * @return the Success object if predicate returns true, Failure otherwise.
     */
    public Result<T> filter(Function<T, Boolean> p) {
        return filter(p, "Condition not matched");
    }

    /**
     * This function applies a predicate to the element and returns the Success
     * object, if the containing element matches the criterion. Otherwise, it
     * returns a Failure object, with the message provided.
     * @param p : The predicate to be applied to the element in Success object.
     * @return the Success object if predicate returns true, Failure otherwise.
     */
    public Result<T> filter(Function<T, Boolean> p, String message) {
        return flatMap(t -> p.apply(t) ? this: failure(message));
    }

    /**
     * This function returns true if the object is of Success type, and the
     * predicate returns true, false otherwise.
     * @param p : The predicate to be applied to the value in success object.
     * @return true if the predicate returns true for the value in Success
     * object, false otherwise.
     */
    public boolean exists(Function<T, Boolean> p) {
        return map(p).getOrElse(false);
    }

    /**
     * This is an alias to exists function.
     * @param p
     * @return
     */
    public boolean forAll(Function<T, Boolean> p) {
        return exists(p);
    }

    /**
     * This function maps the failure object, into another failure object,
     * using the string provided. This function is a noop for Success and Empty
     * objects.
     * @param e : The message which needs to be encapsulated in the new object.
     * @return the transformed failure object if the original object was a
     * failure object, otherwise this.
     */
    public abstract Result<T> mapFailure(String e);

    /**
     * This function maps the failure object, into another failure object,
     * using the exception provided. This function is a noop for Success and Empty
     * objects.
     * @param e : The exception which needs to be encapsulated in the new object.
     * @return the transformed failure object if the original object was a
     * failure object, otherwise this.
     */
    public abstract Result<T> mapFailure(String s, Exception e);

    /**
     * This function maps the empty object, into failure object,
     * using the string provided. This function is a noop for Success and Failure
     * objects.
     * @param e : The message which needs to be encapsulated in the new object.
     * @return the transformed failure object if the original object was a
     * empty object, otherwise this.
     */
    public abstract Result<T> mapEmpty(String e);


    /**
     * This overload maps the empty result to a failure object. This function is a noop
     * for Success and Failure objects.
     * @return the transformed failure object if the original object was an empty
     * object, otherwise this.
     */
    public abstract Result<T> mapEmpty();

    /**
     * This function returns the other object if this object is not a success
     * object. Otherwise it returns this object.
     * @param defaultValue : The supplier function, which will be evaluated in
     *                     case this object is not a success object.
     * @return this object if it is a Success object, otherwise the default value.
     */
    public Result<T> orElse(Supplier<Result<T>> defaultValue) {
        return map(x -> this).getOrElse(defaultValue);
    }

    public abstract boolean isSuccess();

    public abstract boolean isEmpty();

    public abstract boolean isFailure();

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

    @SuppressWarnings("unchecked")
    public static <T> Result<T> empty() {
        return empty;
    }

    public static <T> Result<T> of(T value) {
        return of(value, "Null value");
    }

    public static <T> Result<T> of(T value, String message) {
        return value == null ? failure(message) : success(value);
    }

    /**
     * A convenience funtion to turn the result of a computation into a result object.
     * If the function returns true, then the supplied value is encapsulated into a
     * result object, otherwise an empty object is returned. If an exception is throwsn
     * during the evaluation of the function, then a failure is returned.
     * @param p : Predicate to be applied to value.
     * @param value : The value which needs to be encapsulated.
     * @param message : The Message which should be used for the failure case.
     * @param <T> : Type parameter of the value argument.
     * @return the Result object.
     */
    public static <T> Result<T> of(Function<T, Boolean> p, T value, String message) {
        try {
            return p.apply(value) ? success(value) : empty();
        } catch (Exception e) {
            return failure(new IllegalStateException(message, e));
        }
    }

    public static <T> Result<T> of(Function<T, Boolean> p, T value) {
        return of(p, value, String.format("Exception while evaluating predicate: %s", value));
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


    /**
     * This method transforms a function operating on A -> B into a function operating
     * on Result<A> to Result<B>.
     * @param f : The function which transforms an A into a B.
     * @param <A> : Type parameter of input.
     * @param <B> : Type parameter of output.
     * @return : The function accepting an object of type Result<A> and returning
     * an object of Result<B>.
     */
    public static <A, B> Function<Result<A>, Result<B>> lift(Function<A, B> f) {
        return a -> {
            try {
                return a.map(f);
            } catch (Exception e) {
                return failure(e);
            }
        };
    }

    public static <A,B,C> Function<Result<A>, Function<Result<B>, Result<C>>> lift2(Function<A, Function<B, C>> f) {
        return a -> b -> a.map(f).flatMap(b::map);
    }

    public static <A,B,C,D> Function<Result<A>, Function<Result<B>, Function<Result<C>, Result<D>>>> lift3(
            Function<A, Function<B, Function<C, D>>> f) {
        return a -> b -> c -> a.map(f).flatMap(b::map).flatMap(c::map);
    }

    public static <A, B, C> Result<C> map2(Result<A> a, Result<B> b, Function<A, Function<B, C>> f) {
        return lift2(f).apply(a).apply(b);
    }

    public <U> U fold(U identity, Function<U, Function<T, U>> f) {
        return map(v -> f.apply(identity).apply(v)).getOrElse(identity);
    }

    /**
     * This function applies the given effect to the Result, if it is a success.
     * If the object is a failure, this function wraps the message in another
     * result object and returns it. For Success and Empty objects, this function
     * returns empty.
     * @param f: The effect to be applied to the success object.
     * @return the Result object, which contains the failure message if the object
     * is a failure, empty otherwise.
     */
    public abstract Result<String> forEachOrFail(Effect<T> f);

    private static class Success<T> extends Result<T> {
        private final T value;

        private Success(T t) {
            value = t;
        }

        /**
         * Success implements bind, by applying the success effect to the value.
         * @param success : function to apply on successful computation.
         */
        @Override
        public void forEach(Effect<T> success) {
            success.apply(value);
        }

        @Override
        public void forEachOrThrow(Effect<T> f) {
            forEach(f);
        }

        @Override
        public Result<RuntimeException> forEachOrException(Effect<T> f) {
            forEach(f);
            return empty();
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

        @Override
        public Result<T> mapFailure(String s) {
            return this;
        }

        @Override
        public Result<T> mapEmpty(String e) {
            return  this;
        }

        @Override
        public Result<T> mapEmpty() { return this; }

        @Override
        public Result<T> mapFailure(String s, Exception e) {
            return this;
        }

        @Override
        public boolean isSuccess() {
            return true;
        }

        @Override
        public boolean isFailure() {
            return false;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public Result<String> forEachOrFail(Effect<T> ef) {
            ef.apply(value);

            return empty();
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

        @Override
        public Result<T> mapFailure(String s) {
            return mapFailure(s, error);
        }

        @Override
        public Result<T> mapFailure(String s, Exception e) {
            return failure(new IllegalStateException(s, e));
        }

        @Override
        public Result<T> mapEmpty(String s) { return this; }

        @Override
        public Result<T> mapEmpty() { return this; }

        @Override
        public void forEachOrThrow(Effect<T> ef) {
            throw error;
        }

        @Override
        public Result<RuntimeException> forEachOrException(Effect<T> f) {
            return success(error);
        }

        @Override
        public boolean isFailure() {
            return true;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public Result<String> forEachOrFail(Effect<T> ef) {
            return success(error.getMessage());
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
        public void forEach(Effect<T> success) {  }

        @Override
        public <U> Result<U> flatMap(Function<T, Result<U>> f) {
            return empty();
        }

        @Override
        public <U> Result<U> map(Function<T, U> f) {
            return empty();
        }

        @Override
        public Result<T> mapFailure(String s) {
            return this;
        }

        @Override
        public Result<T> mapFailure(String s, Exception e) {
            return this;
        }

        @Override
        public Result<T> mapEmpty(String s) {
            return failure(new IllegalStateException(s));
        }

        @Override
        public Result<T> mapEmpty() { return mapEmpty("Empty object"); }

        @Override
        public void forEachOrThrow(Effect<T> ef) { }

        @Override
        public Result<RuntimeException> forEachOrException(Effect<T> f) {
            return empty();
        }

        @Override
        public boolean isSuccess() {
            return false;
        }

        @Override
        public boolean isFailure() {
            return false;
        }

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public Result<String> forEachOrFail(Effect<T> ef) {
            return empty();
        }
    }
}
