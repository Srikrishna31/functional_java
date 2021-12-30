package com.util;


import java.awt.*;

/**
 * A Red-Black tree is a binary search tree (BST) with some additions to its
 * structure and a modified insertion algorithm, which also balances the
 * result. It is a self-balancing, general-purpose tree with high performance.
 * It's suitable for general use and data sets of any size. In a red-black tree
 * each tree (including subtrees) has an additional property representing its
 * color.
 * @param <A> : The type parameter of the elements that will be stored in the tree.
 *           Note that the elements must implement Comparabel interface, since
 *           they will be ordered accordingly.
 */
public abstract class RBTree<A extends Comparable<A>> {
    private static RBTree E = new E();

    /**
     * Colors are used through static singletons.
     */
    private static Color R = new Red();
    private static Color B = new Black();

    /**
     * Methods are defined to test each characteristic of a tree (emptiness,
     * color and some combination of them)
     */
    protected abstract boolean isE();
    protected abstract boolean isT();
    protected abstract boolean isB();
    protected abstract boolean isR();
    protected abstract boolean isTB();
    protected abstract boolean isTR();

    public abstract boolean isEmpty();

    protected abstract RBTree<A> right();
    protected abstract RBTree<A> left();
    protected abstract A value();

    public abstract int size();
    public abstract int height();

    @Override
    public abstract String toString();

    /**
     * This class represents the empty node of a tree. It's just named E for
     * convenience.
     * @param <A>
     */
    private static class E<A extends Comparable<A>> extends RBTree<A> {

        @Override
        protected boolean isE() {
            return true;
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public int height() {
            return -1;
        }

        @Override
        public RBTree<A> right() {
            return E;
        }

        @Override
        public RBTree<A> left() {
            return E;
        }

        @Override
        protected A value() {
            throw new IllegalStateException("value called on Empty");
        }

        @Override
        protected boolean isR() {
            return false;
        }

        @Override
        protected boolean isT() {
            return false;
        }

        @Override
        protected boolean isB() {
            return true;
        }

        @Override
        protected boolean isTB() {
            return false;
        }

        @Override
        protected boolean isTR() {
            return false;
        }

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public String toString() {
            return "E";
        }
    }

    private static class T<A extends Comparable<A>> extends RBTree<A> {
        private final RBTree<A> left;
        private final RBTree<A> right;
        private final A value;
        private final Color color;
        private final int length;
        private final int height;

        private T(Color color, RBTree<A> left, A value, RBTree<A> right) {
            this.color = color;
            this.left = left;
            this.right = right;
            this.value = value;
            this.length = left.size() + 1 + right.size();
            this.height = Math.max(left.height(), right.height()) + 1;
        }

        @Override
        public boolean isR() {
            return color.isR();
        }

        @Override
        public boolean isB() {
            return color.isB();
        }

        @Override
        public boolean isTB() {
            return color.isB();
        }

        @Override
        public boolean isTR() {
            return color.isR();
        }

        @Override
        protected boolean isE() {
            return false;
        }

        @Override
        protected boolean isT() {
            return true;
        }

        @Override
        public int size() {
            return length;
        }

        @Override
        public int height() {
            return height;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        protected RBTree<A> right() {
            return right;
        }

        @Override
        protected RBTree<A> left() {
            return left;
        }

        @Override
        protected A value() {
            return value;
        }

        @Override
        public String toString() {
            return String.format("(T %s %s %s %s)", color, left, value, right);
        }
    }

    /**
     * The color classes Red and Black extend the Color abstract class.
     */
    private static abstract class Color {
        abstract boolean isR();
        abstract boolean isB();

        @Override
        public abstract String toString();
    }

    private static class Red extends Color {
        @Override
        boolean isR() {
            return true;
        }

        @Override
        boolean isB() {
            return false;
        }

        @Override
        public String toString() {
            return "R";
        }
    }

    private static class Black extends Color {
        @Override
        boolean isR() {
            return false;
        }

        @Override
        boolean isB() {
            return true;
        }

        @Override
        public String toString() {
            return "B";
        }
    }

    @SuppressWarnings("unchecked")
    public static <A extends Comparable<A>> RBTree<A> empty() {
        return E;
    }
}

