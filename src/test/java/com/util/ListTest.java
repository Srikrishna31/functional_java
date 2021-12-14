package com.util;

import static com.util.List.*;

import com.functional.Function;
import com.functional.TailCall;
import static com.functional.TailCall.sus;
import static com.functional.TailCall.ret;

import com.functional.Tuple;
import org.junit.Test;

import java.math.BigInteger;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ListTest {
    private final List<Integer> l1 = list(1,2,3,4,5,6,7,8,9);

    @Test
    public void testList() {

        var list = List.list(1, 2, 3, 4);

        System.out.println(list);

        var list1 = List.<Integer>list();

        var res = list1.dropWhile(x -> false);
        System.out.println(list1.dropWhile(x -> false));

        assertEquals(list(), res);
    }

    @Test
    public void testConcat() {
        var l1 = list(1,2,3);
        var l2 = list(4,5,6);

        var l3 = concat(l1, l2);

        System.out.println(l3);

        assertEquals(l3.toString(), list(1,2,3,4,5,6).toString());
    }

    @Test
    public void testListSum() {

        class SumHelper{
            TailCall<Integer> go(List<Integer> ls, Integer sum) {
                return ls.isEmpty() ? ret(sum) : sus(() -> go(ls.tail(), sum + ls.head()));
            }
        }

        var res = new SumHelper().go(l1, 0).eval();
        System.out.println(res);

        Function<List<Integer>, Integer> add = ls -> ls.foldRight(0, x -> y -> x + y);

        assertEquals(Integer.valueOf(45), res);
        assertEquals(res, add.apply(l1));
    }

    @Test
    public void testDrop() {
        var res = l1.drop(5);

        System.out.println(res);

        assertEquals(res.toString(), list(6,7,8,9).toString());
    }

    @Test
    public void testLength() {
        System.out.println(l1.length());

        assertEquals(l1.length(), 9);
    }

    @Test
    public void testReverse() {
        System.out.println("Reverse: " + l1.reverse());

        assertEquals(list(9,8,7,6,5,4,3,2,1).toString(), l1.reverse().toString());
    }

    @Test
    public void testMapWithFold() {
        var res = l1.foldRight(list(), v -> acc -> acc.cons(3 * v));

        System.out.println(res);

        assertEquals(list(3,6,9,12,15,18,21,24,27).toString(), res.toString());
    }

    @Test
    public void testMap() {
        var res = l1.map(x -> x *3);

        System.out.println(res);

        assertEquals(list(3,6,9,12,15,18,21,24,27).toString(), res.toString());

    }

    @Test
    public void testFilter() {
        var res = l1.filter(x -> x <= 5);

        System.out.println(res);

        assertEquals(list(1,2,3,4,5).toString(), res.toString());
    }

    @Test
    public void testLastOption() {
        var res = l1.lastOption();

        System.out.println(res.getOrElse(0));

        assertEquals(res.getOrElse(0), Integer.valueOf(9));
    }

    @Test
    public void testFlattenListResult() {
        var l = List.list(Result.success(1), Result.success(3), Result.success(3));
        var res = List.flattenResult(l);

        System.out.println(res.getOrElse(list()));

        assertEquals(res.getOrElse(list()).toString(), list(1,3,3).toString());
    }

    @Test
    public void testSequence() {
        var l = list(Result.success(1), Result.<Integer>failure("Random"), Result.success(2));

        var res = List.sequence(l);

        System.out.println(res.getOrElse(list()));

        assertEquals(res.toString(), "Failure(Random)");
    }

    @Test
    public void testZipWith() {
        var l2 = list("1", "2", "3", "4", "5", "6", "7", "8", "9");

        var res = List.zipWith(l1, l2, i -> s -> Tuple.create(i, s));

        System.out.println(res.toString());
    }

    @Test
    public void testUnzip() {
        var l = list(Tuple.create(1, "a"),
                Tuple.create(2, "b"),
                Tuple.create(3, "c"));
        var res = List.unzip(l);

        System.out.println(res.toString());

        assertEquals(res._1.toString() , list(1,2,3).toString());
        assertEquals(res._2.toString(), list("a", "b", "c").toString());
    }

    @Test
    public void testProduct() {
        var l1 = list(1, 2, 3);
        var l2 = list(4,5,6);

        var res = List.product(l1, l2);

        System.out.println(res);

        assertEquals(res.toString(), list(Tuple.create(1,4), Tuple.create(1, 5), Tuple.create(1, 6),
                Tuple.create(2, 4), Tuple.create(2, 5), Tuple.create(2, 6),
                Tuple.create(3, 4), Tuple.create(3, 5), Tuple.create(3, 6)).toString());
    }

    @Test
    public void testGetAt() {
        var res = l1.getAt(5);

        System.out.println(res.getOrElse(0));

        assertEquals(res.getOrElse(0), Integer.valueOf(6));

    }

    @Test
    public void testSplitAt() {
        var res = l1.splitAt(5);

        System.out.println(res);

        assertEquals(list(1,2,3,4,5).toString(), res._1.toString());
        assertEquals(list(6,7,8,9).toString(), res._2.toString());
    }

    @Test
    public void testStartsWith() {
        var res = startsWith(l1, list(1,2,3));

        assertTrue(res);
    }

    @Test
    public void testHasSubList() {
        var res = hasSubList(l1, list(5,6,7));

        assertTrue(res);
    }

    @Test
    public void testUnfold() {
        var l2 = unfold(1, i -> i < 10 ? Result.success(Tuple.create(i,i + 1)) : Result.empty());

        System.out.println(l2.toString());

        assertEquals(l1.toString(), l2.toString());
    }

    @Test
    public void testExists() {
        var res = l1.exists(i -> i == 5);

        assertTrue(res);
    }

    @Test
    public void testForAll() {
        var res = l1.forAll(i -> i > 0);

        assertTrue(res);
    }


    @Test
    public void testDivide() {
        var res = l1.divide(2);

        var res1 = l1.divide(4);
        System.out.println("Divided lists:" + res.toString());
        System.out.println("More divided lists: " + res1.toString());

        assertEquals(list(list(1, 2, 3, 4), list(5, 6, 7, 8, 9)).toString(), res.toString());
        assertEquals(list(list(1,2), list(3,4), list(5,6), list(7,8,9)).toString(), res1.toString());
    }

    @Test
    public void testParFold() {
        int testLimit = 35000;
        int numThreads = 8;
        Random rd = new Random();

        List<Integer> testList = unfold(0, i -> i < testLimit ? Result.success(Tuple.create(0, i +1)) :
                Result.empty()).map(v -> (int)(Math.random() * 100));

        //Result<BigInteger> result
    }

    @Test
    public void testParMap() {
        ExecutorService es = Executors.newFixedThreadPool(8);
        var res = l1.parMap(es, v -> v *30);

        assertEquals(list(30,60,90,120,150,180,210,240,270).toString(), res.getOrElse(list()).toString());
    }

    //TOOD: Write benchhmark tests for Parallel fold and parallel map.
}

