package com.io;

import com.functional.Tuple;
import com.util.Result;

/**
 * An interface to model the input to functional programs.
 */
public interface Input {
    /**
     * Read a string from any source like console, file, database etc.
     * @return the string, and the modified Input object, which can read
     * subsequent values.
     */
    Result<Tuple<String, Input>> readString();

    /**
     * Read an int from any source like console, file, database etc.
     * @return the int, and the modified Input object, which can read
     * subsequent values.
     */
    Result<Tuple<Integer, Input>> readInt();

    /**
     * The below methods allow to pass a message as a parameter, which can be
     * useful for prompting the user, but the provided default implementations
     * ignore the message.
     * @param message
     * @return
     */
    default Result<Tuple<String, Input>> readString(String message) {
        return readString();
    }

    default Result<Tuple<Integer, Input>> readInt(String message) {
        return readInt();
    }
}
