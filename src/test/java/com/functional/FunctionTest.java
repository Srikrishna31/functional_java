package com.functional;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import static com.functional.Function.*;
import static com.util.CollectionUtilities.range;
import static com.util.CollectionUtilities.map;
import static com.util.CollectionUtilities.list;

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

        var res1 = composeAllWithFoldLeft(list(f1, f2, f3)).apply("x");
        var res2 = composeAllWithFoldRight(list(f1, f2, f3)).apply("x");
        System.out.println(res1);
        System.out.println(res2);

        assertEquals(res1, res2);
    }

    @Test
    public void testAndThenAll() {
        Function<String, String> f1 = x -> "(a" + x + ")";
        Function<String, String> f2 = x -> "{b" + x + "}";
        Function<String, String> f3 = x -> "[c" + x + "]";

        var res1 = andThenAllWithFoldLeft(list(f1, f2, f3)).apply("x");
        var res2 = andThenAllWithFoldRight(list(f1, f2, f3)).apply("x");
        System.out.println(res1);
        System.out.println(res2);

        assertEquals(res1, res2);
    }
}
