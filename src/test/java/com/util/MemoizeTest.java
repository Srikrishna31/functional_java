package com.util;

import com.functional.Function;
import com.functional.TailCall;
import com.functional.Tuple;
import com.functional.Tuple3;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import static com.util.CollectionUtilities.map;
import static com.util.CollectionUtilities.iterate;
import static com.util.CollectionUtilities.makeString;

import static com.functional.TailCall.ret;
import static com.functional.TailCall.sus;

import java.math.BigInteger;
import java.util.function.Supplier;

public class MemoizeTest {
    @Test
    public void testFibonacciString() {
        System.out.println(fiboCorecursive(10));
    }

    private static String fiboCorecursive(int number) {
        var seed = Tuple.create(BigInteger.ZERO, BigInteger.ONE);
        Function<Tuple<BigInteger, BigInteger>, Tuple<BigInteger, BigInteger>> f =
                x -> Tuple.create(x._2, x._1.add(x._2));

        java.util.List<BigInteger> list = map(iterate(seed, f, number + 1), x -> x._1);

        return makeString(list, ", ");
    }

    private static Integer longCalculation(Integer x) {
        try {
            Thread.sleep(1_000);
        } catch (InterruptedException ignored) {}
        return x * 2;
    }

    private static <T> void verifyMemoizedFunction(Supplier<T> f) {
        long startTime = System.currentTimeMillis();
        var result1 = f.get();
        long time1 = System.currentTimeMillis() - startTime;

        startTime = System.currentTimeMillis();
        var result2 = f.get();
        long time2 = System.currentTimeMillis() - startTime;

        System.out.println(result1 + ",  " + result2);
        System.out.println(time1  + ", " + time2);

        assertEquals(result1, result2);
        assertTrue(time2 < time1);
    }

    @Test
    public void testMemoization() {
        Function<Integer, Integer> f = MemoizeTest::longCalculation;
        Function<Integer, Integer> g = Memoizer.memoize(f);

        verifyMemoizedFunction(() -> g.apply(1));
    }

    @Test
    public void testMemoizedFibo() {
        Function<Tuple3<BigInteger, BigInteger, BigInteger>, BigInteger> f = x -> fibo(x._1, x._2, x._3).eval();
        Function<Tuple3<BigInteger, BigInteger, BigInteger>, BigInteger> fm = Memoizer.memoize(f);

        verifyMemoizedFunction(() -> fm.apply(Tuple3.create(BigInteger.ONE, BigInteger.ZERO, BigInteger.valueOf(50))));
    }

    private static TailCall<BigInteger> fibo(BigInteger acc, BigInteger acc1, BigInteger n) {
        return n.equals(BigInteger.ZERO) ? ret(acc) : n.equals(BigInteger.ONE) ? ret(acc1.add(acc)) :
                sus(() -> fibo(acc.add(acc1), acc, n.subtract(BigInteger.ONE)));
    }

    @Test
    public void testCurriedMemoizer() {
        /*
        Memoizing curried functions is easy. You have to memoize each function.
         */
        Function<Integer, Function<Integer, Function<Integer, Integer>>> f3m =
                Memoizer.memoize(x -> Memoizer.memoize(y -> Memoizer.memoize(z -> longCalculation(x) + longCalculation(y)  + longCalculation(z))));

        verifyMemoizedFunction(() -> f3m.apply(2).apply(3).apply(4));
    }

    @Test
    public void testTupledMemoizer() {
        Function<Tuple3<Integer, Integer, Integer>, Integer> ft =
                x -> longCalculation(x._1) + longCalculation(x._2)  + longCalculation(x._3);

        Function<Tuple3<Integer, Integer, Integer>, Integer> ftm = Memoizer.memoize(ft);

        verifyMemoizedFunction(() -> ftm.apply(Tuple3.create(2, 3, 4)));
    }
}
