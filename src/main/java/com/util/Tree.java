package com.util;


public abstract class Tree<A extends Comparable<A>> {
    @SuppressWarnings("rawtypes")
    private static Tree EMPTY = new Empty();

    public abstract A value();

    abstract Tree<A> left();
    abstract Tree<A> right();

    public abstract Tree<A> insert(A value);

    public abstract boolean member(A value);

    @Override
    public abstract String toString();

    private static class Empty<A extends Comparable<A>> extends Tree<A> {
        @Override
        public A value() {
            throw new IllegalStateException("value() called on empty tree");
        }

        @Override
        Tree<A> left() {
            throw new IllegalStateException("left() called on empty tree");
        }

        @Override
        Tree<A> right() {
            throw new IllegalStateException("right() called on empty tree");
        }

        @Override
        public String toString() {
            return "E";
        }

        @Override
        public Tree<A> insert(A value) {
            return new T<>(EMPTY, value, EMPTY);
        }

        @Override
        public boolean member(A value) {
            return false;
        }
    }

    private static class T<A extends Comparable<A>> extends Tree<A> {
        private final Tree<A> left;
        private final Tree<A> right;
        private final A value;

        private T(Tree<A> left, A value, Tree<A> right) {
            this.left = left;
            this.value = value;
            this.right = right;
        }

        @Override
        public A value() {
            return value;
        }

        @Override
        Tree<A> left() {
            return left;
        }

        @Override
        Tree<A> right() {
            return right;
        }

        @Override
        public String toString() {
            return String.format("(T %s %s %s)", left, value, right);
        }

        @Override
        public Tree<A> insert(A value) {
            return this.value.compareTo(value) < 0 ? new T<>(left.insert(value), this.value, right) :
                    this.value.compareTo(value) > 0 ? new T<>(left, this.value, right.insert(value)) :
                            new T<>(left, value, right);
        }

        @Override
        public boolean member(A value) {
            return this.value.compareTo(value) < 0 ? left.member(value) :
                    this.value.compareTo(value) == 0 || right.member(value);
        }
    }

    @SuppressWarnings("unchecked")
    public static <A extends Comparable<A>> Tree<A> empty() {
        return EMPTY;
    }

    @SafeVarargs
    public static <A extends Comparable<A>> Tree<A> tree(A... as) {
        Tree<A> res = empty();

        for (A a : as) {
            res = res.insert(a);
        }

        return res;
    }

    public static <A extends Comparable<A>> Tree<A> tree(List<A> as) {
        return as.foldLeft(Tree.<A>empty(), acc -> a -> acc.insert(a));
    }
}
