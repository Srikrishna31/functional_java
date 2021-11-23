package com.functional;

/**
 * This class encapsulates a side effect which can be applied to a computation.
 * For example, if the result needs to be printed to console, or sent over a network etc.
 * @param <T>
 */
public interface Effect<T> {
    void apply(T t);
}
