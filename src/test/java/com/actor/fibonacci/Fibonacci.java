package com.actor.fibonacci;

import com.actor.AbstractActor;
import com.actor.Actor;
import com.state.SimpleRNG;
import com.util.List;
import com.util.Result;
import org.junit.Test;

import java.util.concurrent.Semaphore;

public class Fibonacci {
    private static final Semaphore semaphore = new Semaphore(1);
    private static int listLength = 200_000;
    private static int workers = 8;
    private static final List<Integer> testList =
            SimpleRNG.doubles(listLength, new SimpleRNG.Simple(3))._1.map(x -> (int) (x * 30)).reverse();
    @Test
    public void testFibonacciActor() throws InterruptedException {
        semaphore.acquire();
        final var client =
                new AbstractActor<Result<List<Integer>>>("Client", Actor.Type.SERIAL) {
                    @Override
                    public void onReceive(Result<List<Integer>> message, Result<Actor<Result<List<Integer>>>> sender) {
                        message.forEachOrFail(Fibonacci::processSuccess)
                                .forEach(Fibonacci::processFailure);
                        semaphore.release();
                    }
                };
        final var manager = new Manager("Manager", testList, client, workers);
        manager.start();
        semaphore.acquire();
    }

    private static void processFailure(String s) {
        System.out.println(s);
    }

    private static void processSuccess(List<Integer> lst) {
        System.out.println("Result: " + lst.takeAtMost(40));
    }
}
