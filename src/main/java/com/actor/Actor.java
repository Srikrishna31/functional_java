package com.actor;

import com.util.Result;

public interface Actor<T> {
    /**
     * The noSender method is a helper method to provide a Result.Empty with
     * the Result<Actor> type.
     * @param <T>
     * @return
     */
    static <T> Result<Actor<T>> noSender() {
        return Result.empty();
    }

    /**
     * The self method returns a reference to this actor.
     * @return
     */
    Result<Actor<T>> self();

    ActorContext<T> getContext();

    /**
     * This is a convenience method to simplify sending messages without having
     * to indicate the sender.
     * @param message
     */
    default void tell (T message) {
        tell(message, self());
    }

    void tell(T message, Result<Actor<T>> sender);

    void shutdown();

    default void tell(T message, Actor<T> sender) {
        tell(message, Result.of(sender));
    }

    /**
     * In some specific cases, Actors can be configured to be multithreaded.
     */
    enum Type {SERIAL, PARALLEL};
}
