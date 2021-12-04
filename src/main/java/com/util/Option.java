package com.util;

import java.util.function.Supplier;
import com.functional.Function;

/**
 * A facility for representing absent values in the computation. This relieves
 * programmers from either throwing exceptions or using special values like -1 or 0
 * to denote the absence of a value.
 * In short, this class helps to turn partial functions into total functions,
 * that make them composable.
 * It consists of two subclasses: Some, and None.
 * Some class represents the case when a value is present.
 * None class represents the case when a value is absent.
 * The implementation closely parallels that of a List.
 *  @param <A> : The type parameter which is being encapsulated.
 */
public abstract class Option<A> {
    /**
     * Store a signleton None instance, which can be used to represent the
     * absence of a value for all type parameters.
     */
    @SuppressWarnings("rawtypes")
    private static final Option none = new None();

    /**
     * Prevent clients from extending this class.
     */
    private Option() {}

    public abstract A getOrThrow();

    /**
     * A method which returns the underlying value if the object is of type
     * Some, other wise, returns the value returned by the Supplier.
     * The parameter is a Supplier to facilitate lazy evaluation, which happens
     * only when the object is of None type.
     * @param defaultValue : The supplier function which returns a default value.
     * @return the underlying value or the supplied default value as the case may be.
     */
    public abstract A getOrElse(Supplier<A> defaultValue);

    /**
     * A function to transform the Option<A> into Option<B>.
     * @param f : function which turns a value of type A to value of type B.
     * @param <B> : Type parameter of the resultant value.
     * @return the transformed option.
     */
    public abstract <B> Option<B> map(Function<A, B> f);

    /**
     *
     * @param f
     * @param <B>
     * @return
     */
    public <B> Option<B> flatMap(Function<A, Option<B>> f) {
        return map(f).getOrElse(Option::none);
    }

    public Option<A> filter(Function<A, Boolean> p) {
        return flatMap(x -> p.apply(x) ? this : none());
    }

    public Option<A> orElse(Supplier<Option<A>> defaultValue) {
        return map(x -> this).getOrElse(defaultValue);
    }

    @Override
    public abstract String toString();

    private static class None<A> extends Option<A> {
        private None() {}

        @Override
        public A getOrThrow() {
            throw new IllegalStateException("get called on None");
        }

        @Override
        public String toString() {
            return "None";
        }

        @Override
        public A getOrElse(Supplier<A> defaultValue) {
            return defaultValue.get();
        }

        @Override
        public <B> Option<B> map(Function<A, B> f) {
            return none();
        }
    }

    private static class Some<A> extends Option<A> {
        private final A value;

        private Some(A a) {
            value = a;
        }

        @Override
        public A getOrThrow() {
            return value;
        }

        @Override
        public String toString() {
            return String.format("Some(%s)", value);
        }

        @Override
        public A getOrElse(Supplier<A> defaultValue) {
            return value;
        }

        @Override
        public <B> Option<B> map(Function<A, B> f) {
            return some(f.apply(value));
        }
    }

    /**
     * A factory method to wrap a value into an Option class.
     * @param a : The value to be wrapped.
     * @param <A> : Type parameter of the value to be wrapped.
     * @return the wrapped object.
     */
    public static <A> Option<A> some(A a) {
        return new Some<>(a);
    }

    /**
     * A factory method to retrieve the none object, which is a universal
     * empty object, for all different type parameters.
     * @param <A>: The type parameter on which to return a none object.
     * @return the none reference object to the type parameter.
     */
    @SuppressWarnings("unchecked")
    public static <A> Option<A> none() {
        return none;
    }
}
