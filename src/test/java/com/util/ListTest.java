package com.util;

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
}
