package com.functional;

import java.util.function.Supplier;

/**
 * This class represents an alternative to if else, and allows pattern matching
 * as envisioned in functional programs.
 * It represents a condition and corresponding result. The condition is represented
 * by a Supplier<Boolean>, where Supplier is a functional interface(as defined in Java 8).
 */
public class Case<T> extends Tuple<Supplier<Boolean>, Supplier<Result<T>>> {
    /** Clients never need to instantiate this class explicitly
     * since it is done by the static methods provided.
     */
    private Case(Supplier<Boolean>  booleanSupplier,
                 Supplier<Result<T>> resultSupplier) {
        super(booleanSupplier, resultSupplier);
    }

    /**
     * This is simply a marker class to distinguish the default case from other
     * cases in the arguments to the match function.
     * Hence the boolean supplier always returns true, but it is one that is never used.
     */
    private static class DefaultCase<T> extends Case<T> {
        private DefaultCase(Supplier<Boolean> booleanSupplier,
                Supplier<Result<T>> resultSupplier) {
            super(booleanSupplier, resultSupplier);
        }
    };

    /**
     * This function represents a case which consists of two parts: a boolean supplier,
     * and a value supplier.
     * @param condition : a boolean supplier that returns true/false
     * @param value: a value supplier, that returns the underlying value.
     * @param <T>
     * @return the Case object, which can be evaluated using the match function.
     */
    public static <T> Case<T> mcase(Supplier<Boolean> condition,
                                    Supplier<Result<T>> value) {
        return new Case<>(condition, value);
    }


    /**
     * This overload returns the DefaultCase, so that at the client site, one
     * can just pass the default value supplier.
     * @param value: a value supplier, that returns the underlying value.
     * @param <T>
     * @return the DefaultCase object, which can be evaluated using the match function.
     */
    public static <T> DefaultCase<T> mcase(Supplier<Result<T>> value) {
        /**
         * () -> true is a lambda representing a Supplier<Boolean> that will
         * always return true. In other words, it's a "lazy" true.
         */
        return new DefaultCase<>(() -> true, value);
    }

    /**
     * This function accepts all the cases, and returns the value of the first case
     * that matches, i.e the supplier returns true for that case. It is the client's
     * responsibility to ensure that the cases are mutually exclusive, and as exhaustive
     * as they wish. And also, they should be provided in correct order, otherwise,
     * the results may be undefined.
     * @param defaultCase : the default value to be returned if none of the other cases match.
     * @param matchers : the different cases that need to be considered.
     * @param <T> : value type parameter.
     * @return a Result object, which encapsulates a success or a failure.
     */
    @SafeVarargs
    public static <T> Result<T> match(DefaultCase<T> defaultCase, Case<T>... matchers) {
        for(Case<T> aCase: matchers) {
            if (aCase._1.get()) return aCase._2.get();
        }

        return defaultCase._2.get();
    }
}
