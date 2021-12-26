package com.util;

import org.junit.Test;

import com.functional.Function;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class TreeTest {
    private Tree<Integer> tree = Tree.tree(2,1,3);
    @Test
    public void testTree() {
        var tree = Tree.tree(2,1,3);

        System.out.println(tree.toString());

        assertEquals("(T (T E 1 E) 2 (T E 3 E))", tree.toString());
    }

    @Test
    public void testMember() {
        var tree = Tree.tree(2,3,1);

        assertTrue(tree.member(1));
        assertFalse(tree.member(6));
    }

    @Test
    public void testMax() {
        assertEquals(3, (int)tree.max().getOrElse(0));
        assertEquals(0, (int)Tree.<Integer>empty().max().getOrElse(0));
    }

    @Test
    public void testMin() {
        assertEquals(1, (int)tree.min().getOrElse(0));
        assertEquals(0, (int)Tree.<Integer>empty().min().getOrElse(0));
    }

    @Test
    public void testSize() {
        assertEquals(3, tree.size());
    }

    @Test
    public void testHeight() {
        assertEquals(1, tree.height());
    }

    @Test
    public void testRemove() {
        var res = tree.remove(2);

        System.out.println(res);

        assertEquals("(T E 1 (T E 3 E))", res.toString());
    }

    @Test
    public void testMerge2() {
        var tree2 = Tree.tree(5, 4, 6);
        var res = tree.merge(tree2);

        System.out.println(res);

        assertEquals("(T (T E 1 E) 2 (T E 3 (T (T E 4 E) 5 (T E 6 E))))", res.toString());
    }

    @Test
    public void testFoldLeft() {
        List<Integer> list0 = List.list(4, 2, 1, 3, 6, 5, 7);
        List<Integer> list1 = Tree.tree(list0).foldLeft(List.list(), list -> a -> list.cons(a),
                x -> y -> List.concat(y, x));
        assertEquals(List.list(7,6,5,4,3,2,1).toString(), list1.toString());
    }

    @Test
    public void testFoldRight() {
        List<Integer> list0 = List.list(4, 2, 1, 3, 6, 5, 7);
        List<Integer> list1 = Tree.tree(list0).foldRight(List.list(), a -> list -> list.cons(a),
                x -> y -> List.concat(x, y));
        assertEquals(list0.toString(), list1.toString());
    }

    @Test
    public void testFoldInOrder_inOrderLeft() {
        Function<String, Function<Integer, Function<String, String>>> f = s1 -> i -> s2 -> s1 + i + s2;
        Tree<Integer> tree = Tree.tree(4, 2, 1, 3, 6, 5, 7);
        assertEquals("1234567", tree.foldInOrderLeft("", f));
    }

    @Test
    public void testFoldInOrder_preOrderLeft() {
        Function<String, Function<Integer, Function<String, String>>> f = s1 -> i -> s2 -> i + s1 + s2;
        Tree<Integer> tree = Tree.tree(4, 2, 1, 3, 6, 5, 7);
        assertEquals("4213657", tree.foldInOrderLeft("", f));
    }

    @Test
    public void testFoldInOrder_postOrderLeft() {
        Function<String, Function<Integer, Function<String, String>>> f = s1 -> i -> s2 -> s1 + s2 + i;
        Tree<Integer> tree = Tree.tree(4, 2, 1, 3, 6, 5, 7);
        assertEquals("1325764", tree.foldInOrderLeft("", f));
    }

    @Test
    public void testFoldInOrder_inOrderRight() {
        Function<String, Function<Integer, Function<String, String>>> f = s1 -> i -> s2 -> s2 + i + s1;
        Tree<Integer> tree = Tree.tree(4, 2, 1, 3, 6, 5, 7);
        assertEquals("7654321", tree.foldInOrderLeft("", f));
    }

    @Test
    public void testFoldInOrder_preOrderRight() {
        Function<String, Function<Integer, Function<String, String>>> f = s1 -> i -> s2 -> s2 + s1 + i;
        Tree<Integer> tree = Tree.tree(4, 2, 1, 3, 6, 5, 7);
        assertEquals("7563124", tree.foldInOrderLeft("", f));
    }

    @Test
    public void testFoldInOrder_postOrderRight() {
        Function<String, Function<Integer, Function<String, String>>> f = s1 -> i -> s2 -> i + s2 + s1;
        Tree<Integer> tree = Tree.tree(4, 2, 1, 3, 6, 5, 7);
        assertEquals("4675231", tree.foldInOrderLeft("", f));
    }

    @Test
    public void testFoldPreOrder_preOrderLeft() {
        Function<Integer, Function<String, Function<String, String>>> f = i -> s1 -> s2 -> i + s1 + s2;
        Tree<Integer> tree = Tree.tree(4, 2, 1, 3, 6, 5, 7);
        assertEquals("4213657", tree.foldPreOrderLeft("", f));
    }

    @Test
    public void testFoldPreOrder_inOrderLeft() {
        Function<Integer, Function<String, Function<String, String>>> f = i -> s1 -> s2 -> s1 + i + s2;
        Tree<Integer> tree = Tree.tree(4, 2, 1, 3, 6, 5, 7);
        assertEquals("1234567", tree.foldPreOrderLeft("", f));
    }

    @Test
    public void testFoldPreOrder_preOrderRight() {
        Function<Integer, Function<String, Function<String, String>>> f = i -> s1 -> s2 -> i + s2 + s1;
        Tree<Integer> tree = Tree.tree(4, 2, 1, 3, 6, 5, 7);
        assertEquals("4675231", tree.foldPreOrderLeft("", f));
    }

    @Test
    public void testFoldPreOrder_postOrderRight() {
        Function<Integer, Function<String, Function<String, String>>> f = i -> s1 -> s2 -> s2 + s1 + i;
        Tree<Integer> tree = Tree.tree(4, 2, 1, 3, 6, 5, 7);
        assertEquals("7563124", tree.foldPreOrderLeft("", f));
    }

    @Test
    public void testFoldPreOrder_inOrderRight() {
        Function<Integer, Function<String, Function<String, String>>> f = i -> s1 -> s2 -> s2 + i + s1;
        Tree<Integer> tree = Tree.tree(4, 2, 1, 3, 6, 5, 7);
        assertEquals("7654321", tree.foldPreOrderLeft("", f));
    }

    @Test
    public void testFoldPreOrder_postOrderLeft() {
        Function<Integer, Function<String, Function<String, String>>> f = i -> s1 -> s2 -> s1 + s2 + i;
        Tree<Integer> tree = Tree.tree(4, 2, 1, 3, 6, 5, 7);
        assertEquals("1325764", tree.foldPreOrderLeft("", f));
    }

    @Test
    public void testFoldPostOrder_postOrderLeft() {
        Function<String, Function<String, Function<Integer, String>>> f = s1 -> s2 -> i -> s1 + s2 + i;
        Tree<Integer> tree = Tree.tree(4, 2, 1, 3, 6, 5, 7);
        assertEquals("1325764", tree.foldPostOrderLeft("", f));
    }

    @Test
    public void testFoldPostOrder_postOrderRight() {
        Function<String, Function<String, Function<Integer, String>>> f = s1 -> s2 -> i -> s2 + s1 + i;
        Tree<Integer> tree = Tree.tree(4, 2, 1, 3, 6, 5, 7);
        assertEquals("7563124", tree.foldPostOrderLeft("", f));
    }

    @Test
    public void testFoldPostOrder_inOrderLeft() {
        Function<String, Function<String, Function<Integer, String>>> f = s1 -> s2 -> i -> s1 + i + s2;
        Tree<Integer> tree = Tree.tree(4, 2, 1, 3, 6, 5, 7);
        assertEquals("1234567", tree.foldPostOrderLeft("", f));
    }

    @Test
    public void testFoldPostOrder_preOrderRight() {
        Function<String, Function<String, Function<Integer, String>>> f = s1 -> s2 -> i -> i + s2 + s1;
        Tree<Integer> tree = Tree.tree(4, 2, 1, 3, 6, 5, 7);
        assertEquals("4675231", tree.foldPostOrderLeft("", f));
    }

    @Test
    public void testFoldPostOrder_preOrderLeft() {
        Function<String, Function<String, Function<Integer, String>>> f = s1 -> s2 -> i -> i + s1 + s2;
        Tree<Integer> tree = Tree.tree(4, 2, 1, 3, 6, 5, 7);
        assertEquals("4213657", tree.foldPostOrderLeft("", f));
    }

    @Test
    public void testFoldPostOrder_inOrderRight() {
        Function<String, Function<String, Function<Integer, String>>> f = s1 -> s2 -> i -> s2 + i + s1;
        Tree<Integer> tree = Tree.tree(4, 2, 1, 3, 6, 5, 7);
        assertEquals("7654321", tree.foldPostOrderLeft("", f));
    }

    @Test
    public void testMergeLeftEmptyNok() {
        Tree<Integer> tree = Tree.tree(4, 6, 7, 5, 2, 1, 0);
        Tree<Integer> result = Tree.tree(Tree.empty(), 4, tree);
        assertEquals("(T (T (T (T E 0 E) 1 E) 2 E) 4 (T (T E 5 E) 6 (T E 7 E)))", result.toString());
    }

    @Test
    public void testMergeRightEmpty() {
        Tree<Integer> tree = Tree.tree(4, 6, 7, 5, 2, 1, 0);
        Tree<Integer> result = Tree.tree(tree, 4, Tree.<Integer>empty());
        assertEquals("(T (T (T (T E 0 E) 1 E) 2 E) 4 (T (T E 5 E) 6 (T E 7 E)))", result.toString());
    }

    @Test
    public void testMergeNok() {
        Tree<Integer> tree1 = Tree.tree(4, 6, 7, 5, 2);
        Tree<Integer> tree2 = Tree.tree(7, 5, 2, 1, 0);
        Tree<Integer> result = Tree.tree(tree1, 4, tree2);
        assertEquals("(T (T (T (T E 0 E) 1 E) 2 E) 4 (T (T E 5 E) 6 (T E 7 E)))", result.toString());
    }

    @Test
    public void testMergeEmpty() {
        Tree<Integer> result = Tree.tree(Tree.<Integer>empty(), 4, Tree.<Integer>empty());
        assertEquals("(T E 4 E)", result.toString());
    }

    @Test
    public void testMerge() {
        Tree<Integer> tree1 = Tree.tree(2, 1, 3);
        Tree<Integer> tree2 = Tree.tree(6, 5, 7);
        Tree<Integer> result = Tree.tree(tree1, 4, tree2);
        assertEquals("(T (T (T E 1 E) 2 (T E 3 E)) 4 (T (T E 5 E) 6 (T E 7 E)))", result.toString());
        assertEquals("[4, 2, 1, 3, 6, 5, 7, NIL]", result.foldPreOrderLeft(List.<Integer>list(),
                i -> l1 -> l2 -> List.list(i).concat(l1).concat(l2)).toString());
    }

    @Test
    public void testMergeInverseOrder() {
        Tree<Integer> tree1 = Tree.tree(2, 1, 3);
        Tree<Integer> tree2 = Tree.tree(6, 5, 7);
        Tree<Integer> result = Tree.tree(tree2, 4, tree1);
        assertEquals("(T (T (T E 1 E) 2 (T E 3 E)) 4 (T (T E 5 E) 6 (T E 7 E)))", result.toString());
        assertEquals("[4, 2, 1, 3, 6, 5, 7, NIL]", result.foldPreOrderLeft(List.<Integer>list(),
                i -> l1 -> l2 -> List.list(i).concat(l1).concat(l2)).toString());
    }

    @Test
    public void testTreeFold1() {
        List<Integer> list = List.list(4, 6, 7, 5, 2, 1, 3);
        Tree<Integer> tree0 = Tree.tree(list);
        Tree<Integer> tree1 = tree0.foldInOrderLeft(Tree.<Integer>empty(), t1 -> i -> t2 -> Tree.tree(t1, i, t2));
        assertEquals(tree0.toString(), tree1.toString());
        Tree<Integer> tree2 = tree0.foldPostOrderLeft(Tree.<Integer>empty(), t1 -> t2 -> i -> Tree.tree(t1, i, t2));
        assertEquals(tree0.toString(), tree2.toString());
        Tree<Integer> tree3 = tree0.foldPreOrderLeft(Tree.<Integer>empty(), i -> t1 -> t2 -> Tree.tree(t1, i, t2));
        assertEquals(tree0.toString(), tree3.toString());
    }

    @Test
    public void testMap() {
        var res = tree.map(Double::valueOf);

        System.out.println(res);

        assertEquals("(T (T E 1.0 E) 2.0 (T E 3.0 E))", res.toString());
    }
}
