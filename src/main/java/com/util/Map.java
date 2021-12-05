package com.util;

import java.util.concurrent.ConcurrentHashMap;

/**
 * A simple wrapper class to ConcurrentHashMap, that promotes the use of
 * Option, rather than throwing exception or returning null for get function.
 */
public class Map<T, U> {
    private final ConcurrentHashMap<T, U> map = new ConcurrentHashMap<>();

    public static <T,U> Map<T, U> empty() {
        return new Map<>();
    }

    public static <T, U> Map<T,U> add(Map<T,U> m, T t, U u) {
        m.map.put(t, u);
        return m;
    }

    public Option<U> get(final T t) {
        return map.containsKey(t) ? Option.some(map.get(t)) : Option.none();
    }

    public Result<U> getResult(final T t) {
        return map.containsKey(t) ? Result.success(map.get(t))
                : Result.failure(String.format("Key %s not found in map", t));
    }

    public Map<T, U> put(T t, U u) {
        return add(this, t, u);
    }

    public Map<T, U> removeKey(T t) {
        map.remove(t);

        return this;
    }
}
