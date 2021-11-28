package com.functional;

import org.junit.Test;

import static com.functional.TailCall.ret;
import static com.functional.TailCall.sus;

public class TailCallTest {
    @Test
    public void testTailCall() {
        var call = add(5, 7);

        while(call.isSuspend()) {
            call = call.resume();
        }

        System.out.println(call.eval());
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
}
