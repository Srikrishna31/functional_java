package com.io;

import com.functional.Nothing;
import com.functional.Function;

import com.lazy.Stream;
import org.junit.Test;

public class IOTest {
    @Test
    public void testIO() {
        var s = Stream.fill(3, () -> IO.empty());
        IO program = IO.repeat(3, sayHello());

        program.run();
    }

    private static IO<Nothing> sayHello() {
        return Console.printLine("Enter your name: ")
                .flatMap(Console::readLine)
                .map(IOTest::buildMessage)
                .flatMap(Console::printLine);
    }

    private static String buildMessage(String name) {
        return String.format("Hello %s!", name);
    }

    //TODO: Fix the nullpointer exception, by figuring out how to provide console
    //input to the tests in bazel.

    @Test(expected = NullPointerException.class)
    public void testWhen() {
        IO program = program(buildWhenMessage, "Enter the names of the persons to welcome: ");

        program.run();
    }

    private static Function<String, IO<Boolean>> buildWhenMessage = name -> IO.when(name.length() != 0,
            () -> IO.unit(String.format("Hello, %s!", name)).flatMap(Console::printLine));

    private static IO<Nothing> program(Function<String, IO<Boolean>> f, String title) {
        return IO.sequence(
                Console.printLine(title),
                IO.doWhile(Console.readLine(Nothing.instance), f),
                Console.printLine("bye!")
        );
    }

    @Test
    public void testForever() {
        IO program = IO.forever(IO.unit("Hi again!")
                        .flatMap(Console::printLine));

        //This line will cause the printing go on forever.
        //program.run();
    }
}
