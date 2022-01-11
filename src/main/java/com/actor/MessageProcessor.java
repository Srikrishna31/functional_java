package com.actor;

import com.util.Result;

public interface MessageProcessor<T> {
    /**
     * The MessageProcessor interface has only one method, which represents the
     * processing of one message.
     * @param t
     * @param sender
     */
    void process(T t, Result<Actor<T>> sender);
}
