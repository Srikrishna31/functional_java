package com.io;

import org.junit.Test;

import com.util.Result;

public class TestReader {
    @Test
    public void testReader() {
        Input input = ConsoleReader.consoleReader();
        Result<String> rString = input.readString("Enter your name: ").map(t -> t._1);

        var result = rString.map(s -> String.format("Hello, %s!",s));

        //The pattern from Result is applied to output either the result
        //or an error message.
        result.forEachOrFail(System.out::println).forEach(System.out::println);
    }
}
