package com.io;

import com.functional.Tuple;

import com.lazy.Stream;
import com.util.List;
import com.util.Result;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class ReadFile {
    private static String path = "/data.txt";

    static Result<Tuple<Person, Input>> person(Input input) {
        return input.readInt("Enter ID:")
                .flatMap(id -> id._2.readString("Enter first name: ")
                        .flatMap(firstName -> firstName._2.readString("Enter last name: ")
                                .map(lastName -> Tuple.create(Person.apply(id._1, firstName._1, lastName._1), lastName._2))));
    }

    @Test
    public void testReadFile() {

        String currentPath = System.getProperty("user.dir");
        Result<Input> rInput = FileReader.fileReader(getClass().getResourceAsStream(path));
        Result<Stream<Person>> rStream = rInput.map(input -> Stream.unfold(input, ReadFile::person));
        rStream.forEachOrFail(stream -> stream.toList().forEach(System.out::println)).forEach(System.out::println);
        var expected = List.list(Person.apply(1, "Mickey", "Mouse"),
                Person.apply(2, "Minnie", "Mouse"),
                Person.apply(3, "Donald", "Duck"),
                Person.apply(4, "Homer", "Simpson"));

        assertEquals(expected, rStream.getOrElse(Stream.empty()).toList());
    }
}
