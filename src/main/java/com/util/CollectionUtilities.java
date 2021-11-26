package com.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.functional.Function;

public class CollectionUtilities {
    public static <T> List<T> list() {
        return Collections.EMPTY_LIST;
    }

    public static <T> List<T> list(T t) {
        return Collections.singletonList(t);
    }

    public static <T> List<T> list(List<T> ts) {
        return Collections.unmodifiableList(new ArrayList<>(ts));
    }

    @SafeVarargs
    public static <T> List<T> list(T... ts) {
        return Collections.unmodifiableList(Arrays.asList(Arrays.copyOf(ts, ts.length)));
    }

    <T, U> List<U> map(List<T> list, Function<T, U> f) {
        return list.stream().map(f::apply).collect(Collectors.toList());
    }
}
