package com.functional;

import java.util.function.Supplier;

/**
 * This class abstracts recursion, to allow for tail calls to be executed in constant
 * stack space. Following are the steps needed to abstract recursion:
 * -> Represent unevaluated method calls.
 * -> Store them in a stack-like structure untile you encounter a terminal condition
 * -> Evaluate the calls in "last-in, first-out" LIFO order.
 * To represent a step in the calculation, we define an abstract (why?) class called
 * TailCall(because we want to represent a call to a method that appears in the tail
 * position).
 * Also refer to com.functional.TailCallTest class to see examples of using this class
 * to encapsulate Tail Recursive calls.
 * TailCall abstract has two subclasses namely: Suspend  and Return.
 * @param <T> : The type parameter representing the result of the computation.
 *
 */
public abstract class TailCall<T> {
    /**
     * Prevent extension by clients. They should only need to deal with Suspend and
     * Return classes.
     */
    private TailCall() {}
    /**
     * This method returns the next call in the evaluation process. The next call
     * could either be a Suspend object or a Return object, based on the current
     * evaluation result.
     * @return : Either a Suspend object or a Return object.
     */
    public abstract TailCall<T> resume();

    /**
     * This method evaluates the recursive call, and returns the result.
     * This evaluation happens in a stack safe way to avoid StackoverflowException.
     * @return : The result of evaluating the call.
     */
    public abstract T eval();

    /**
     * A convenience method, replacing instanceof. This method returns true in the
     * Suspend class, and false in Return class.
     * @return : true if this object is a Suspend, false otherwise.
     */
    public abstract boolean isSuspend();

    /**
     * A convenience factory method to instantiate the Return class.
     * @param t : The end result of the computation.
     * @param <T> : Type parameter of the result.
     * @return : Return object holding the result of computation.
     */
    public static <T> Return<T> ret(T t) {
        return new Return<>(t);
    }

    /**
     * A convenience factory method to instantiate the Suspend class.
     * @param s : A Supplier representing the intermediate computation.
     * @param <T> : Type parameter of the result.
     * @return : Suspend object holding the intermediate step of computation.
     */
    public static <T> Suspend<T> sus(Supplier<TailCall<T>> s) {
        return new Suspend<>(s);
    }
    /**
     * This class represents an intermediate call, when the processing of one step
     * is suspended to call the method again for evaluating the next step.
     * It is instantiated with Supplier<TaillCall<T>>, which represents the next
     * recursive call. This way, instead of putting all TailCalls in a list, we'll
     * construct a linked list by linking each tail call to the next. The benefit
     * of this approach is that such a linked list is a stack, offering constant
     * time insertion as well as constant time access to the last inserted element,
     * which is optimal for a LIFO structure.
     * @param <T> : The type parameter which would be the end result of the recursive call.
     */
    private static class Suspend<T> extends TailCall<T> {
        private final Supplier<TailCall<T>> resume;

        Suspend(Supplier<TailCall<T>> resume) {
            this.resume = resume;
        }

        /**
         * This is the meat of the solution, that evaluates a tail call on the
         * constant stack space. Of course it uses a while loop, but the clients
         * of the class don't need to rely on this control structure, as long as
         * they can write the function calls in tail recursive terms.
         * @return
         */
        @Override
        public T eval() {
            TailCall<T> tailRec = this;
            while(tailRec.isSuspend()) {
                tailRec = tailRec.resume();
            }

            return tailRec.eval();
        }

        @Override
        public boolean isSuspend() {
            return true;
        }

        @Override
        public TailCall<T> resume() {
            return resume.get();
        }
    }

    /**
     * This class represents the last call, which is supposed to return the result,
     * hence the name Return. It won't hold a link to the next TailCall, because
     * there's nothing next, but it'll hold the result.
     * @param <T>
     */
    private static class Return<T> extends TailCall<T> {
        private final T t;
        Return(T t) {
            this.t = t;
        }

        /**
         * This represents the last step in the computation, hence
         * return the result, which is held as the private member.
         * @return : The result of the recursive computation.
         */
        @Override
        public T eval() {
            return t;
        }

        @Override
        public boolean isSuspend() {
            return false;
        }

        /**
         * This method is not supposed to be used by the client. Hence, this method
         * throws an exception when called on Return object. Calling this method on
         * Return object is a bug, hence stop the application by throwing an
         * exception.
         * @return
         */
        @Override
        public TailCall<T> resume() {
            throw new IllegalStateException("Return has no resume");
        }
    }

}
