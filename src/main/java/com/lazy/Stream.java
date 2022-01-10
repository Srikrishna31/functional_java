package com.lazy;

import com.functional.TailCall;
import com.functional.Tuple;
import com.util.Result;
import static com.util.Result.failure;
import static com.util.Result.success;
import static com.util.Result.empty;

import com.util.List;
import static com.util.List.list;

import com.functional.Function;
import com.functional.TailCall;
import static com.functional.TailCall.ret;
import static com.functional.TailCall.sus;

import java.util.function.Supplier;

/**
 * This class is a counterpart to list class. Whereas the list class contains
 * values which are already evaluated, this class contains values which are as
 * yet unevaluated, when the stream is constructed. When the head value is queried
 * for the first time, the value is then evaluated. For this reason, this data structure
 * can encapsulate infinite values, which are as yet unevaluated, and are evaluated
 * on demand. Streams are also called lazy lists for the same reason.
 * The implementation is similar to List class, in that this class also contains
 * two subclasses namely : Empty, Cons.
 * Empty class denotes empty stream, and throws exception when head or tail is
 * called on it.
 * Cons class represents a list with unevaluated head and tail elements.
 * Some of the functions can lead to non-terminating prgrams, if applied to infinite
 * streams.
 * @param <A>
 */
public abstract class Stream<A> {
    private static Stream EMPTY = new Empty();

    /**
     * Evaluate and return the head element of the stream.
     * @return the head element of the stream.
     */
    public abstract Tuple<A, Stream<A>>  head();

    /**
     * @return the tail of the stream.
     */
    public abstract Stream<A> tail();

    /**
     * @return true if the stream is empty, false otherwise.
     */
    public abstract boolean isEmpty();

    /**
     * This function returns the value wrapped in a Result object, so that
     * the exception is also wrapped, and allows the client to handle it
     * as appropriate.
     * @return the result object which could be a success object, if the Stream
     * is a Cons object, otherwise, it is a failure object.
     */
    public Result<A> headOption() {
        return foldRight(Result::empty, a -> sa -> success(a));
    }

    /**
     * This method converts the stream to a list of values. This means, the
     * unevaluated values in the stream would be evaluated, and stored in a list.
     * For infinite streams, this program runs forever (within the memory
     * constraints) ofcourse.
     * @return the list object containing the values evaluated from the Stream.
     */
    public List<A> toList() {
        class ListHelper {
            TailCall<List<A>> go(List<A> acc, Stream<A> ss) {
                return ss.isEmpty() ? ret(acc) : sus(() -> go(acc.cons(ss.head()._1), ss.tail()));
            }
        }

        return new ListHelper().go(list(), this).eval().reverse();
    }

    /**
     * This function drops the first n values from the stream, and returns the
     * remaining values of the stream.
     * @param n : The number of values to skip/drop.
     * @return the remaining tail of the elements from the stream.
     */
    public Stream<A> drop(int n) {
        //Drop cannot reuse the implementation of dropWhile, since the predicate
        //cannot get the current index value of the element.
        class DropHelper {
            TailCall<Stream<A>> go(Stream<A> ss, int n) {
                return n > 0 ? sus(() -> go(ss.tail(), n -1)) : ret(ss);
            }
        }

        return new DropHelper().go(this, n).eval();
    }

    /**
     * This function drops the elements from the stream as long as the provided
     * predicate returns true for the elements evaulated.
     * @param p : The predicate which returns true / false for each evaluated
     *          element in the stream.
     * @return the remaining stream after dropping the initial elements.
     */
    public Stream<A> dropWhile(Function<A, Boolean> p) {
        class DropHelper{
            TailCall<Stream<A>> go(Stream<A> ss) {
                return ss.isEmpty() ||  p.apply(ss.head()._1) ?  sus(() -> go(ss.tail())) : ret(ss);
            }
        }

        return new DropHelper().go(this).eval();
    }

    /**
     * This function takes only the first n elements in the stream, and then
     * returns the empty object, although the original stream might have more
     * items.
     * @param n : The number of elements to take from the stream.
     * @return The truncated Stream object.
     */
    public abstract Stream<A> take(int n);

    /**
     * This function takes a predicate, and takes the elements from the stream
     * as long as the predicate returns true.
     * @param p : The predicate to be applied to each element of the stream.
     * @return The truncated Stream object, based on the provided predicate.
     */
    public Stream<A> takeWhile(Function<A, Boolean> p) {
        return foldRight(Stream::empty, a -> sa -> p.apply(a) ? cons(() -> a, sa) : empty());
    }

    /**
     * This method evaluates the elements of the stream, until the predicate returns
     * true for an element.
     * @param p : The predicate which returns a boolean for each element of the stream.
     * @return true, if an element is found, false otherwise.
     */
    public boolean exists(Function<A, Boolean> p) {
        class ExistsHelper {
            TailCall<Boolean> go(Stream<A> ss) {
                return ss.isEmpty() || p.apply(ss.head()._1) ? ret(true) : sus(() -> go(ss.tail()));
            }
        }

        return new ExistsHelper().go(this).eval();
    }

    /**
     * A general purpose function which can be used to turn the stream into any other type.
     * This function operates on the stream from left to right, applying the accumulating operator
     * on the right, and the accumulating value on the left(for each element of the stream).
     * @param identity : The identity of the operation. This will be returned if the
     *                 input stream is empty.
     * @param f : Accumulating function, which is a curried function, which accepts a parameter of type A, and
     *          returns a function that accepts a parameter of Supplier of B and returns a B.
     * @param <B> : The type of reduction element.
     * @return the reduced type object.
     *
     * Note that it may not be possible to do a foldLeft on the stream, since it inverts the direction of operation
     * and would need the stream to be reversed, and would not complete on infinite streams.
     * Also note that, the foldRight implementation is not stack safe, meaning that, it might throw
     * StackOverflowException for streams with elements larger than 10000 elements. For more
     * information, refer here: https://github.com/fpinjava/fpinjava/issues/12
     */
    public abstract <B> B foldRight(Supplier<B> identity, Function<A, Function<Supplier<B>, B>> f);

    /**
     * This function turns the stream of type A into a stream of type B.
     * @param f : The function which maps a type from A to B.
     * @param <B> : Type parameter of the return type of the function.
     * @return the stream of type B.
     */
    public <B> Stream<B> map(Function<A, B> f) {
        return foldRight(Stream::empty, a -> acc -> cons(() -> f.apply(a), acc));
    }

    /**
     * This function filters (removes), the elements from the stream, as long as
     * the given predicate is false for a particular element, and allows only those
     * elements in the stream for which the predicate returns true.
     * @param p : The predicate which returns a boolean for each element.
     * @return the filtered stream, which contains elements that satisfy the given
     * predicate.
     */
    public Stream<A> filter(Function<A, Boolean> p) {
        var stream = dropWhile(x -> !p.apply(x));

        return stream.isEmpty() ? stream : cons(() -> stream.head()._1, () -> stream.tail().filter(p));
    }

    /**
     * This function is equivalent to concat on lists. This one returns the elements
     * of the other stream, once the elements from this stream are exhausted.
     * @param that : The other stream, that needs to be added to the end of this stream.
     * @return a stream that contains both the streams.
     */
    public Stream<A> append(Supplier<Stream<A>> that) {
        return foldRight(that, a -> acc -> cons(() -> a, acc));
    }

    /**
     * This function is a generalization of map. The input function here returns a stream
     * of elements, rather than a single element, for each application. The flatMap
     * then flattens the stream of streams into a single stream. The runtime of this function
     * could be O(n2), since it needs to traverse the stream for each invocation.
     * @param f : The function which produces a Stream<B> for each element of Stream<A>.
     * @param <B> : The type parameter of mapped result.
     * @return the stream of objects of type B.
     */
    public <B> Stream<B> flatMap(Function<A, Stream<B>> f) {
        return foldRight(Stream::empty, a -> acc -> f.apply(a).append(acc));
    }

    /**
     * Find a particular element, which is determined by the predicate provided.
     * @param p : The predicate which returns a boolean for each element applied,
     *          returning true if the element matches the criteria, false otherwise.
     * @return the element if found, otherwise an empty.
     */
    public Result<A> find(Function<A, Boolean> p) {
        return filter(p).headOption();
    }

    /**
     * This function creates a stream of infinite repetitions of the given
     * element.
     * @param a : The element which needs to be repeated.
     * @param <A> : Type parameter of the element to be repeeated.
     * @return the stream of repeating elements of a.
     */
    public static <A> Stream<A> repeat(A a) {
        return cons(() -> a, () -> repeat(a));
    }

    /**
     * This method generalizes from and repeat methods, by taking a seed element
     * which is used as the first value, and applying the supplied function to
     * generate the subsequent values.
     * @param seed : The starting element.
     * @param f : Function which takes the current value, and produces the next
     *          element.
     * @param <A> : Type paramter of the elements.
     * @return the stream of generated elements.
     */
    public static <A> Stream<A> iterate(A seed, Function<A, A> f) {
        return cons(() -> seed, () -> iterate(f.apply(seed), f));
    }

    /**
     * This overload takes a supplier of the seed value, and then applies the
     * provided function to generate the subsequent values.
     * @param seed : the supplier providing the starter element.
     * @param f : The function which takes the current value, and produces the
     *          next element.
     * @param <A> : Type parameter of the elements.
     * @return the stream of generated elements.
     */
    public static <A> Stream<A> iterate(Supplier<A> seed, Function<A, A> f) {
        return cons(seed, () -> iterate(f.apply(seed.get()), f));
    }

    /**
     * A convenience function to build a list of values, which are obtained by
     * repeatedly applying the given supplier for the specified number of times.
     * @param n : The number of times to apply the supplier function.
     * @param f : The supplier function which provides the next value to be stored in the stream.
     * @param <A> : Type parameter of the elements in the list.
     * @return the Stream containing n elements, which are generated by repeatedly calling the supplier function.
     */
    public static <A> Stream<A> fill(int n, Supplier<A> f) {
        class FillHelper {
            TailCall<Stream<A>> go(Stream<A> acc, int n) {
                return n > 0 ? sus(() -> go(cons(f, () -> acc), n - 1)) : ret(acc);
            }
        }
        //Below implementation, although it follows the types, is not correct,
        //since it evaluates the given supplier function only once. Whereas, the
        //expectation for fill is that the function be evaluated n number of times.
        //return repeat(f.get()).take(n);

        return new FillHelper().go(Stream.empty(), n).eval();
    }
    /**
     * This is the most general form of the iterate/repeat method, which takes
     * a seed element, and a generating function, which provides the seed for
     * generating the subsequent element, and also the next element. Getting
     * and empty result will mark the end of the stream.
     * @param z : The starting value of the stream.
     * @param f : Function which takes the current seed, and returns a result
     *          producing a tuple of seed, and next element.
     * @param <A> : The type parameter of the elements.
     * @param <S> : The type parameter of the seed element.
     * @return the stream of elements of type A.
     */
    public static <A, S> Stream<A> unfold(S z, Function<S, Result<Tuple<A, S>>> f) {
        return f.apply(z).map(r -> Stream.cons(() -> r._1, () -> unfold(r._2, f))).getOrElse(empty());
    }

    private Stream() {}

    private static class Empty<A> extends Stream<A> {
        @Override
        public Stream<A> tail() {
            throw new IllegalStateException("tail called on empty stream");
        }

        @Override
        public Tuple<A, Stream<A>> head() {
            throw new IllegalStateException("head called on empty stream");
        }

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public Stream<A>  take(int n) {
            return this;
        }

        @Override
        public <B> B foldRight(Supplier<B> identity, Function<A, Function<Supplier<B>, B>> f) {
            return identity.get();
        }
    }

    private static class Cons<A> extends Stream<A> {
        private final Supplier<A> head;
        private Result<A> h;
        private final Supplier<Stream<A>> tail;
        private Stream<A> t;

        private Cons(Supplier<A> h, Supplier<Stream<A>> t) {
            head = h;
            tail = t;
            this.h = Result.empty();
        }

        private Cons(A h, Supplier<Stream<A>> t) {
            head = () -> h;
            tail = t;
            this.h = Result.success(h);
        }

        @Override
        public Tuple<A, Stream<A>> head() {
            A a = h.getOrElse(head.get());

            return h.isEmpty() ? Tuple.create(a, new Cons<>(a, tail)) : Tuple.create(a, this);
        }

        @Override
        public Stream<A> tail() {
            if (t == null) {
                t = tail.get();
            }
            return t;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public Stream<A> take(int n) {
            return n <= 0 ? empty() : cons(head, () -> tail().take(n - 1));
        }

        @Override
        public <B> B foldRight(Supplier<B> identity, Function<A, Function<Supplier<B>, B>> f) {
            //Its really a challenge to implement a stack safe version of foldRight,
            //or its next to impossible.
            return f.apply(head()._1).apply(() -> tail().foldRight(identity, f));
        }
    }

    /**
     * A convenience method to prepend an element to an existing stream, and return
     * a new stream.
     * @param hd : The unevaluated head element.
     * @param tl : The tail of the stream
     * @param <A> : Type parameter of the elements to be contained in the Stream.
     * @return a new stream with the element prepended.
     */
    public static <A> Stream<A> cons(Supplier<A> hd, Stream<A> tl) {
        return new Cons<>(hd, () -> tl);
    }

    public static <A> Stream<A> cons(Supplier<A> hd, Supplier<Stream<A>> tl) {
        return new Cons<>(hd, tl);
    }

    /**
     * Method to create an empty stream.
     * @param <A> : Type parameter to which the empty stream must conform to.
     * @return the empty stream of the provided type parameter.
     */
    @SuppressWarnings("unchecked")
    public static <A> Stream<A> empty() {
        return EMPTY;
    }

    /**
     * A convenience method to generate a sequence of numbers starting from a given
     * number.
     * @param i : The starting value.
     * @return the stream of contiguous integers from the starting value.
     */
    public static Stream<Integer> from (int i) {
        return unfold(i, j -> Result.success(Tuple.create(j, j + 1)));
    }

    @SafeVarargs
    public static <A> Stream<A> of(A... array) {
        return of(list(array));
    }

    public static <A> Stream<A> of(List<A> list) {
        return list.isEmpty() ? Stream.empty() : cons(list::head, () -> of(list.tail()));
    }

    @SafeVarargs
    public static <A> Stream<A> stream(A... array) {
        return of(list(array));
    }
}
