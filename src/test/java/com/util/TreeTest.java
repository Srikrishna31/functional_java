package com.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
}
