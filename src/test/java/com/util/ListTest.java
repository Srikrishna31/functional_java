package com.util;

import static com.util.List.*;

import com.functional.Function;
import com.functional.TailCall;
import static com.functional.TailCall.sus;
import static com.functional.TailCall.ret;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class ListTest {
    @Test
    public void testList() {

        var list = List.list(1, 2, 3, 4);

        System.out.println(list);

        var list1 = List.<Integer>list();

        System.out.println(list1.dropWhile(x -> false));
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
        var l1 = list(1,2,3,4,5,6,7,8,9);

        class SumHelper{
            TailCall<Integer> go(List<Integer> ls, Integer sum) {
                return ls.isEmpty() ? ret(sum) : sus(() -> go(ls.tail(), sum + ls.head()));
            }
        }

        var res = new SumHelper().go(l1, 0).eval();
        System.out.println(res);

        Function<List<Integer>, Integer> add = ls -> foldRight(ls, 0, x -> y -> x +y);

        assertEquals(Integer.valueOf(45), res);
        assertEquals(res, add.apply(l1));
    }
}

