package com.util;

import com.functional.Function;
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

    @Test
    public void testFoldLeft() {
        List<Integer> list0 = List.list(4, 2, 1, 3, 6, 5, 7);
        List<Integer> list1 = RBTree.tree(list0).foldLeft(List.list(), list -> a -> list.cons(a),
                x -> y -> List.concat(y, x));
        assertEquals(List.list(7,6,5,4,3,2,1).toString(), list1.toString());
    }

    @Test
    public void testFoldRight() {
        List<Integer> list0 = List.list(4, 2, 1, 3, 6, 5, 7);
        var tree = RBTree.tree(list0);
        List<Integer> list1 = tree.foldRight(List.list(), a -> list -> list.cons(a),
                x -> y -> List.concat(x, y));
        assertTrue(RBTree.isValidTree(tree));
        assertEquals(List.list(2, 1, 5, 4, 3, 6, 7).toString(), list1.toString());
    }

    @Test
    public void testFoldInOrder_inOrderLeft() {
        Function<String, Function<Integer, Function<String, String>>> f = s1 -> i -> s2 -> s1 + i + s2;
        RBTree<Integer> tree = RBTree.tree(4, 2, 1, 3, 6, 5, 7);
        assertTrue(RBTree.isValidTree(tree));
        assertEquals("1234567", tree.foldInOrderLeft("", f));
    }

    @Test
    public void testFoldInOrder_preOrderLeft() {
        Function<String, Function<Integer, Function<String, String>>> f = s1 -> i -> s2 -> i + s1 + s2;
        var tree = RBTree.tree(4, 2, 1, 3, 6, 5, 7);
        assertTrue(RBTree.isValidTree(tree));
        assertEquals("2154367", tree.foldInOrderLeft("", f));
    }

    @Test
    public void testFoldInOrder_postOrderLeft() {
        Function<String, Function<Integer, Function<String, String>>> f = s1 -> i -> s2 -> s1 + s2 + i;
        var tree = RBTree.tree(4, 2, 1, 3, 6, 5, 7);
        assertTrue(RBTree.isValidTree(tree));
        assertEquals("1347652", tree.foldInOrderLeft("", f));
    }

    @Test
    public void testFoldInOrder_inOrderRight() {
        Function<String, Function<Integer, Function<String, String>>> f = s1 -> i -> s2 -> s2 + i + s1;
        var tree = RBTree.tree(4, 2, 1, 3, 6, 5, 7);
        assertTrue(RBTree.isValidTree(tree));
        assertEquals("7654321", tree.foldInOrderLeft("", f));
    }

    @Test
    public void testFoldInOrder_preOrderRight() {
        Function<String, Function<Integer, Function<String, String>>> f = s1 -> i -> s2 -> s2 + s1 + i;
        var tree = RBTree.tree(4, 2, 1, 3, 6, 5, 7);
        assertTrue(RBTree.isValidTree(tree));
        assertEquals("7634512", tree.foldInOrderLeft("", f));
    }

    @Test
    public void testFoldInOrder_postOrderRight() {
        Function<String, Function<Integer, Function<String, String>>> f = s1 -> i -> s2 -> i + s2 + s1;
        var tree = RBTree.tree(4, 2, 1, 3, 6, 5, 7);
        assertTrue(RBTree.isValidTree(tree));
        assertEquals("2567431", tree.foldInOrderLeft("", f));
    }

    @Test
    public void testFoldPreOrder_preOrderLeft() {
        Function<Integer, Function<String, Function<String, String>>> f = i -> s1 -> s2 -> i + s1 + s2;
        var tree = RBTree.tree(4, 2, 1, 3, 6, 5, 7);
        assertTrue(RBTree.isValidTree(tree));
        assertEquals("2154367", tree.foldPreOrderLeft("", f));
    }

    @Test
    public void testFoldPreOrder_inOrderLeft() {
        Function<Integer, Function<String, Function<String, String>>> f = i -> s1 -> s2 -> s1 + i + s2;
        var tree = RBTree.tree(4, 2, 1, 3, 6, 5, 7);
        assertTrue(RBTree.isValidTree(tree));
        assertEquals("1234567", tree.foldPreOrderLeft("", f));
    }

    @Test
    public void testFoldPreOrder_preOrderRight() {
        Function<Integer, Function<String, Function<String, String>>> f = i -> s1 -> s2 -> i + s2 + s1;
        var tree = RBTree.tree(4, 2, 1, 3, 6, 5, 7);
        assertTrue(RBTree.isValidTree(tree));
        assertEquals("2567431", tree.foldPreOrderLeft("", f));
    }

    @Test
    public void testFoldPreOrder_postOrderRight() {
        Function<Integer, Function<String, Function<String, String>>> f = i -> s1 -> s2 -> s2 + s1 + i;
        var tree = RBTree.tree(4, 2, 1, 3, 6, 5, 7);
        assertTrue(RBTree.isValidTree(tree));
        assertEquals("7634512", tree.foldPreOrderLeft("", f));
    }

    @Test
    public void testFoldPreOrder_inOrderRight() {
        Function<Integer, Function<String, Function<String, String>>> f = i -> s1 -> s2 -> s2 + i + s1;
        var tree = RBTree.tree(4, 2, 1, 3, 6, 5, 7);
        assertTrue(RBTree.isValidTree(tree));
        assertEquals("7654321", tree.foldPreOrderLeft("", f));
    }

    @Test
    public void testFoldPreOrder_postOrderLeft() {
        Function<Integer, Function<String, Function<String, String>>> f = i -> s1 -> s2 -> s1 + s2 + i;
        var tree = RBTree.tree(4, 2, 1, 3, 6, 5, 7);
        assertTrue(RBTree.isValidTree(tree));
        assertEquals("1347652", tree.foldPreOrderLeft("", f));
    }

    @Test
    public void testFoldPostOrder_postOrderLeft() {
        Function<String, Function<String, Function<Integer, String>>> f = s1 -> s2 -> i -> s1 + s2 + i;
        var tree = RBTree.tree(4, 2, 1, 3, 6, 5, 7);
        assertEquals("1347652", tree.foldPostOrderLeft("", f));
    }

    @Test
    public void testFoldPostOrder_postOrderRight() {
        Function<String, Function<String, Function<Integer, String>>> f = s1 -> s2 -> i -> s2 + s1 + i;
        var tree = RBTree.tree(4, 2, 1, 3, 6, 5, 7);
        assertEquals("7634512", tree.foldPostOrderLeft("", f));
    }

    @Test
    public void testFoldPostOrder_inOrderLeft() {
        Function<String, Function<String, Function<Integer, String>>> f = s1 -> s2 -> i -> s1 + i + s2;
        var tree = RBTree.tree(4, 2, 1, 3, 6, 5, 7);
        assertEquals("1234567", tree.foldPostOrderLeft("", f));
    }

    @Test
    public void testFoldPostOrder_preOrderRight() {
        Function<String, Function<String, Function<Integer, String>>> f = s1 -> s2 -> i -> i + s2 + s1;
        var tree = RBTree.tree(4, 2, 1, 3, 6, 5, 7);
        assertEquals("2567431", tree.foldPostOrderLeft("", f));
    }

    @Test
    public void testFoldPostOrder_preOrderLeft() {
        Function<String, Function<String, Function<Integer, String>>> f = s1 -> s2 -> i -> i + s1 + s2;
        var tree = RBTree.tree(4, 2, 1, 3, 6, 5, 7);
        assertEquals("2154367", tree.foldPostOrderLeft("", f));
    }

    @Test
    public void testFoldPostOrder_inOrderRight() {
        Function<String, Function<String, Function<Integer, String>>> f = s1 -> s2 -> i -> s2 + i + s1;
        var tree = RBTree.tree(4, 2, 1, 3, 6, 5, 7);
        assertTrue(RBTree.isValidTree(tree));
        assertEquals("7654321", tree.foldPostOrderLeft("", f));
    }

    //TODO: Uncomment and fix after adding the join/merge and remove functions to RBTree.
//    @Test
//    public void testTreeFold1() {
//        List<Integer> list = List.list(4, 6, 7, 5, 2, 1, 3);
//        var tree0 = RBTree.tree(list);
//        var tree1 = tree0.foldInOrderLeft(RBTree.<Integer>empty(), t1 -> i -> t2 -> RBTree.tree(t1, i, t2));
//        assertEquals(tree0.toString(), tree1.toString());
//        var tree2 = tree0.foldPostOrderLeft(RBTree.<Integer>empty(), t1 -> t2 -> i -> RBTree.tree(t1, i, t2));
//        assertEquals(tree0.toString(), tree2.toString());
//        var tree3 = tree0.foldPreOrderLeft(RBTree.<Integer>empty(), i -> t1 -> t2 -> RBTree.tree(t1, i, t2));
//        assertEquals(tree0.toString(), tree3.toString());
//    }

//    @Test
//    public void testMergeLeftEmptyNok() {
//        var tree = RBTree.tree(4, 6, 7, 5, 2, 1, 0);
//        var result = RBTree.tree(RBTree.empty(), 4, tree);
//        assertEquals("(T (T (T (T E 0 E) 1 E) 2 E) 4 (T (T E 5 E) 6 (T E 7 E)))", result.toString());
//    }
//
//    @Test
//    public void testMergeRightEmpty() {
//        var tree = RBTree.tree(4, 6, 7, 5, 2, 1, 0);
//        var result = RBTree.tree(tree, 4, RBTree.<Integer>empty());
//        assertEquals("(T (T (T (T E 0 E) 1 E) 2 E) 4 (T (T E 5 E) 6 (T E 7 E)))", result.toString());
//    }
//
//    @Test
//    public void testMergeNok() {
//        var tree1 = RBTree.tree(4, 6, 7, 5, 2);
//        var tree2 = RBTree.tree(7, 5, 2, 1, 0);
//        var result = RBTree.tree(tree1, 4, tree2);
//        assertEquals("(T (T (T (T E 0 E) 1 E) 2 E) 4 (T (T E 5 E) 6 (T E 7 E)))", result.toString());
//    }
//
//    @Test
//    public void testMergeEmpty() {
//        var result = RBTree.tree(RBTree.<Integer>empty(), 4, RBTree.<Integer>empty());
//        assertEquals("(T B E 4 E)", result.toString());
//    }
//
//    @Test
//    public void testMerge() {
//        var tree1 = RBTree.tree(2, 1, 3);
//        var tree2 = RBTree.tree(6, 5, 7);
//        var result = RBTree.tree(tree1, 4, tree2);
//        assertEquals("(T (T (T E 1 E) 2 (T E 3 E)) 4 (T (T E 5 E) 6 (T E 7 E)))", result.toString());
//        assertEquals("[4, 2, 1, 3, 6, 5, 7, NIL]", result.foldPreOrderLeft(List.<Integer>list(),
//                i -> l1 -> l2 -> List.list(i).concat(l1).concat(l2)).toString());
//    }
//
//    @Test
//    public void testMergeInverseOrder() {
//        var tree1 = RBTree.tree(2, 1, 3);
//        var tree2 = RBTree.tree(6, 5, 7);
//        var result = RBTree.tree(tree2, 4, tree1);
//        assertEquals("(T (T (T E 1 E) 2 (T E 3 E)) 4 (T (T E 5 E) 6 (T E 7 E)))", result.toString());
//        assertEquals("[4, 2, 1, 3, 6, 5, 7, NIL]", result.foldPreOrderLeft(List.<Integer>list(),
//                i -> l1 -> l2 -> List.list(i).concat(l1).concat(l2)).toString());
//    }
//
//    @Test
//    public void testMap() {
//        var tree = RBTree.tree(2,1,3);
//        var res = tree.map(Double::valueOf);
//
//        System.out.println(res);
//
//        assertEquals("(T (T E 1.0 E) 2.0 (T E 3.0 E))", res.toString());
//    }

}

