package com.util;

import com.functional.TailCall;
import static com.functional.TailCall.ret;
import static com.functional.TailCall.sus;

import org.junit.Test;

import static com.util.List.*;
import com.functional.Function;

import java.util.List;

public class ListTest {
    @Test
    public void testCase() {
        Function<Double, Double> addTax = x -> x * 1.09;
        Function<Double, Double> addShipping = x -> x + 3.50;
        List<Double> prices = List.of(10.10, 23.45, 32.07, 9.23);
        List<Double> pricesIncludingTax = map(prices, addTax);
        List<Double> pricesIncludingShipping = map(pricesIncludingTax, addShipping);

        System.out.println(pricesIncludingShipping);

        /**
         * The above code works, but is not efficient since, it needs to map the elements
         * of the list twice, separately, which means iterating over the list twice.
         * Instead, if the functions are composed, then the mapping can be done once, we
         * can improve the performance of the computation.
         */
        Function<Double, Double> addTaxAndShipping = addTax.andThen(addShipping);
        System.out.println(map(prices, addTaxAndShipping));
    }

    private static List<Integer> range(Integer start, Integer limit) {
        return unfold(start, x -> x + 1, x -> x < limit);
    }

    private static List<Integer> rangeRecursive(Integer start, Integer end) {
        class RangeHelper {
            TailCall<List<Integer>> go(Integer start, List<Integer> acc) {
                return (end <= start) ? ret(acc) :
                        sus(() -> go(start + 1, append(acc, start)));
            }
        }

        return new RangeHelper().go(start, List.of()).eval();
    }

    @Test
    public void testUnfold() {
        /**
         * To test the unfold function, we will simulate the regularly used construct
         * in programming:
         * for (int i = 0; i < limit; i++) {
         *  some processing
         *  }
         *  The idea is to abstract out the loop, so that one can concentrate on writing
         *  the core logic of producing an element.
         */
        var res = range(1, 10);

        System.out.println(res);

        rangeRecursive(0, 5).forEach(System.out::println);

        System.out.println(map(range(0, 10), x -> x * x));
    }
}
