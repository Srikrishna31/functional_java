package com.util;

import com.functional.TailCall;
import com.functional.Function;
import com.functional.Tuple;

import static com.util.Result.empty;
import static com.util.Result.success;
import static com.util.Result.failure;

import static com.functional.TailCall.sus;
import static com.functional.TailCall.ret;

import java.util.Collection;

/**
 * A functional list that supports all the functional operations on a list.
 * The list class is implemented as an abstract class. The List class contains
 * two private static subclasses to represent the two possible forms a List can
 * take: Nil for an empty list, and Cons for a non-empty one.-
 */
public abstract class List<A> {
    /**
     * Prevent the clients from extending this class.
     */
    private List() {}

    @SuppressWarnings("rawtypes")
    private static final List NIL = new Nil();

    /**
     * Returns the first or head element of the list.
     * Throws an exception if called on the Nil object.
     * @return the first element from the list.
     */
    public abstract A head();

    /**
     * Returns the first or head element of the list, wrapped in a Result object.
     * If the Object is Nil, then it is encapsulated in a Failure object.
     * @return the Result object holding the value or exception.
     */
    public abstract Result<A> headOption();

    /**
     * Returns the last element int he list, wrapped in a Result object.
     * If the list is Nil, an Empty object is returned.
     * @return a Result object, which could be a Success or an Empty object.
     */
    public Result<A> lastOption() {
        return foldLeft(empty(), acc -> Result::success);
    }

    /**
     * Returns the tail (or rest) of the list.
     * @return the remaining elements of the list.
     */
    public abstract List<A> tail();

    /**
     * @return true if the list is empty or false otherwise.
     */
    public abstract boolean isEmpty();

    /**
     * Returns a new list with the head element set to the
     * element passed in.
     * @param a : The element to be set as head.
     * @return the list with the head element set to a.
     */
    public abstract List<A> setHead(A a);

    @Override
    public String toString() {
        return String.format("[%sNIL]",
                foldLeft(new StringBuilder(), acc -> v -> acc.append(", ").append(v)));
    }

    /**
     * This is more general method compared to tail method - which removes just
     * the first element.
     * Drop method removes n elements from the list and returns the remaining
     * elements.
     * @param n : the number of elements to drop from the list.
     * @return the rmaining list after dropping n elements. If n > list size, this
     * method returns an empty list.
     */
    public List<A> drop(int n) {
        class DropHelper {
            TailCall<List<A>> go(List<A> as, int n) {
                return (n <= 0 || as.isEmpty()) ? ret(as) : sus(() -> go(as.tail(), n - 1));
            }
        }
        return new DropHelper().go(this, n).eval();
    }

    /**
     * This method drops elements from the list as long as a condition is met,
     * or the elements are exhausted.
     * @param p : The predicate that needs to be applied to each element.
     * @return the remaining list after dropping the elements.
     */
    public List<A> dropWhile(Function<A, Boolean> p) {
        class DropHelper {
            TailCall<List<A>> go(List<A> as) {
                return (!as.isEmpty() && p.apply(as.head())) ? sus(() -> go(as.tail())) : ret(as);
            }
        }

        return new DropHelper().go(this).eval();
    }

    /**
     * Concatenates two lists, and returns a combined list. This function traverses
     * the entire list1, and then links the last element of the first list to the
     * second one.
     * @param list1 : The first list to be concatenated.
     * @param list2 : The second list to be concatenated.
     * @param <A> : Type parameter of the lists to be concatenated.
     * @return : the combined list of list1 and list2.
     */
    public static <A> List<A> concat(List<A> list1, List<A> list2) {
        return list1.foldRight(list2, v -> acc -> acc.cons(v));
    }

    /**
     * A general purpose function which can be used to turn the list into any other type.
     * This function operates on the list from left to right, applying the accumulating operator
     * on the left, and the accumulating value on the right(for each element of the list).
     * @param identity : The identity of the operation. This will be returned if the
     *                 input list is empty.
     * @param f : Accumulating function, which is a curried function, which accepts a parameter of type B, and
     *          returns a function that accepts a parameter of type A and returns a B.
     * @param <B> : The type of reduction element.
     * @return the reduced type object.
     */
    public <B> B foldLeft(B identity, Function<B, Function<A, B>> f) {
        class FoldHelper {
            TailCall<B> go(List<A> ls, B acc) {
                return ls.isEmpty() ? ret(acc) : sus(() -> go(ls.tail(), f.apply(acc).apply(ls.head())));
            }
        }

        return new FoldHelper().go(this, identity).eval();
    }

    /**
     * This overload supports early termination of the fold, if the zero element matches
     * with the accumulator.
     * @param identity : The identity of the operation. This will be returned if the input
     *                 list is empty.
     * @param zeroElement : The absorbing element of the computation.
     * @param f : Accumulating function, which is a curried function, which accepts a parameter of type B, and
     *           returns a function that accepts a parameter of type A and returns a B.
     * @param <B> : The type of reduction element.
     * @return the reduced type object.
     */
    public <B> B foldLeft(B identity, B zeroElement, Function<B, Function<A, B>> f) {
        class FoldHelper {
            TailCall<B> go(List<A> ls, B acc) {
                return ls.isEmpty() || acc.equals(zeroElement) ? ret(acc) : sus(() -> go(ls.tail(),
                        f.apply(acc).apply(ls.head())));
            }
        }

        return new FoldHelper().go(this, identity).eval();
    }

    /**
     * This is a more general overload supports early termination of the fold based on
     * the predicate, which accepts both the accumulator(intermediate result) and the current
     * element. If it returns true, then the fold is terminated immediately. This
     * obviates the need to find a zero element, and also design any custom condition for
     * which the fold must be terminated early.
     * @param identity: The identity of the operation. This will be returned if the input
     *                 is empty.
     * @param p : The predicate which accepts the current accumulator and the current
     *          element and returns a boolean
     * @param f : Accumulating function, which is a curried function, which accepts a
     *          parameter of type B, and returns a function that accepts a parameter
     *          of type A and returns a B.
     * @param <B> : The type of reduction element.
     * @return the reduced element.
     */
    public <B> B foldLeft(B identity, Function<B, Function<A, Boolean>> p, Function<B, Function<A, B>> f) {
        class FoldHelper {
            TailCall<B> go(List<A> ls, B acc) {
                return ls.isEmpty() || p.apply(acc).apply(ls.head()) ? ret(acc) : sus(() -> go(ls.tail(),
                        f.apply(acc).apply(ls.head())));
            }
        }

        return new FoldHelper().go(this, identity).eval();
    }

    /**
     * Another general purpose function which can be used to turn the list into any other type.
     * This function operates on the list from right to left, applying the accumulating operator
     * on the right, and the accumulating value on the left(for each element of the list).
     * @param identity : The identity element of the operation. This will be returned if the
     *                 input list is empty.
     * @param f : Accumulating function that takes a parameter of type A, and returns a function
     *          that takes a parameter of type B and returns a B.
     * @param <B> : The type of reduction element.
     * @return the reduced type object.
     */
    public <B> B foldRight(B identity, Function<A, Function<B, B>> f) {
        return reverse().foldLeft(identity, acc -> v -> f.apply(v).apply(acc));
    }

    /**
     * This overload supports early termination of the fold, if the zero element matches
     * with the accumulator.
     * @param identity : The identity of the operation. This will be returned if the input
     *                 list is empty.
     * @param zeroElement : The absorbing element of the computation.
     * @param f : Accumulating function that takes a parameter of type A, and returns a function
     *          that takes a parameter of type B and returns a B.
     * @param <B> : The type of reduction element.
     * @return the reduced type object.
     */
    public <B> B foldRight(B identity, B zeroElement, Function<A, Function<B, B>> f) {
        return reverse().foldLeft(identity, zeroElement, acc -> v -> f.apply(v).apply(acc));
    }

    /**
     * This is a more general overload supports early termination of the fold based on
     * the predicate, which accepts both the accumulator(intermediate result) and the current
     * element. If it returns true, then the fold is terminated immediately. This
     * obviates the need to find a zero element, and also design any custom condition for
     * which the fold must be terminated early.
     * @param identity: The identity of the operation. This will be returned if the input
     *                 is empty.
     * @param p : The predicate which accepts the current accumulator and the current
     *          element and returns a boolean
     * @param f : Accumulating function, which is a curried function, which accepts a
     *          parameter of type A, and returns a function that accepts a parameter
     *          of type B and returns a B.
     * @param <B> : The type of reduction element.
     * @return the reduced element.
     */
    public <B> B foldRight(B identity, Function<B, Function<A, Boolean>> p, Function<A, Function<B, B>> f) {
        return reverse().foldLeft(identity, p, (Function<B, Function<A, B>>) acc -> v -> f.apply(v).apply(acc));
    }

    /**
     * Returns the list with last element removed. This function is currently
     * inefficient, since the running time is O(2N).
     * @return the list with last element removed.
     */
    public List<A> init() {
        return reverse().tail().reverse();
    }

    /**
     * @return Returns the number of elements in the list.
     */
    public abstract int length();

    /**
     * Reverses the order of the elements and returns a new list.
     * @return the list with elements in the reverse order.
     */
    public List<A> reverse() {
        return foldLeft(list(), acc -> v -> acc.cons(v));
    }

    /**
     * Implements the functional method cons, adding an element at the beginning
     * of a list.
     * @param a : The element that needs to be added at the beginning.
     * @return The list with the element added in the beginning.
     */
    public List<A> cons(A a) {
        return new Cons<>(a, this);
    }

    /**
     * This method transforms a list of type List<A> to a list of type List<B>,
     * by applying the given function to each element of the list. The runtime of
     * this function is O(2n), since there is a reversing of the list.
     * @param f : The function to be applied to each element of the list.
     * @param <B> : The type into which the function maps each element.
     * @return the transformed list containing the elements of type B.
     */
    public <B> List<B> map(Function<A, B> f) {
        return foldRight(list(), v -> acc -> acc.cons(f.apply(v)));
    }

    /**
     * This function applies a predicate to each element and returns the list of
     * elements that match the criterion. The runtime of this function is O(2n),
     * since there is a reversing of the list.
     * @param p : The predicate to be applied to each element of the list.
     * @return the list of values for which the predicate returned true.
     */
    public List<A> filter(Function<A, Boolean> p) {
        return foldLeft(List.<A>list(), acc -> v -> p.apply(v) ? acc.cons(v) : acc).reverse();
//        return foldRight(list(), v -> acc -> p.apply(v) ? acc.cons(v) : acc);
    }

    /**
     * This function is a generalization of map. The input function here returns a list
     * of elements, rather than a single element, for each application. The flatMap
     * then flattens the list of lists into a single list. The runtime of this function
     * could be O(n2), since it needs to traverse the list for each invocation.
     * @param f : The function which produces a List<B> for each element of List<A>.
     * @param <B> : The type parameter of mapped result.
     * @return the list of objects of type B.
     */
    public <B> List<B> flatMap(Function<A, List<B>> f) {
//        return flatten(map(f));
        return foldRight(list(), v -> acc -> concat(f.apply(v), acc));
    }

    /**
     * A convenience function to flatten a list of list of elements into a single
     * list. The runtime of this function could be O(n2), since it needs to
     * traverse the list for each invocation.
     * @param aas : List of list of a.
     * @param <A> : Type parameter of elements of inner list.
     * @return the flattened list of elements.
     */
    public static <A> List<A> flatten(List<List<A>> aas) {
        return aas.foldRight(list(), v -> acc -> concat(v, acc));

    }

    /**
     * A convenience function to turn the Java collection to functional List, which
     * then supports all the usual functional programming operations. This method runs
     * in O(N) time, since it loops over the collection.
     * @param ct : The collection which needs to be converted to a list.
     * @param <T> : Type parameter of the collection.
     * @return the List class holding the elements from collection.
     */
    public static <T> List<T> fromCollection(Collection<T> ct) {
        var list = List.<T>list();

        for(final T t : ct) {
            list.cons(t);
        }

        return list.reverse();
    }

    /**
     * A function which returns the first element that satisfies the given predicate.
     * @param p : The predicate which is applied for each element in the list.
     * @return the Result object encapsulating the element if the predicate returns
     * true for an element or return an empty object otherwise.
     */
    public Result<A> first(Function<A, Boolean> p) {
        class FirstHelper {
            TailCall<Result<A>> go(List<A> ls) {
                return ls.isEmpty() ? ret(Result.<A>failure("Empty list")) : p.apply(ls.head()) ?
                                            ret(Result.success(ls.head())) :
                                            sus(() -> go(ls.tail()));
            }
        }

        return new FirstHelper().go(this).eval();
    }

    /**
     * This function flattens a list of results into a result of list object. This
     * function will ignore failures and empty objects, and returns only the list
     * of successful objects.
     * @param lrs : The list of result objects of type A.
     * @param <A> : The type parameter of the encapsulated objects.
     * @return the result of list of encapsulated objects.
     */
    public static <A> Result<List<A>> flattenResult(List<Result<A>> lrs) {
        return lrs.foldRight(success(list()), v -> acc -> acc.flatMap(ls -> v.map(el -> ls.cons(el))));
    }

    /**
     * This function flattens a list of results into a result of list object. This
     * function returns a Success object, only if all the elements in the list are
     * success objects. Otherwise, it returns a failure object.
     * @param lrs : The list of result objects of type A.
     * @param <A> : The type parameter of the encapsulated objects.
     * @return the result of list of encapsulated objects if all are success objects,
     * or failure otherwise.
     */
    public static <A> Result<List<A>> sequence(List<Result<A>> lrs) {
        return traverse(lrs, x -> x);
        //return lrs.foldRight(success(list()), v -> acc -> Result.map2(v, acc, a -> b -> b.cons(a)));
    }

    /**
     * This is a more generic function,that applies a function to each element of the
     * list producing a result object. Finally, this method transforms the list of result
     * objects into a result of list objects.
     * @param ls : The list of elements to traverse.
     * @param f : The function that takes a parameter of type A, and returns a result object of type B.
     * @param <A> : Type parameter of input list.
     * @param <B> : Type parameter of the output of the function and the output result list objects.
     * @return the result of list of objects of type B.
     */
    public static <A,B> Result<List<B>> traverse(List<A> ls, Function<A, Result<B>> f) {
        return ls.foldRight(success(list()), v -> acc -> acc.flatMap(rs -> f.apply(v).map(u -> rs.cons(u))));
//        return ls.foldRight(success(list()), v -> acc -> Result.map2(f.apply(v), acc, a -> b -> b.cons(a)));
    }

    /**
     * This method combines the elements of two lists of different types to produce a new list, given
     * a function argument. This function runs for the shortest list length, and ignores the remaining
     * elements in the longer list.
     * @param as : The list of elements of type A.
     * @param bs : The list of elements of type B.
     * @param f : A curried function that accepts arguments of a, b and produces c.
     * @param <A> : Type parameter of elements in the first list.
     * @param <B> : Type parameter of elements in the second list.
     * @param <C> : Type parameter of elements in the result list.
     * @return the list of elements of type C, which are obtained by applying the function on the
     * pair of elements of input lists.
     */
    public static <A,B,C> List<C> zipWith(List<A> as, List<B> bs, Function<A, Function<B, C>> f) {
        class ZipHelper {
            TailCall<List<C>> go(List<A> as, List<B> bs, List<C> cs) {
                if (as.isEmpty() || bs.isEmpty()) {
                    return ret(cs);
                } else {
                    return go(as.tail(), bs.tail(), cs.cons(f.apply(as.head()).apply(bs.head())));
                }
            }
        }

        return new ZipHelper().go(as, bs, list()).eval().reverse();
    }

    /**
     * This function combines the elements of two lists of different types into a list of Tuple of a, b.
     * This function is similar to zipWith, except that, it simply joins the corresponding elements into
     * a tuple.
     * @param as : The list of elements of type A.
     * @param bs : The list of elements of type B.
     * @param <A> : Type parameter of elements in the first list.
     * @param <B> : Type parameter of elements in the second list.
     * @return the list of elements of Tuple<A, B>.
     */
    public static <A, B> List<Tuple<A, B>> zip(List<A> as, List<B> bs) {
        return zipWith(as, bs, a -> b -> Tuple.create(a, b));
    }

    /**
     * This function creates a cartesian product of two lists, and applies a function
     * that takes the arguments a, b and produces a c.
     * @param as : The list of elements of type A.
     * @param bs : The list of elements of type B.
     * @param f : A curried function that accepts arguments of a, b and produces c.
     * @param <A> Type parameter of elements in the first list.
     * @param <B> : Type parameter of elements in the second list.
     * @param <C> : Type parameter of elements in the result list.
     * @return the list of elements of type C, obtained by applying the function to each
     * pair of (a,b) values.
     */
    public static <A, B, C> List<C> productWith(List<A> as, List<B> bs, Function<A, Function<B, C>> f) {
        return as.flatMap(a -> bs.map(b -> f.apply(a).apply(b)));
    }

    /**
     * This function creates a cartesian product of two lists, and returns a list of pairs(tuple).
     * @param as : The list of elements of type A.
     * @param bs : The list of elements of type B.
     * @param <A> : Type parameter of elements in the first list.
     * @param <B> : Type parameter of elements in the second list.
     * @return the list of tuple of (a, b).
     */
    public static <A, B> List<Tuple<A,B>> product(List<A> as, List<B> bs) {
        return productWith(as, bs, a -> b -> Tuple.create(a, b));
    }

    /**
     * This function is opposite of zip. It separates the list of Tuples into a Tuple of Lists.
     * @param abs : list of tuple of a, b.
     * @param <A> : Type parameter of first element in the tuple.
     * @param <B> : Type parameter of second element in the tuple.
     * @return the tuple of lists of type A and type B.
     */
    public static <A, B> Tuple<List<A>, List<B>> unzip(List<Tuple<A, B>> abs) {
        return abs.foldRight(Tuple.create(list(), list()), v -> acc -> Tuple.create(acc._1.cons(v._1),
                acc._2.cons(v._2)));
    }

    /**
     * This is a generalization of unzip, which applies a function that takes a single element
     * and returns a tuple of b, c, which are then returned in two separate lists.
     * @param as : List of elements of type A.
     * @param f : A function that produces a tuple from a single value.
     * @param <A> : Type parameter of elements in the input list.
     * @param <B> : Type parameter of first element of the resultant tuple.
     * @param <C> : Type parameter of second element of the resultant tuple.
     * @return the Tuple of List<B> and List<C>.
     */
    public static <A, B, C> Tuple<List<B>, List<C>> unzipWith(List<A> as, Function<A, Tuple<B, C>> f) {
        return as.foldRight(Tuple.create(list(), list()), v -> acc -> {
            var t = f.apply(v);
            return Tuple.create(acc._1.cons(t._1), acc._2.cons(t._2));
        });
    }

    public Result<A> getAt(int index) {
        class GetHelper{
            TailCall<Result<A>> go(List<A> as, int index) {
                return index == 0 ? ret(success(as.head())) : sus(() -> go(as.tail(), index - 1));
            }
        }

        return index < 0 || index > length()  ? failure("Index out of bounds") :
                new GetHelper().go(this, index).eval();

    }

    private static class Nil<A> extends List<A> {
        private Nil() {}

        @Override
        public A head() {
            throw new IllegalStateException("head called on empty list");
        }

        @Override
        public List<A> tail() {
            throw new IllegalStateException("tail called on empty list");
        }

        @Override
        public boolean isEmpty() { return true; }

        @Override
        public List<A> setHead(A a) {
            throw new IllegalStateException("setHead called on empty list");
        }

        @Override
        public int length() { return 0; }

        @Override
        public Result<A> headOption() {
            return failure("head called on empty list.");
        }
    }

    private static class Cons<A> extends List<A> {
        private final A head;
        private final int length;
        private final List<A> tail;

        private Cons(A head, List<A> tail) {
            this.head = head;
            this.tail = tail;
            this.length = tail.length() + 1;
        }

        @Override
        public A head() { return head; }

        @Override
        public List<A> tail() { return tail; }

        @Override
        public boolean isEmpty() { return false; }

        @Override
        public List<A> setHead(A a) {
            return new Cons<>(a, tail);
        }

        @Override
        public int length() { return length; }

        @Override
        public Result<A> headOption() {
            return success(head);
        }
    }

    /**
     * A convenience method to create an empty list.
     *
     * As such, creating or using an empty list will generate a warning by the
     * compiler. The advantage is that you can use a singleton for the empty list.
     * Another solution would have been to use a parameterized empty list, but
     * this would mean to create a different empty list for each type parameter.
     * To solve this problem, we use a singleton empty list with no parameter
     * type. This generates a compiler warning. In order to restrict this
     * warning to the List class and not let it leak to the List users, you
     * don't give direct access to the singleton. That's why there's a (parameterized)
     * static method to access the singleton, and a @SuppressWarnings("rawtypes")
     * on the NIL property, as well as @SuppressWarnings("unchecked") on the
     * list method.
     * @param <A>
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <A> List<A> list() {
        return NIL;
    }

    /**
     * A convenience method to construct a list of elements from an array of
     * elements.
     * This method is annotated with @SafeVarargs to indicate that the method
     * doesn't do anything that could lead to heap pollution.
     * @param as
     * @param <A>
     * @return
     */
    @SafeVarargs
    public static <A> List<A> list(A... as) {
        class ListHelper {
            TailCall<List<A>> go(List<A> acc, int i) {
                //Need to construct the list from reverse, so do downcounting.
                return i >= 0  ? sus(() -> go(new Cons<>(as[i], acc), i-1)) : ret(acc);
            }
        }

        return new ListHelper().go(list(), as.length - 1).eval();
    }
}
