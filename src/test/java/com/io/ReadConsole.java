package com.io;

import com.functional.Tuple;
import com.lazy.Stream;
import com.util.Result;
import org.junit.Test;

//TODO: Create a junit test to do the console test, which can probably automate it somehow.
class ReadConsole {
    static Result<Tuple<Person, Input>> person(Input input) {
        return input.readInt("Enter ID:")
                .flatMap(id -> id._2.readString("Enter first name: ")
                    .flatMap(firstName -> firstName._2.readString("Enter last name: ")
                            .map(lastName -> Tuple.create(Person.apply(id._1, firstName._1, lastName._1), lastName._2))));
    }

    @Test
    public void testReader() {
        Input input = ConsoleReader.consoleReader();
        Stream<Person> stream = Stream.unfold(input, ReadConsole::person);
        stream.toList().forEach(System.out::println);
    }
}
