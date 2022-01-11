package com.actor;

public interface ActorContext<T> {
    /**
     * The become method allows an actor to change its behavior by registering a
     * new MessageProcessor.
     * @param behavior
     */
    void become(MessageProcessor<T> behavior);

    MessageProcessor<T> getBehavior();
}
