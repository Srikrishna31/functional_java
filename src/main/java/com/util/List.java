package com.util;

import com.functional.TailCall;
import com.functional.Function;

import static com.functional.TailCall.sus;
import static com.functional.TailCall.ret;

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
     * @return the first element from the list.
     */
    public abstract A head();

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
        class StringHelper {
            TailCall<StringBuilder> go(StringBuilder acc, List<A> as) {
                return as.isEmpty() ? ret(acc) : sus(() -> go(acc.append(as.head().toString()).append(", "), as.tail()));
            }
        }

        return String.format("[%sNIL]",
                new StringHelper().go(new StringBuilder(), this).eval().toString());
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
        return list1.isEmpty() ? list2 : new Cons<>(list1.head(), concat(list1.tail(), list2));
    }


    /**
     * Another general purpose function which can be used to turn the list into any other type.
     * This function operates on the list from right to left, applying the accumulating operator
     * on the right, and the accumulating value on the left(for each element of the list).
     * @param ls: List of values which need to be folded into another type.
     * @param identity : The identity element of the operation. This will be returned if the
     *                 input list is empty.
     * @param f : Accumulationg function that takes a parameter of type A, and returns a function
     *          that takes a parameter of type B and returns a B.
     * @param <A> : The type of elements in the list.
     * @param <B> : The type of reduction element.
     * @return the reduced type object.
     */
    public static <A,B> B foldRight(List<A> ls, B identity, Function<A, Function<B, B>> f) {
        return ls.isEmpty() ? identity : f.apply(ls.head()).apply(foldRight(ls.tail(), identity,f));
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
     * Reverses the order of the elements and returns a new list.
     * @return the list with elements in the reverse order.
     */
    public List<A> reverse() {
        class ReverseHelper {
            TailCall<List<A>>  go(List<A> acc, List<A> as) {
                return as.isEmpty() ? ret(acc) : sus(() -> go(new Cons<>(as.head(), acc), as.tail()));
            }
        }

        return new ReverseHelper().go(list(), this).eval();
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
    }

    private static class Cons<A> extends List<A> {
        private final A head;
        private final List<A> tail;

        private Cons(A head, List<A> tail) {
            this.head = head;
            this.tail = tail;
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
