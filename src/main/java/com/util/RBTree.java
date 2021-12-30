package com.util;

import static com.functional.Case.match;
import static com.functional.Case.mcase;

import static com.util.Result.success;

import com.functional.QuadFunction;
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

    private final QuadFunction<Color, RBTree<A>, A, RBTree<A>, Result<RBTree<A>>> balancer =
            (Color color, RBTree<A> left, A value, RBTree<A> right) -> match(
                    mcase(() -> success(new T<>(color, left, value, right))),
                    mcase(() -> color.isB() && left.isTR() && left.left().isTR(),
                            () -> success(new T<>(R, new T<>(B, left.left().left(), left.left().value(),
                                    left.left().right()),
                                    left.value(), new T<>(B, left.right(), value, right)))),
                    mcase(() -> color.isB() && left.isTR() && left.right().isTR(),
                            () ->success(new T<>(R, new T<>(B, left.left(), left.value(), left.right().left()),
                                    left.right().value(),
                                    new T<>(B, left.right().right(), value, right)))),
                    mcase(() -> color.isB() && right.isTR() && right.left().isTR(),
                            () -> success(new T<>(R, new T<>(B, left, value, right.left().left()), right.left().value(),
                                    new T<>(B,
                                    right.left().right(), right.value(), right.right())))),
                    mcase(() -> color.isB() && right.isTR() && right.right().isTR(),
                            () -> success(new T<>(R, new T<>(B, left, value, right.left()), right.value(), new T<>(B,
                                    right.right().left(),
                                    right.right().value(), right.right().right()))))
            );

    RBTree<A> balance(Color color, RBTree<A> left, A value, RBTree<A> right) {
        return balancer.apply(color,left, value, right).getOrElse(this);
    }

    abstract RBTree<A> ins(A value);

    public RBTree<A> insert(A value) {
        return blacken(ins(value));
    }

    static <A extends Comparable<A>> RBTree<A> blacken(RBTree<A> t) {
        return t.isEmpty() ? empty() : new T<>(B, t.left(), t.value(), t.right());
    }

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

        @Override
        RBTree<A> ins(A value) {
            return new T<>(R, empty(), value, empty());
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

        @Override
        RBTree<A> ins(A value) {
            return value.compareTo(this.value) < 0
                    ? balance(color, left.ins(value), this.value, right)
                    : value.compareTo(this.value) > 0
                        ? balance(color, left, this.value, right.ins(value))
                        : this;
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

