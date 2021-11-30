package com.functional;

import org.junit.Test;

import com.functional.Function;

import static com.functional.Function.*;
import static com.util.List.range;
import static com.util.List.map;
import static com.util.List.list;

public class FunctionTest {
    @Test
    public void testComposeAll() {
        Function<Integer, Integer> add = y -> y + 1;
        System.out.println(composeAll(map(range(0, 500), x -> add)).apply(0));
    }

    @Test
    public void testAnotherComposeAll() {
        Function<String, String> f1 = x -> "(a" + x + ")";
        Function<String, String> f2 = x -> "{b" + x + "}";
        Function<String, String> f3 = x -> "[c" + x + "]";
        System.out.println(composeAllWithFoldLeft(list(f1, f2, f3)).apply("x"));
        System.out.println(composeAllWithFoldRight(list(f1, f2, f3)).apply("x"));
    }

    @Test
    public void testAndThenAll() {
        Function<String, String> f1 = x -> "(a" + x + ")";
        Function<String, String> f2 = x -> "{b" + x + "}";
        Function<String, String> f3 = x -> "[c" + x + "]";
        System.out.println(andThenAllWithFoldLeft(list(f1, f2, f3)).apply("x"));
        System.out.println(andThenAllWithFoldRight(list(f1, f2, f3)).apply("x"));
    }
}
