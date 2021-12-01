package com.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.functional.Function;

/**
 * A utility class which provides the memoization facility for functions.
 * Memoization of Multiargument functions
 * There is no such thing in the world as a function with several arguments.
 * Functions are applications of one set (source) to another set (target). They
 * can't have several arguments. Functions that appear to have several arguments
 * are one of these:
 * -> Functions of tuples.
 * -> Functions returning functions returning functions ... returning a result.
 *
 * In either case, we're concerned onl with functions of one argument so we can
 * easily use Memoizer class.
 *
 * Memoizing is about maintaining state between function calls. A memoized function
 * is a function whose behavior is dependent on the current state. But it'll always
 * return the same value for the same argument. Only the time needed to return the
 * value will be different. So the memoized function is still a pure function if
 * the original function is pure.
 * @param <T> Type from which function operates
 * @param <U> Type into which the function maps.
 */
public class Memoizer<T, U>  {
    private final Map<T, U> cache = new ConcurrentHashMap<>();

    private Memoizer() {}

    /**
     * A convenience function, which accepts a function as argument
     * and returns a memoized version of it.
     * @param function : Function from T to U, which needs to be memoized.
     * @param <T> : Type parameter of the input.
     * @param <U> : Type parameter of the output.
     * @return the memoized function.
     */
    public static <T, U> Function<T, U> memoize(Function<T, U> function) {
        return new Memoizer<T, U>().doMemoize(function);
    }

    /**
     * The meat of the Memoizer class. This helper function, encloses a Memoizer
     * instance, which holds the map, and then looks up the value in the map.
     * If the value is not present, then it calls the function and then returns
     * the value.
     * @param function : The function which needs to be memoized.
     * @return the memoized function.
     */
    private Function<T, U> doMemoize(Function<T, U> function) {
        return input -> cache.computeIfAbsent(input, function::apply);
    }
}
