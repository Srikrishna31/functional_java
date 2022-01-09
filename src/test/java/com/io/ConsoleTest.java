package com.io;

import com.io.Console;

import org.junit.Test;

public class ConsoleTest {
    @Test
    public void testConsole() {
        var io =  Console.printLine("Enter your name: ")
                .flatMap(Console::readLine)
                .map(ConsoleTest::buildMessage)
                .flatMap(Console::printLine);

        io.run();
    }

    private static String buildMessage(String name) {
        return String.format("Hello %s!", name);
    }
}
