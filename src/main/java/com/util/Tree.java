package com.util;


import static com.util.List.list;

public abstract class Tree<A extends Comparable<A>> {
    @SuppressWarnings("rawtypes")
    private static Tree EMPTY = new Empty();

    public abstract A value();

    abstract Tree<A> left();
    abstract Tree<A> right();

    public abstract Tree<A> insert(A value);

    public abstract boolean member(A value);

    public abstract int size();

    public abstract int height();

    public abstract Result<A> max();

    public abstract Result<A> min();

    public abstract Tree<A> remove(A a);

    public abstract boolean isEmpty();

    protected abstract Tree<A> removeMerge(Tree<A> that);

    public abstract Tree<A> merge(Tree<A> that);

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
        @SuppressWarnings("unchecked")
        public Tree<A> insert(A value) {
            return new T<>(EMPTY, value, EMPTY);
        }

        @Override
        public boolean member(A value) {
            return false;
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
        public Result<A> max() {
            return Result.empty();
        }

        @Override
        public Result<A> min() {
            return Result.empty();
        }

        @Override
        public Tree<A> remove(A a) {
            throw new IllegalStateException("remove() called on empty tree");
        }

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        protected Tree<A> removeMerge(Tree<A> that) {
            return that;
        }

        @Override
        public Tree<A> merge(Tree<A> that) {
            return that;
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
            return this.value.compareTo(value) > 0 ? new T<>(left.insert(value), this.value, right) :
                    this.value.compareTo(value) < 0 ? new T<>(left, this.value, right.insert(value)) :
                            new T<>(left, value, right);
        }

        @Override
        public boolean member(A value) {
            return value.compareTo(this.value) < 0 ? left.member(value) :
                    value.compareTo(this.value) == 0 || right.member(value);
        }

        @Override
        public int size() {
            return left.size() + right.size() + 1;
        }

        @Override
        public int height() {
            return Math.max(left.height(), right.height()) + 1;
        }

        @Override
        public Result<A> max() {
            return right.max().orElse(() -> Result.success(value));
        }

        @Override
        public Result<A> min() {
            return left.min().orElse(() -> Result.success(value));
        }

        @Override
        public Tree<A> remove(A a) {
            if (a.compareTo(value) < 0) {
                return new T<>(left.remove(a), value, right);
            } else if (a.compareTo(value) > 0) {
                return new T<>(left, value, right.remove(a));
            } else {
                return left.removeMerge(right);
            }
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        protected Tree<A> removeMerge(Tree<A> that) {
            if (that.isEmpty()) {
                return this;
            }
            if (that.value().compareTo(value) < 0) {
                return new T<>(left.removeMerge(that), value, right);
            }

            if(that.value().compareTo(value) > 0) {
                return new T<>(left, value, right.removeMerge(that));
            }

            throw new IllegalStateException("We shouldn't be here");
        }

        @Override
        public Tree<A> merge(Tree<A> that) {
            if (that.isEmpty()) {
                return this;
            } else if (that.value().compareTo(value()) > 0) {
                return new T<>(left, value,
                        right.merge(new T<>(empty(), that.value(), that.right())).merge(that.left()));
            } else if (that.value().compareTo(value()) < 0) {
                return new T<>(left.merge(new T<>(that.left(), that.value(), empty())), value,
                        right).merge(that.right());
            } else {
                return new T<>(that.left().merge(left()), value, that.right().merge(right()));
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static <A extends Comparable<A>> Tree<A> empty() {
        return EMPTY;
    }

    @SafeVarargs
    public static <A extends Comparable<A>> Tree<A> tree(A... as) {
        return tree(list(as));
    }

    public static <A extends Comparable<A>> Tree<A> tree(List<A> as) {
        return as.foldLeft(Tree.empty(), acc -> acc::insert);
    }
}
