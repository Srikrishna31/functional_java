package com.functional;

import org.junit.Test;

import java.util.List;

import static com.util.List.map;
import static com.util.List.forEach;
import static com.util.List.foldLeft;

import static com.functional.Executable.compose;

public class ExecutableTest {
    @Test
    public void testCase() {
        Function<Double, Double> addTax = x -> x * 1.09;
        Function<Double, Double> addShipping = x -> x + 3.50;
        List<Double> prices = List.of(10.10, 23.45, 32.07, 9.23);
        List<Double> pricesIncludingTax = map(prices, addTax);
        List<Double> pricesIncludingShipping = map(pricesIncludingTax, addShipping);

        Effect<Double> printWith2Decimals = x -> System.out.printf("%.2f\n", x);
        forEach(pricesIncludingShipping, printWith2Decimals);

        Executable program = foldLeft(pricesIncludingShipping, Executable.identity,
                e -> d -> compose.apply(e).apply(() -> printWith2Decimals.apply(d)));

        program.exec();
    }
}
