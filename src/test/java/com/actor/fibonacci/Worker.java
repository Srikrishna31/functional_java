package com.actor.fibonacci;

import com.actor.AbstractActor;
import com.actor.Actor;
import com.functional.TailCall;
import com.util.Result;

public class Worker extends AbstractActor<Integer> {
    public Worker(String id, Type type) {
        super(id, type);
    }

    @Override
    public void onReceive(Integer message, Result<Actor<Integer>> sender) {
        /**
         * When the Worker receives a number, it reacts by computing the
         * corresponding Fibonacci value and sending it back to the caller.
         */
        sender.forEach(a -> a.tell(fibo(message), self()));
    }

    private static int fibo(int number) {
        class FiboHelper {
            TailCall<Integer> go(int acc1, int acc2, int x) {
                if (x == 0) {
                    return TailCall.ret(1);
                } else if (x == 1) {
                    return TailCall.ret(acc1 + acc2);
                } else {
                    return TailCall.sus(() -> go(acc2, acc1 + acc2, x -1));
                }
            }
        }

        /**
         * Intentional usage of inefficient fibonacci number calculation.
         */
        return new FiboHelper().go(0, 1, number).eval();
    }
}
