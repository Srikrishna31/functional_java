package com.functional;

import org.junit.Test;

import com.functional.Function;
import static com.functional.Function.composeAll;
import static com.util.List.range;
import static com.util.List.map;

public class FunctionTest {
    @Test
    public void testFunction() {
        Function<Integer, Integer> add = y -> y + 1;
        System.out.println(composeAll(map(range(0, 500), x -> add)).apply(0));
    }
}
