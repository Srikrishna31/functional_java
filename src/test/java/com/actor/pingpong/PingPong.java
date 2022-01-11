package com.actor.pingpong;

import com.actor.AbstractActor;
import com.actor.Actor;
import com.util.Result;
import org.junit.Test;

import java.util.concurrent.Semaphore;

public class PingPong {
    private static final Semaphore semaphore = new Semaphore(1);

    @Test
    public void testPingPongActor() throws InterruptedException {
        Actor<Integer> referee = new AbstractActor<Integer>("Referee", Actor.Type.SERIAL) {
            @Override
            public void onReceive(Integer message, Result<Actor<Integer>> sender) {
                System.out.println("Game ended after " + message + " shots");
                semaphore.release();
            }
        };

        Actor<Integer> player1 = new Player("Player1", "Ping", referee);
        Actor<Integer> player2 = new Player("Player2", "Pong", referee);

        semaphore.acquire();
        player1.tell(1, Result.success(player2));
        semaphore.acquire();
    }
}
