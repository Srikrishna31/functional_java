package com.util;

import static com.functional.Case.match;
import static com.functional.Case.mcase;

import static com.util.List.list;
import static com.util.Result.success;

import com.functional.Function;
import com.functional.QuadFunction;
import com.functional.TailCall;

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

    public abstract Result<A> max();
    public abstract Result<A> min();

    public abstract int size();
    public abstract int height();

    @Override
    public abstract String toString();

    private final QuadFunction<Color, RBTree<A>, A, RBTree<A>, Result<RBTree<A>>> balancer =
            (Color color, RBTree<A> left, A value, RBTree<A> right) -> match(
                    /**
                     * Default case is no balancing. Just instantiate a tree with the
                     * provided arguments.
                     */
                    mcase(() -> success(new T<>(color, left, value, right))),
                    /**
                     * Check if the left and the left child of left are both red.
                     * If so, return a right rotated tree, with left as root, left's left
                     * subtree as subtree of new root(left), the left's right subtree as
                     * new root's right's left child, and the right parameter as the right
                     * subtree of the right child of the new root.
                     */
                    mcase(() -> color.isB() && left.isTR() && left.left().isTR(),
                            () -> success(new T<>(R, new T<>(B, left.left().left(), left.left().value(),
                                    left.left().right()),
                                    left.value(), new T<>(B, left.right(), value, right)))),
                    /**
                     * Check if the left and the right child of left are both red.
                     * If so, return a left rotated tree, with the right subtree of the left
                     * child as the root, left subtree of left child as the left subtree,
                     * the value, together with right subtree of right child of left and right parameter
                     * together forming the right subtree.
                     */
                    mcase(() -> color.isB() && left.isTR() && left.right().isTR(),
                            () ->success(new T<>(R, new T<>(B, left.left(), left.value(), left.right().left()),
                                    left.right().value(),
                                    new T<>(B, left.right().right(), value, right)))),
                    /**
                     * Check if the right and the left child of right are both red.
                     */
                    mcase(() -> color.isB() && right.isTR() && right.left().isTR(),
                            () -> success(new T<>(R, new T<>(B, left, value, right.left().left()), right.left().value(),
                                    new T<>(B,
                                    right.left().right(), right.value(), right.right())))),
                    /**
                     * Check if the right and the right child of right are both red.
                     */
                    mcase(() -> color.isB() && right.isTR() && right.right().isTR(),
                            () -> success(new T<>(R, new T<>(B, left, value, right.left()), right.value(), new T<>(B,
                                    right.right().left(),
                                    right.right().value(), right.right().right()))))
            );

    @SuppressWarnings("unchecked")
    RBTree<A> balance(Color color, RBTree<A> left, A value, RBTree<A> right) {
        return balancer.apply(color,left, value, right).getOrElse(this);
    }

    abstract RBTree<A> ins(A value);

    /**
     * This function implements the balancing algorithm for RBTrees:
     * Following are invariants:
     * -> An empty tree is black.
     * -> The left and right subtrees of a red tree are black. In other words, it is not
     * possbile to find two successive reds while descending the tree.
     * -> Every path from the root to an empty subtree has the same number of blacks.
     * Insertion proceeds as follows:
     * -> An empty tree is always black.
     * -> Insertion proper is done exactly as in an ordinary tree, but is followed by balancing.
     * -> Inserting an element into an empty tree produces a red tree.
     * -> After balancing, the root is blackened.
     * In essence, RBTrees have the property to a one red node, which indicates the need 
     * for balancing the tree in the next insertion, if the insertion happens to touch the 
     * already inserted red node(which means the newly inserted red node becomes the immediate
     * child of the red node).
     * @param value : to be inserted into the tree.
     * @return the tree with the value inserted at the correct place.
     */
    public RBTree<A> insert(A value) {
        return blacken(ins(value));
    }

    public abstract <B> B foldLeft(B identity, Function<B, Function<A, B>> f, Function<B, Function<B, B>> g);

    public abstract <B> B foldRight(B identity, Function<A, Function<B, B>> f, Function<B, Function<B, B>> g);

    public abstract <B> B foldInOrderLeft(B identity, Function<B, Function<A, Function<B, B>>> f);

    public abstract <B> B foldPreOrderLeft(B identity, Function<A, Function<B, Function<B, B>>> f);

    public abstract <B> B foldPostOrderLeft(B identity, Function<B, Function<B, Function<A, B>>> f);

    public abstract <B> B foldInOrderRight(B identity, Function<B, Function<A, Function<B, B>>> f);

    public abstract <B> B foldPreOrderRight(B identity, Function<A, Function<B, Function<B, B>>> f);

    public abstract <B> B foldPostOrderRight(B identity, Function<B, Function<B, Function<A, B>>> f);

    static <A extends Comparable<A>> RBTree<A> blacken(RBTree<A> t) {
        return t.isEmpty() ? empty() : new T<>(B, t.left(), t.value(), t.right());
    }

    public List<A> toList() {
        return foldLeft(list(), l -> l::cons, a-> b-> a.concat(b));
    }

    //TODO: Uncomment this function once the join functions have been written out
//    public <B extends Comparable<B>> RBTree<B> map(Function<A, B> f) {
//        return foldInOrderLeft(empty(), t1 -> a -> t2 -> tree(t1, f.apply(a), t2));
//    }

    public static <A extends Comparable<A>> boolean lt(A first, A second) {
        return first.compareTo(second) < 0;
    }

    public static <A extends Comparable<A>> boolean lt(A first, A second, A third) {
        return lt(first, second) && lt(second, third);
    }

    public static <A extends Comparable<A>> boolean ordered(RBTree<A> left, A value, RBTree<A> right) {
        return left.max().flatMap(lMax -> right.min().map(rMin -> lt(lMax, value, rMin))).getOrElse(left.isEmpty() && right.isEmpty())
                || left.min().mapEmpty().flatMap(ignore -> right.min().map(rMin -> lt(value, rMin))).getOrElse(false)
                || right.min().mapEmpty().flatMap(ignore -> left.max().map(lMax -> lt(lMax, value))).getOrElse(false);
    }

    public abstract RBTree<A> merge(RBTree<A> that);

    protected abstract RBTree<A> rotateLeft();
    protected abstract RBTree<A> rotateRight();

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
        public Result<A> max() {
            return Result.empty();
        }

        @Override
        public Result<A> min() {
            return Result.empty();
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

        @Override
        public <B> B foldLeft(B identity, Function<B, Function<A, B>> f, Function<B, Function<B, B>> g) {
            return identity;
        }

        @Override
        public <B> B foldRight(B identity, Function<A, Function<B, B>> f, Function<B, Function<B, B>> g) {
            return identity;
        }

        @Override
        public <B> B foldInOrderLeft(B identity, Function<B, Function<A, Function<B, B>>> f) {
            return identity;
        }

        @Override
        public <B> B foldPreOrderLeft(B identity, Function<A, Function<B, Function<B, B>>> f) {
            return identity;
        }

        @Override
        public <B> B foldPostOrderLeft(B identity, Function<B, Function<B, Function<A, B>>> f) {
            return identity;
        }

        @Override
        public <B> B foldInOrderRight(B identity, Function<B, Function<A, Function<B, B>>> f) {
            return identity;
        }

        @Override
        public <B> B foldPreOrderRight(B identity, Function<A, Function<B, Function<B, B>>> f) {
            return identity;
        }

        @Override
        public <B> B foldPostOrderRight(B identity, Function<B, Function<B, Function<A, B>>> f) {
            return identity;
        }

        @Override
        public RBTree<A> merge(RBTree<A> that) { return that; }

        @Override
        public RBTree<A> rotateLeft() {
            return this;
        }

        @Override
        public RBTree<A> rotateRight() {
            return this;
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
        public Result<A> max() {
            return right.max().orElse(() -> Result.success(value));
        }

        @Override
        public Result<A> min() {
            return left.min().orElse(() -> Result.success(value));
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

        @Override
        public <B> B foldLeft(B identity, Function<B, Function<A, B>> f, Function<B, Function<B, B>> g) {
            /**
             * There can be 6 possible implementations:
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
            /**
             * There can be 6 possible implementations:
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
        public <B> B foldInOrderLeft(B identity, Function<B, Function<A, Function<B, B>>> f) {
            return f.apply(left.foldInOrderLeft(identity, f)).apply(value).apply(right.foldInOrderLeft(identity, f));
        }

        @Override
        public <B> B foldInOrderRight(B identity, Function<B, Function<A, Function<B, B>>> f) {
            return f.apply(right.foldInOrderRight(identity, f)).apply(value).apply(left.foldInOrderRight(identity, f));
        }

        @Override
        public <B> B foldPreOrderLeft(B identity, Function<A, Function<B, Function<B, B>>> f) {
            return f.apply(value).apply(left.foldPreOrderLeft(identity, f)).apply(right.foldPreOrderLeft(identity, f));
        }

        @Override
        public <B> B foldPreOrderRight(B identity, Function<A, Function<B, Function<B, B>>> f) {
            return f.apply(value).apply(right.foldPreOrderRight(identity, f)).apply(left.foldPreOrderRight(identity,
                    f));
        }

        @Override
        public <B> B foldPostOrderLeft(B identity, Function<B, Function<B, Function<A, B>>> f) {
            return f.apply(left.foldPostOrderLeft(identity, f)).apply(right.foldPostOrderLeft(identity, f)).apply(value);
        }

        @Override
        public <B> B foldPostOrderRight(B identity, Function<B, Function<B, Function<A, B>>> f) {
            return f.apply(right.foldPostOrderRight(identity, f)).apply(left.foldPostOrderRight(identity, f)).apply(value);
        }

        @Override
        public RBTree<A> merge(RBTree<A> that) {
            if (that.isEmpty()) {
                return this;
            }

            if (that.value().compareTo(value) > 0) {
                return balance(R, left, value,
                        right.merge(balance(R, empty(), that.value(), that.right()))).merge(that.left());
            }
            if (that.value().compareTo(value) < 0) {
                return balance(R, left.merge(balance(R, that.left(), that.value(),
                        empty())), value,
                        right).merge(that.right());
            }

            return balance(R, left.merge(that.left()), value, right.merge(that.right()));
        }

        @Override
        protected RBTree<A> rotateLeft() {
            if (right.isEmpty()) {
                 return this;
            }

            return balance(R, balance(R, left, value, right.left()), right.value(), right.right());
        }

        @Override
        protected RBTree<A> rotateRight() {
            if (left.isEmpty()) {
                return this;
            }

            return balance(R, left.left(), left.value(), balance(R, left.right(), value, right));
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

    @SafeVarargs
    public static <A extends Comparable<A>> RBTree<A> tree(A... as) {
        return tree(list(as));
    }

    public static <A extends Comparable<A>> RBTree<A> tree(List<A> as) {
        return as.foldLeft(empty(), acc -> acc::insert);
    }


    //TODO: Write the join algorithm for RBTrees, which is described in this paper :
    // https://www.cs.cmu.edu/~guyb/papers/BFS16.pdf
    // and here: https://stackoverflow.com/questions/43697546/best-way-to-join-two-red-black-trees
    // and here: https://en.wikipedia.org/wiki/Red%E2%80%93black_tree#Set_operations_and_bulk_operations
//    public static <A extends Comparable<A>> RBTree<A> tree(RBTree<A> t1, A a, RBTree<A> t2) {
//        var heightLeft = t1.height();
//        var heightRight = t2.height();
//
//        return heightLeft > heightRight + 1 ? joinRight(t1, a, t2) : heightRight > heightLeft + 1 ? joinLeft(t1, a,
//                t2) : new T<>()
//        return ordered(t1, a, t2)
//                ? E.balance(R, t1, a, t2)
//                : ordered(t2, a, t1)
//                ? E.balance(R, t2, a, t1)
//                : RBTree.<A>empty().insert(a).merge(t1).merge(t2);
//    }

    private static <A extends Comparable<A>> boolean areRedNodesInOrder(RBTree<A> tree) {
        return tree.isEmpty()
                ? true
                : (tree.isR() && tree.left().isB()) || (tree.isR() && tree.right().isB()) || tree.isB()
                ? areRedNodesInOrder(tree.left()) && areRedNodesInOrder(tree.right())
                : false;
    }

    /**
     * Given a redblack tree, checks if all the constraints for the redblack tree
     * are satisfied.
     * @param tree : The tree to be checked for validity
     * @param <A> : Type parameter of the elements of the tree
     * @return true if the tree is a valid redblack tree, false otherwise.
     */
    public static <A extends Comparable<A>> boolean isValidTree(RBTree<A> tree) {
        var res = areRedNodesInOrder(tree);

        if (res) {
            var paths =paths(tree);
            var blackCounts = paths.map(s -> s.chars().filter(ch -> ch == 'B').count());
            return blackCounts.foldLeft(true, false, acc -> v -> v.equals(blackCounts.head()))._1;
        }

        return res;
    }

    private static <A extends Comparable<A>> List<String> paths(RBTree<A> tree) {
        //This method is identical in structure to fold, but since this operates
        // on the metadata of the tree, fold cannot be used.
        if (tree.isEmpty()) {
            return list("B");
        }

        var leftPaths = paths(tree.left());
        var rightPaths = paths(tree.right());

        return leftPaths.concat(rightPaths).map(s -> s + (tree.isB() ? "B" : "R"));
    }

    //TODO: Write the functional ne removal algorithm for RBTrees described here:
    // https://matt.might.net/articles/red-black-delete/
}

