package com.io;

import com.functional.Function;
import com.functional.Nothing;

import com.lazy.Stream;
import com.util.List;

import java.util.function.Supplier;

/**
 * A utility interface to define functional
 * input and output.
 * @param <A>
 */
public interface IO<A> {
    A run();

    default IO<A> add(IO<A> io) {
        return () -> {
            run();
            return io.run();
        };
    }

    /**
     * Wrap the value a in the context of IO.
     * @param a : The value to be wrapped.
     * @param <A> : Type parameter of A.
     * @return the IO object.
     */
    static <A> IO<A> unit(A a) {
        return () -> a;
    }

    IO<Nothing> empty = () -> Nothing.instance;

    /**
     * Transform the IO of type A to IO of type B.
     * @param f : Function which creates a value of type B, from a value of type A.
     * @param <B> : type parameter of the result.
     * @return the transformed io object.
     */
    default <B> IO<B> map(Function<A, B> f) {
        return () -> f.apply(run());
    }

    /**
     * This function generalizes map. THe input function here returns an IO object
     * rather than simple transformation of the element.
     * @param f : The function which returns an IO object of type B, accepting a
     *          value of type A.
     * @param <B>: Type parameter of the result.
     * @return the transformed io object.
     */
    default <B> IO<B> flatMap(Function<A, IO<B>> f) {

        return () -> f.apply(run()).run();
        // although below statement is a valid implementation, following the types,
        //the flatMap function is only composing the computations, and hence,
        // it should not explicitly call the run function here. This is the
        //reason why, the implementation explicitly returns a program(function),
        //that wraps the run call, so that the effect is applied when the run is
        //explicitly called.
        //return f.apply(run());
    }

    /**
     * This is a convenience function to map two IO types into a third IO type.
     * @param aio : IO object that could produce a value of type A.
     * @param bio : IO object that could produce a value of type B.
     * @param f : A curried function, that accepts the values of A and B, and
     *          returns a value of type C.
     * @param <A> : Type parameter of first argument.
     * @param <B> : Type parameter of second argument.
     * @param <C> : Type parameter of the result.
     * @return the mapped IO object of type C.
     */
    static <A,B,C> IO<C> map2(IO<A> aio, IO<B> bio, Function<A, Function<B, C>> f) {
        return aio.flatMap(a -> bio.map(b -> f.apply(a).apply(b)));
        //return () -> f.apply(aio.run()).apply(bio.run());
    }

    /**
     * This function repeats the provided IO function/object for n times.
     * @param n : The number of times to repeatedly evaluate the IO function.
     * @param io : The IO function/object which can produce a value of type A.
     * @param <A> : The value type produced by the IO function.
     * @return the IO object of list of values that have been generated after
     * the application of the IO for n times.
     */
    static <A> IO<List<A>> repeat(int n, IO<A> io) {
        return Stream.fill(n, () -> io)
                .foldRight(() -> unit(List.list()), v -> acc -> map2(v, acc.get(), i -> ios -> ios.cons(i)));
    }

    /**
     * A convenience function to turn an IO function of type A into an IO function
     * of type B. This function is different from map in that, this function
     * accepts a pre-calculated value of type B, and wraps that into an IO context.
     * @param a : The IO function producing a value of type A.
     * @param b : The value of type B, which needs to be wrapped in IO.
     * @param <A> : Type parameter A.
     * @param <B> : Type parameter B.
     * @return the wrapped IO object, that would return the provided B value.
     */
    static <A, B> IO<B> as(IO<A> a, B b) {
        return a.map(ignore -> b);
    }

    /**
     * This function, evaluates the provided IO supplier, if the given boolean
     * is true. Otherwise, it returns an IO which contains the false value wrapped.
     * @param b : The boolean value indicating if sIoa should be evaluated or not.
     * @param sIoa : The supplier of IO object that returns a value of type A.
     * @param <A> : Type parameter A.
     * @return the IO object, wrapping a boolean.
     */
    static <A> IO<Boolean> when (boolean b, Supplier<IO<A>> sIoa) {
        return b ? as(sIoa.get(), true) : unit(false);
        //return sIoa.get().map(ignore -> b);
    }

    static IO<Nothing> ifElse(boolean b, IO<Nothing> io1, IO<Nothing> io2) {
        return b ? io1 : io2;
    }

    /**
     * Evaluate the stream of IO objects in a sequence, and return another IO
     * object holding nothing.
     * @param stream : The stream of IO operations to be evaluated.
     * @param <A> : The type parameter of the IO object.
     * @return The IO object holding nothing.
     */
    static <A> IO<Nothing> sequence(Stream<IO<A>> stream) {
        return forEach(stream, IO::skip);
    }

    /**
     * Evaluate an array of IO objects and return an IO<Nothing>.
     * @param array : Array containing the IO objects of type A.
     * @param <A> : Type parameter of the value
     * @return The IO object holding nothing.
     */
    @SafeVarargs
    static <A> IO<Nothing> sequence(IO<A>... array) {
        return sequence(Stream.of(array));
    }

    /**
     * Evaluate a list of IO objects and return an IO<Nothing>.
     * @param list : List containing the IO objects of type A.
     * @param <A> : Type parameter of the value.
     * @return The IO object holding nothing.
     */
    static <A> IO<Nothing> sequence(List<IO<A>> list) {
        return sequence(Stream.of(list));
    }

    /**
     * Ignore / donot evaluate the provided IO and return an IO
     * object of nothing.
     * @param a : The io object that should be ignored.
     * @param <A> : The type parameter of the value.
     * @return The IO object holding nothing.
     */
    static <A> IO<Nothing> skip(IO<A> a) {
        return as(a, Nothing.instance);
    }

    static <A, B> IO<B> fold(Stream<A> s, B z, Function<B, Function<A, IO<B>>> f) {
        return s.isEmpty() ? unit(z) : f.apply(z).apply(s.head()._1).flatMap(zz -> fold(s.tail(), zz, f));
    }

    static <A, B> IO<B> forever(IO<A> ioa) {
        Supplier<IO<B>> t = () -> forever(ioa);
        return ioa.flatMap(x -> t.get());
    }

    static <A> IO<Nothing> forEach(Stream<A> s, Function<A, IO<Nothing>> f) {
        return fold(s, Nothing.instance, n -> t -> skip(f.apply(t)));
    }

    static <A> IO<Nothing> doWhile(IO<A> ioa, Function<A, IO<Boolean>> f) {
        return ioa.flatMap(f).flatMap(ok -> ok ? doWhile(ioa, f) : empty);
    }
}
