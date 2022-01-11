package com.actor.pingpong;

import com.actor.AbstractActor;
import com.actor.Actor;
import com.util.Result;

class Player extends AbstractActor<Integer> {
    private final String sound;
    private final Actor<Integer> referee;

    public Player(String id, String sound, Actor<Integer> referee) {
        super(id, Actor.Type.SERIAL);
        this.referee = referee;
        this.sound = sound;
    }

    @Override
    public void onReceive(Integer message, Result<Actor<Integer>> sender) {
        System.out.println(sound + " - " + message);
        if (message >= 10) {
            referee.tell(message, sender);
        } else {
            sender.forEachOrFail(actor -> actor.tell(message + 1, self()))
                    .forEach(ignore -> referee.tell(message, sender));
        }
    }
}
