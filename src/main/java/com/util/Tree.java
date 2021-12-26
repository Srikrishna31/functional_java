package com.util;

import com.functional.Function;
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

    public abstract <B> B foldLeft(B identity, Function<B, Function<A, B>> f, Function<B, Function<B, B>> g);

    public abstract <B> B foldRight(B identity, Function<A, Function<B, B>> f, Function<B, Function<B, B>> g);

    public abstract <B> B foldInOrder(B identity, Function<B, Function<A, Function<B, B>>> f);

    public abstract <B> B foldPreOrder(B identity, Function<A, Function<B, Function<B, B>>> f);

    public abstract <B> B foldPostOrder(B identity, Function<B, Function<B, Function<A, B>>> f);

    public static <A extends Comparable<A>> boolean lt(A first, A second) {
        return first.compareTo(second) < 0;
    }

    public static <A extends Comparable<A>> boolean lt(A first, A second, A third) {
        return lt(first, second) && lt(second, third);
    }

    public static <A extends Comparable<A>> boolean ordered(Tree<A> left, A value, Tree<A> right) {
        return left.max().flatMap(lMax -> right.min().map(rMin -> lt(lMax, value, rMin))).getOrElse(left.isEmpty() && right.isEmpty())
                || left.min().mapEmpty().flatMap(ignore -> right.min().map(rMin -> lt(value, rMin))).getOrElse(false)
                || right.min().mapEmpty().flatMap(ignore -> left.max().map(lMax -> lt(lMax, value))).getOrElse(false);
    }

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

        @Override
        public <B> B foldLeft(B identity, Function<B, Function<A, B>> f, Function<B, Function<B, B>> g) {
            return identity;
        }

        @Override
        public <B> B foldRight(B identity, Function<A, Function<B, B>> f, Function<B, Function<B, B>> g) {
            return identity;
        }

        @Override
        public <B> B foldInOrder(B identity, Function<B, Function<A, Function<B, B>>> f) {
            return identity;
        }

        @Override
        public <B> B foldPreOrder(B identity, Function<A, Function<B, Function<B, B>>> f) {
            return identity;
        }

        @Override
        public <B> B foldPostOrder(B identity, Function<B, Function<B, Function<A, B>>> f) {
            return identity;
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
        public Tree<A> insert(A insertedValue) {
            return insertedValue.compareTo(value) < 0
                    ? new T<>(left.insert(insertedValue), value, right)
                    : insertedValue.compareTo(value) > 0
                        ? new T<>(left, value, right.insert(insertedValue))
                        : new T<>(left, insertedValue, right);
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
            }
            if (that.value().compareTo(value) > 0) {
                return new T<>(left, value,
                        right.merge(new T<>(empty(), that.value(), that.right()))).merge(that.left());
            }
            if (that.value().compareTo(value) < 0) {
                return new T<>(left.merge(new T<>(that.left(), that.value(), empty())), value,
                        right).merge(that.right());
            }
            return new T<>(left.merge(that.left()), value, right.merge(that.right()));
        }

        @Override
        public <B> B foldLeft(B identity, Function<B, Function<A, B>> f, Function<B, Function<B, B>> g) {
            /** There can be 6 possible implementations:
             * Post order left
             * Pre order left
             * In order left
             * Post order right
             * Pre order right
             * In order right
             */
            var leftFold = left.foldLeft(identity, f, g);
            var rightFold = right.foldLeft(identity, f, g);

            return g.apply(f.apply(leftFold).apply(value)).apply(rightFold);
        }

        @Override
        public <B> B foldRight(B identity, Function<A, Function<B, B>> f, Function<B, Function<B, B>> g) {
            /** There can be 6 possible implementations:
             * Post order left
             * Pre order left
             * In order left
             * Post order right
             * Pre order right
             * In order right
             */
            var leftFold = left.foldRight(identity, f, g);
            var rightFold = right.foldRight(identity, f, g);

            return g.apply(f.apply(value).apply(leftFold)).apply(rightFold);
        }

        @Override
        public <B> B foldInOrder(B identity, Function<B, Function<A, Function<B, B>>> f) {
            //Other possiblity is to use right fold first.
            return f.apply(left.foldInOrder(identity, f)).apply(value).apply(right.foldInOrder(identity, f));
        }

        @Override
        public <B> B foldPreOrder(B identity, Function<A, Function<B, Function<B, B>>> f) {
            //Other possiblity is to use right fold first.
            return f.apply(value).apply(left.foldPreOrder(identity, f)).apply(right.foldPreOrder(identity, f));
        }

        @Override
        public <B> B foldPostOrder(B identity, Function<B, Function<B, Function<A, B>>> f) {
            //Other possiblity is to use right fold first.
            return f.apply(left.foldPostOrder(identity, f)).apply(right.foldPostOrder(identity, f)).apply(value);
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
        return as.foldLeft(empty(), acc -> acc::insert);
    }

    public static <A extends Comparable<A>> Tree<A> tree(Tree<A> t1, A a, Tree<A> t2) {
        return ordered(t1, a, t2)
                ? new T<>(t1, a, t2)
                : ordered(t2, a, t1)
                    ? new T<>(t2, a, t1)
                    : Tree.<A>empty().insert(a).merge(t1).merge(t2);
    }
}
