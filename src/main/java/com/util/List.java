package com.util;

import com.functional.Effect;
import com.functional.TailCall;
import com.functional.Function;
import com.functional.Tuple;
import com.functional.Nothing;

import static com.util.Result.empty;
import static com.util.Result.success;
import static com.util.Result.failure;

import static com.functional.TailCall.sus;
import static com.functional.TailCall.ret;

import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

import java.util.function.Supplier;

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
     * Returns the tail(or rest) of the list wrapped in a Result context.
     * @return the result element containing the remaining elements of the list if
     * the list is not empty, otherwise an empty result object is returned.
     */
    public abstract Result<List<A>> tailOption();

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
                foldLeft(new StringBuilder(), acc -> v -> acc.append(v).append(", ")));
    }

    @Override
    public boolean equals(Object o) {
        if (!( o instanceof List)) return false;

        List that = (List) o;
        class EqualsHelper {
            TailCall<Boolean> go(List l1, List l2) {
                if (l1.isEmpty() && !l2.isEmpty() || !l1.isEmpty() && l2.isEmpty()) {
                    return ret(false);
                } else if(l1.isEmpty() && l2.isEmpty()) {
                    return ret(true);
                } else {
                    return l1.head().equals(l2.head()) ? go(l1.tail(), l2.tail()) : ret(false);
                }
            }
        }

        return new EqualsHelper().go(this, that).eval();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        return foldLeft(1, acc -> v -> acc * prime + v.hashCode());
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
     * Same as the static concat method, but a convenience member method to
     * concat another list to this list.
     * @param that : The other list to concat to this list.
     * @return the combined list of this and that.
     */
    public List<A> concat(List<A> that) {
        return concat(this, that);
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
     * with the accumulator. In addition, it returns the remaining list as the second
     * result for any further processing.
     * @param identity : The identity of the operation. This will be returned if the input
     *                 list is empty.
     * @param zeroElement : The absorbing element of the computation.
     * @param f : Accumulating function, which is a curried function, which accepts a parameter of type B, and
     *           returns a function that accepts a parameter of type A and returns a B.
     * @param <B> : The type of reduction element.
     * @return the tuple of reduced type object and the remaining list elements.
     */
    public <B> Tuple<B, List<A>> foldLeft(B identity, B zeroElement, Function<B, Function<A, B>> f) {
        class FoldHelper {
            TailCall<Tuple<B, List<A>>> go(List<A> ls, B acc) {
                return ls.isEmpty() || acc.equals(zeroElement) ? ret(Tuple.create(acc, ls)) :
                        sus(() -> go(ls.tail(),
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
     * which the fold must be terminated early. In addition, it returns the remaining list
     * as the second result for any further processing.
     * @param identity: The identity of the operation. This will be returned if the input
     *                 is empty.
     * @param p : The predicate which accepts the current accumulator and the current
     *          element and returns a boolean
     * @param f : Accumulating function, which is a curried function, which accepts a
     *          parameter of type B, and returns a function that accepts a parameter
     *          of type A and returns a B.
     * @param <B> : The type of reduction element.
     * @return the tuple of reduced type object and the remaining list elements.
     */
    public <B> Tuple<B, List<A>> foldLeft(B identity, Function<B, Function<A, Boolean>> p,
                                        Function<B, Function<A, B>> f) {
        class FoldHelper {
            TailCall<Tuple<B, List<A>>> go(List<A> ls, B acc) {
                return ls.isEmpty() || p.apply(acc).apply(ls.head()) ? ret(Tuple.create(acc,  ls)) :
                        sus(() -> go(ls.tail(),
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
     * with the accumulator. In addition, it returns the remaining list
     * as the second result for any further processing.
     * @param identity : The identity of the operation. This will be returned if the input
     *                 list is empty.
     * @param zeroElement : The absorbing element of the computation.
     * @param f : Accumulating function that takes a parameter of type A, and returns a function
     *          that takes a parameter of type B and returns a B.
     * @param <B> : The type of reduction element.
     * @return the tuple of reduced type object and the remaining list elements.
     */
    public <B> Tuple<B, List<A>> foldRight(B identity, B zeroElement, Function<A, Function<B, B>> f) {
        return reverse().foldLeft(identity, zeroElement, acc -> v -> f.apply(v).apply(acc));
    }

    /**
     * This is a more general overload supports early termination of the fold based on
     * the predicate, which accepts both the accumulator(intermediate result) and the current
     * element. If it returns true, then the fold is terminated immediately. This
     * obviates the need to find a zero element, and also design any custom condition for
     * which the fold must be terminated early. In addition, it returns the remaining list
     * as the second result for any further processing.
     * @param identity: The identity of the operation. This will be returned if the input
     *                 is empty.
     * @param p : The predicate which accepts the current accumulator and the current
     *          element and returns a boolean
     * @param f : Accumulating function, which is a curried function, which accepts a
     *          parameter of type A, and returns a function that accepts a parameter
     *          of type B and returns a B.
     * @param <B> : The type of reduction element.
     * @return the tuple of reduced type object and the remaining list elements.
     */
    public <B> Tuple<B, List<A>> foldRight(B identity,
    Function<B, Function<A, Boolean>> p, Function<A, Function<B, B>> f) {
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
     * This function is same as traverse, but implemented using a while loop, instead
     * of foldRight, which will avoid StackOverflow error for huge lists.
     * @param list
     * @param f
     * @param <T>
     * @param <U>
     * @return
     */
    public static <T, U> Result<List<U>> sequence(List<T> list, Function<T, Result<U>> f) {
        List<U> result = list();
        List<T> workList = list.reverse();
        while(!workList.isEmpty()) {
            Result<U> ru = f.apply(workList.head());
            if (ru.isSuccess()) {
                result = result.cons(ru.successValue());
            } else {
                return Result.failure(ru.failureValue());
            }
            workList = workList.tail();
        }
        return Result.success(result);
    }

    /**
     * Convenience method to do the sequence on this object.
     * @param f
     * @param <B>
     * @return
     */
    public <B> Result<List<B>> sequence(Function<A, Result<B>> f) {
        return sequence(this, f);
    }

    public static <U> Supplier<List<U>> lazySequence(final List<Supplier<U>> list) {
        return () -> list.map(Supplier::get);
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
     * This function combines the elements of the provided list with this list.
     * This function is similar to the zip static function, but provided as a
     * convenience method function.
     * @param that : The list of elements of type B.
     * @param <B> : Type parameter of elements in the second list.
     * @return the list of elements of Tuple.
     */
    public <B> List<Tuple<A, B>> zip(List<B> that) {
        return zip(this, that);
    }

    /**
     * This utility function creates a new list with the index of the element as
     * the second element in the tuple, which can be useful in certain applications
     * which rely on the index of the element held in the list.
     * @return The result object holding the list of tuples.
     */
    public Result<List<Tuple<A, Integer>>> zipWithPositionResult() {
        return Result.success(zip(iterate(0, x -> x + 1, length())));
    }

    /**
     * This function is same as zipWithPositionResult, but without the Result
     * context encapsulating the list.
     * @return The list of tuples.
     */
    public List<Tuple<A, Integer>> zipWithPosition() {
        return zipWithPositionResult().getOrElse(list());
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

    /**
     * This function returns the element at the given position.
     * If the list is short, or the index is out of range, then it returns a
     * failure object.
     * @param index : The position from which element is to be fetched.
     * @return the Result object encapsulating the element or failure.
     */
    public Result<A> getAt(int index) {
        class GetHelper{
            TailCall<Result<A>> go(List<A> as, int index) {
                return index == 0 ? ret(success(as.head())) : sus(() -> go(as.tail(), index - 1));
            }
        }

        return index < 0 || index > length()  ? failure("Index out of bounds") :
                new GetHelper().go(this, index).eval();

    }

    /**
     * This function splits the list at the given index, and returns a tuple
     * containing the two parts.
     * @param index : The index at which to split the list.
     * @return the tuple containing the two sub lists.
     */
    public Tuple<List<A>, List<A>> splitAt(int index) {
        if (index < 0) {
            return Tuple.create(list(), this);
        }

        if (index > length()) {
            return Tuple.create(this, list());
        }

        var identity = Tuple.create(List.<A>list(), index);
        Tuple<Tuple<List<A>, Integer>, List<A>> res = foldLeft(identity,
                (Function<Tuple<List<A>, Integer>, Function<A, Boolean>>) acc -> v -> acc._2 == 0,
                (Function<Tuple<List<A>, Integer>, Function<A, Tuple<List<A>, Integer>>>) acc -> v -> acc._2 >= 0 ?
                                                                           Tuple.create(acc._1.cons(v), acc._2 - 1) :
                                                                           acc);
        return Tuple.create(res._1._1.reverse(), res._2);
    }

    /**
     * This function does the same job as splitAt, except in the return value it
     * provides a list of lists, instead of a tuple of lists.
     * @param index : the index at which to split the list.
     * @return the list containing two lists split around the index.
     */
    public List<List<A>> splitListAt(int index) {
        var res = splitAt(index);

        return list(res._1, res._2);
    }

    /**
     * A function which returns true if a list starts with the provided sublist,
     * false otherwise.
     * @param list : The list in which to see the sublist for.
     * @param sub : The sublist which should be checked against the main list.
     * @param <A> : Type parameter of the elements.
     * @return true if the sublist is contained in the beginning of the list,
     * false otherwise.
     */
    public static <A> Boolean startsWith(List<A> list, List<A> sub) {
        class StartsWithHelper {
            TailCall<Boolean> go(List<A> list, List<A> sub) {
                return sub.isEmpty() ? ret(true)
                                    : list.isEmpty() ? ret(false)
                                                    : list.head().equals(sub.head()) ? sus(() -> go(list.tail(), sub.tail()))
                                                                                     : ret(false);
            }
        }

        return new StartsWithHelper().go(list, sub).eval();
    }

    /**
     * This function searches for the provided sublist in a list, and returns true
     * if it finds it, false otherwise.
     * @param list : The list in which to search the sublist.
     * @param sub : The sublist to search for.
     * @param <A> : Type parameter of the elements.
     * @return true if the sublist is contained in the list, false otherwise.
     */
    public static <A> Boolean hasSubList(List<A> list, List<A> sub) {
        class HasSubListHelper {
            TailCall<Boolean> go(List<A> list) {
                return list.isEmpty() ? ret(sub.isEmpty()) : startsWith(list, sub) ? ret(true) :
                        sus(() -> go(list.tail()));
            }
        }

        return new HasSubListHelper().go(list).eval();
    }

    /**
     * This method accepts a function from A to B, and returns a Map, where keys
     * are the result of the function applied to each element of the list and
     * values are lists of elements corresponding to each key.
     * @param f : The function that maps an A into a B.
     * @param <B> : Type parameter of the keys.
     * @return a Map containing the list elements grouped by keys, as provided
     * by the function.
     */
    public <B> Map<B, List<A>> groupBy(Function<A, B> f) {
        return foldLeft(Map.empty(), acc -> a -> {
            final var res = f.apply(a);
            return acc.put(res, acc.get(res).getOrElse(list()).cons(a));
        });
    }

    /**
     * This function takes a starting element (seed), and a function from S to
     * Result<Tuple<A, S>> and produces a List<A> by successively applying f
     * to the S value as long as the result is a Success.
     * @param seed : The element with which to start the list.
     * @param f : The function to apply to the seed element to produce the next
     *          element.
     * @param <A> : Type parameter of the elements of the list.
     * @param <S> : Type parameter of the seed elements.
     * @return the list of values of type A.
     */
    public static <A, S> List<A> unfold(S seed, Function<S, Result<Tuple<A, S>>> f) {
        class UnfoldHelper {
            TailCall<Result<List<A>>> go(S seed, List<A> ls) {
                var res = f.apply(seed);
                Result<TailCall<Result<List<A>>>> result = res.map(rt -> sus(() -> go(rt._2, ls.cons(rt._1))));

                return result.getOrElse(ret(success(ls)));
            }
        }

        return new UnfoldHelper().go(seed, list()).eval().getOrElse(list()).reverse();
    }

    /**
     * A function which can be used to produce a list of integers in a given range.
     * 
     * @param start : The element from which to start counting.
     * @param end : The element before which to include in the list.
     * @return the list of integers that lie between start and end.
     */
    public static List<Integer> range(int start, int end) {
        return unfold(start, i -> i < end ? success(Tuple.create(i, i + 1)) : empty());
    }

    /**
     * A function which can generate a list for given number of elements, starting
     * from the seed value.
     * @param seed : The starting value to be included in the list.
     * @param f : The generator function which takes the current seed, and returns the next one.
     * @param n : The number of times to use the genrator function (also the number of elements
     *          to be included in the list.
     * @param <T> : Type parameter of the elements in the list.
     * @return the list containing n elements, which are generated from the function f, starting
     * from the seed value.
     */
    public static <T> List<T> iterate(T seed, Function<T, T> f, int n) {
        var res = List.<T>list();
        T temp = seed;

        for (int i = 0; i < n; ++i) {
            res = res.cons(temp);
            temp = f.apply(temp);
        }

        return res.reverse();
    }

    /**
     * A convenience function to build a list of values, which are obtained by repeatedly
     * applying the given supplier for the specified number of times.
     * @param length : The number of times to apply the supplier function.
     * @param f : The supplier function which provides the next value to be stored in the list.
     * @param <T> : Type parameter of the elements in the list.
     * @return the list containing length elements, which are generated by repeatedly calling
     * the supplier function.
     */
    public static <T> List<T> fill(int length, Supplier<T> f) {
        return iterate(f.get(), t -> f.get(), length);
    }

    /**
     * This function applies the given predicate to each element, until it returns
     * true. If none of the elements return true, then the result is false.
     * @param p : the predicate to be applied to each element of the list.
     * @return true if predicate returns true for an element, false otherwise.
     */
    public boolean exists(Function<A, Boolean> p) {
        return foldLeft(false, true, acc -> v -> acc || p.apply(v))._1;
    }

    /**
     * This function applies the given predicate to each element, and returns true
     * if and only if the predicate returns true for all the elements, otherwise
     * returns false.
     * @param p : The predicated to be applied to each element of the list.
     * @return true if predicate is true for all elements, false otherwise.
     */
    public boolean forAll(Function<A, Boolean> p) {
        return foldLeft(true, false, acc -> v -> acc && p.apply(v))._1;
    }

    /**
     * Given a depth value, this function divides the list into a number of sublists.
     * At each level, the list is divided into two halves, and this is done recursively
     * until the depth becomes less than 2. This function is useful for parallel
     * processing.
     * @param depth : The number of levels deep the list subdivision should continue for.
     * @return the list of lists of elements of type A.
     */
    public List<List<A>> divide(int depth) {
        class DivideHelper {
            TailCall<List<List<A>>> go(List<List<A>> lls, int depth) {
                return lls.head().length() < depth || depth < 2 ? ret(lls) :
                        sus (() -> go(lls.flatMap(ls -> ls.splitListAt(ls.length() / 2)), depth  / 2));
            }
        }

        return isEmpty() ? list(this) : new DivideHelper().go(list(this), depth).eval();
    }

    /**
     * Given an Executor Service, this function divides the list into chunks and applies
     * the function in parallel. Since it applies the function in parallel, it also needs
     * a function to combine the intermediate results, which may or may not be the same
     * operation. In terms of functionality, this function is identical to foldLeft, unless
     * the operation being applied is not commutative.
     * @param es : The ExecutorService to use for submitting the parallel jobs.
     * @param identity : The identity element for the fold operation.
     * @param f : The accumulator function to be applied to each element and the accumulator.
     * @param m : The combining function, which combines intermediate results from the parallel
     *          jobs.
     * @param <B> : The type parameter of the resulting fold value.
     * @return the folded value.
     */
    public <B> Result<B> parFoldLeft(ExecutorService es, B identity, Function<B,Function<A, B>> f,
                             Function<B, Function<B, B>> m) {
        final int chunks = 1024;
        final var dList  = divide(chunks);

        try {
            List<B> intermediateResult = dList.map(ls -> es.submit(() -> ls.reverse().foldLeft(identity, f))).map( x -> {
                try {
                    return x.get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            });

            return success(intermediateResult.foldLeft(identity, m));
        } catch (Exception e) {
            return failure(e);
        }
    }

    /**
     * This is the parallel version of the map function, analogous to parallel fold in its
     * implementation.
     * @param es : The executor service to use for submitting the parallel jobs.
     * @param f : The function which maps an A to a B.
     * @param <B> : The type parameter of the result of applying f.
     * @return the mapped list of values of type B.
     */
    public <B> Result<List<B>> parMap(ExecutorService es, Function<A, B> f) {
        return parFoldLeft(es, list(), acc -> v -> acc.cons(f.apply(v)), l1 -> l2 -> concat(l1, l2));
    }

    /**
     * Applies the given effect to each element of the list.
     * @param ef : The effect to be applied.
     */
    public abstract void forEach(Effect<A> ef);

    public List<A> takeAtMost(int n) {
        class TakeHelper {
            TailCall<List<A>> go (List<A> acc, List<A> unseenList, int n) {
                return unseenList.isEmpty() || n <= 0
                        ? ret(acc)
                        : sus(() -> go(acc.cons(unseenList.head()), unseenList.tail(), n - 1));
            }
        }

        return new TakeHelper().go(list(), this, n).eval().reverse();

        //TODO: Fix the compiler error, to make the implementation functional.
        //For some reason, the below code is errored by the compiler.
//        Function<Tuple<List<A>, Integer>, Function<A, Boolean>> p = t -> a -> t._2 < n;
//        var res = foldLeft(Tuple.create(List.<A>list(), 0),
//                p, acc -> v -> Tuple.create(acc._1.cons(v), acc._2 + 1));
//
//        return res._1._1.reverse();
    }

    /**
     * This function retrieves the first set of elements from the list, for which
     * the given predicate returns true. The moment the predicate returns false
     * for a value, further processing of the list is stopped.
     * @param f : The predicate to be applied to each element of the list.
     * @return The elements which satisfy the predicate.
     */
    public List<A> takeWhile(Function<A, Boolean> f) {
        class TakeHelper {
            TailCall<List<A>> go(List<A> acc, List<A> unseenList) {
                return  unseenList.isEmpty() || !f.apply(head())
                        ? ret(acc)
                        : sus(() -> go(acc.cons(unseenList.head()), unseenList.tail()));
            }
        }
        var res = new TakeHelper().go(list(), this).eval();
        return res.reverse();

        //TODO: Fix the compiler error, to make the implementation functional.
        //For some reason, this is not being accepted by the compiler.
//        var res = foldLeft(List.<A>list(), ls -> a -> f.apply(a), acc -> v -> acc.cons(v));
//
//        return res._1.reverse();
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
        public Result<List<A>> tailOption() {
            return Result.empty();
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

        @Override
        public void forEach(Effect<A> ef) {};
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
        public Result<List<A>> tailOption() {
            return Result.success(tail);
        }

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

        @Override
        public void forEach(Effect<A> ef) {
            class ForEachHelper {
                TailCall<Nothing> go(List<A> ls) {
                    return ls.isEmpty() ?
                        ret(Nothing.instance)
                            : sus (() -> {
                        ef.apply(ls.head());
                        return sus(() -> go(ls.tail()));
                    });
                }
            }
            new ForEachHelper().go(this).eval();
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

    /**
     * A renaming of list method. This method does the same thing as list(a...).
     * @param as : The array of objects to be wrapped in List.
     * @param <A> : Type parameter of the values in the array.
     * @return the List object, which contains the elements passed in the array.
     */
    @SafeVarargs
    public static <A> List<A> of(A... as) {
        return list(as);
    }
}

