package com.util;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RBTreeTest {

    /*
     * Adjust according to your environment. The faster the computer,
     * the lower this value should be.
     */
    int timeFactor = 100;

    @Test
    public void testInsertOrderedAscending7() {
        int limit = 7;
        List<Integer> list = List.range(1, limit + 1);
        RBTree<Integer> tree = list.foldLeft(RBTree.<Integer>empty(), t -> t::insert);
        assertEquals(limit, tree.size());
        assertTrue(tree.height() <= 2 * log2nlz(tree.size() + 1));
    }

    @Test
    public void testInsertOrderedDescending7() {
        int limit = 7;
        List<Integer> list = List.iterate(limit, x -> x - 1, limit);
        RBTree<Integer> tree = list.foldLeft(RBTree.<Integer>empty(), t -> t::insert);
        assertEquals(limit, tree.size());
        assertTrue(tree.height() <= 2 * log2nlz(tree.size() + 1));
    }

    @Test
    public void testInsertRandom7() {
        List<Integer> list = List.list(2, 5, 7, 3, 6, 1, 4);
        RBTree<Integer> tree = list.foldLeft(RBTree.<Integer>empty(), t -> t::insert);
        assertTrue(tree.height() <= 2 * log2nlz(tree.size() + 1));
    }

    @Test
    public void testInsertOrderedAscending() {
        int limit = 2_000_000;
        long maxTime = 2L * log2nlz(limit + 1) * timeFactor;
        List<Integer> list = List.range(1, limit + 1);
        long time = System.currentTimeMillis();
        RBTree<Integer> tree = list.foldLeft(RBTree.<Integer>empty(), t -> t::insert);
        long duration = System.currentTimeMillis() - time;
        System.out.println("Duration: " + duration + ", expected time: " + maxTime);
        //asserting on time makes this an unstable test, so comment it out for now.
//        assertTrue(duration < maxTime);
        assertEquals(limit, tree.size());
        assertTrue(tree.height() <= 2 * log2nlz(tree.size() + 1));
    }

    @Test
    public void testInsertOrderedDescending() {
        int limit = 2_000_000;
        long maxTime = 2L * log2nlz(limit + 1) * timeFactor;
        List<Integer> list = List.iterate(limit, x -> x - 1, limit);
        long time = System.currentTimeMillis();
        RBTree<Integer> tree = list.foldLeft(RBTree.<Integer>empty(), t -> t::insert);
        long duration = System.currentTimeMillis() - time;
        assertEquals(limit, tree.size());
        assertTrue(tree.height() <= 2 * log2nlz(tree.size() + 1));
        //asserting on time makes this an unstable test, so comment it out for now.
        //assertTrue(duration < maxTime);
    }

//    @Test
//    public void testInsertRandom() {
//        int limit = 2_000_000;
//        long maxTime = 2L * log2nlz(limit + 1) * timeFactor;
//        //TODO: After completing the State chapter, uncomment this test to use the
//        // RNG defined there.
//        List<Integer> list = SimpleRNG.doubles(limit, new SimpleRNG.Simple(3))._1.map(x -> (int) (x * limit * 3));
//        long time = System.currentTimeMillis();
//        RBTree<Integer> tree = list.foldLeft(Tree.<Integer>empty(), t -> t::insert);
//        long duration = System.currentTimeMillis() - time;
//        assertTrue(tree.height() <= 2 * log2nlz(tree.size() + 1));
          //asserting on time makes this an unstable test, so comment it out for now.
//        //assertTrue(duration < maxTime);
//    }

    static int log2nlz(int n) {
        return n == 0
                ? 0
                : 31 - Integer.numberOfLeadingZeros(n);
    }
}
