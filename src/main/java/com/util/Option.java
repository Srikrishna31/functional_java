package com.util;

import java.util.Objects;
import java.util.function.Supplier;
import com.functional.Function;
import static com.util.List.list;

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
     * This function returns whether the underlying type is a Some type or a none type.
     * This function shouldn't be needed for typical functional programming techniques,
     * but is provided for covenience.
     * @return true if udnerlying object is some, false otherwise.
     */
    public abstract Boolean isSome();

    @Override
    public abstract boolean equals(Object o);

    @Override
    public abstract int hashCode();

    /**
     * This function is a generalized version of map. The function parameter
     * accepts a paramter of type A and returns a value of Option<B>.
     * @param f : Function accepting a value of type A and returning a value of Option<B>
     * @param <B> : Type paramter of return value.
     * @return the optional value possibly containing a value of type B.
     */
    public <B> Option<B> flatMap(Function<A, Option<B>> f) {
        return map(f).getOrElse(Option::none);
    }

    /**
     * This function applies a predicate to the containing value, and if the predicate
     * returns false, then returns a None, otherwise returns the original value as is.
     * @param p : A predicate function, which accepts a value of type A and returns a boolean.
     * @return the original object, if the predicate returns true, otherwise None.
     */
    public Option<A> filter(Function<A, Boolean> p) {
        return flatMap(x -> p.apply(x) ? this : none());
    }

    public Option<A> orElse(Supplier<Option<A>> defaultValue) {
        return map(x -> this).getOrElse(defaultValue);
    }

    /**
     * An adaptor function, which transforms a function that maps A to B into a function
     * that maps an Option<A> to Option<B>. If the supplied function throws an exception,
     * this function swallows it and returns a None object.
     * @param f : Function that maps an A into a B.
     * @param <B> : Type parameter of return value.
     * @return a function that maps an Option<A> to Option<B>.
     */
    public <B> Function<Option<A>, Option<B>> lift(Function<A, B> f) {
        return (Option<A> o) -> {
            try {
                return o.map(f);
            } catch (Exception e)  {
                return Option.none();
            }
        };
    }

    public <B,C> Option<C> map2(Option<B> that, Function<A, Function<B, C>> f) {
        return flatMap(a -> that.map(b -> f.apply(a).apply(b)));
    }


    /**
     * This function combines a List<Option<A>> into an Option<List<A>>.
     * It will be Some<List<A>> if all values in the original list were Some
     * instances, or a None<List<A>> otherwise.
     * @param list : List of Optionals of type A.
     * @param <A> : Type parameter of elements in Option.
     * @return the Optional List of elements of type A.
     */
    public static <A> Option<List<A>> sequence(List<Option<A>> list) {
        return traverse(list, x -> x);
    }

    /**
     * This function combines a List<A> into an Optional of List<B> by applying the
     * transform function to each element in the list. If all values return a Some
     * instance after the application of the function, then the result will be
     * Some<List<B>>, otherwise a None<List<B>> is returned.
     * @param l : the list of elements of type A.
     * @param f : The function which transforms an A into an Option<B>.
     * @param <A> : Type parameter of elements in the input list.
     * @param <B> : Type parameter of elements in the output list.
     * @return the optional instance of list of elements of type B.
     */
    public static <A, B> Option<List<B>> traverse(List<A> l, Function<A, Option<B>> f) {
        return l.foldRight(Option.some(List.list()), v -> acc -> f.apply(v).flatMap(b -> acc.map(rs -> rs.cons(b))));
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

        @Override
        public Boolean isSome() {
            return false;
        }

        @Override
        public boolean equals(Object o) {
            return this == o || o instanceof None;
        }

        @Override
        public int hashCode() {
            return 0;
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

        @Override
        public Boolean isSome() {
            return true;
        }

        @Override
        public boolean equals(Object o) {
            return this == o || (o instanceof Some && this.value.equals(((Some<?>)o).value));
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(value);
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
