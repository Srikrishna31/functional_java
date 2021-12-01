package com.functional;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import java.math.BigInteger;

import static com.functional.TailCall.ret;
import static com.functional.TailCall.sus;

/**
 * This test provides two ways of using TailCall class. One is a function
 * directly returning TailCall object, and letting the client call the eval
 * function explicitly on it.
 * The other is to use the regular signature, and use a helper inner class
 * to evaluate the call. In this case, the client can expect to call the function
 * in a normal way and get the results. The downside to this approach is to create
 * another Helper class, and creating an object of it, since java doesn't support
 * static functions in inner classes.
 */
public class TailCallTest {
    @Test
    public void testTailCall() {
        var call = add(5, 7);
        var result = call.eval();
        System.out.println(result);

        assertEquals(result, Integer.valueOf(12));
    }

    /**
     * A simple add method, that uses tailcall to optimize the usage of stack - use heap
     * for recursion.
     * Please note that, this method itself has not evaluated anything, and will
     * return a sinle Suspend / Return object. When the eval method is called, that is
     * when we will get a series of Suspend objects, and the actual evaluation of the
     * call takes place.
     * @param x : first number to be added
     * @param y : second number to be added
     * @return a TailCall object holding the steps of computation.
     */
    private static TailCall<Integer> add(int x, int y) {
        return y == 0
                // In terminal condition a Return is returned
                ? ret(x)
                // In non terminal condition, a Suspend is returned.
                : sus(() -> add(x+1, y - 1));
    }

    /**
     * Defining a function inside a function isn't directly possible in Java. But a function
     * defined as a lambda is a class. Can you define a local function in that class? You
     * can't use a static function, because a local class can't have static members, and
     * anyway, they have no name. Can you use an instance function? No, because you need a
     * reference to this. And one of the differences between lambdas and anonymous classes
     * is the `this` reference. Instead of referring to the anonymous class instance, the
     * `this` reference used in a lambda refers to the enclosing instance.
     * The solution is to declare a local class containing an instance function, as shown below:
     */
    private static Function<Integer, Function<Integer, Integer>> add = x -> y -> {
        class AddHelper {
             Function<Integer, Function<Integer, TailCall<Integer>>> addHelper =
                    a -> b -> b == 0 ? ret(a) : sus(() -> this.addHelper.apply(a + 1).apply(b - 1));
        }

        return new AddHelper().addHelper.apply(x).apply(y).eval();
    };

    @Test
    public void testEnclosedTailCall() {
        var res = add.apply(5).apply(10000);
        System.out.println(res);

        assertEquals(res, Integer.valueOf(10005));
    }

    @Test
    public void testTailRecursiveFibonacci() {
        var res = fib(10000);
        System.out.println(fib(10000));

        //currently there is no way of initializing big numbers, so use the same function call.
        assertEquals(res, fib(10000));
    }

    private static BigInteger fib(int  x) {
        return fib_(BigInteger.ONE, BigInteger.ZERO, BigInteger.valueOf(x)).eval();
    }

    private static TailCall<BigInteger> fib_(BigInteger acc1, BigInteger acc2, BigInteger n) {
        if (n.equals(BigInteger.ZERO)) {
            return ret(BigInteger.ZERO);
        } else if (n.equals(BigInteger.ONE)) {
            return ret(acc1.add(acc2));
        }

        return sus(() -> fib_(acc2, acc1.add(acc2), n.subtract(BigInteger.ONE)));
    }
}
