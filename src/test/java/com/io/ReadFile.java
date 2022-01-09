package com.io;

import com.functional.Tuple;
import com.lazy.Stream;
import com.util.Result;
import org.junit.Test;

public class ReadFile {
    private static String path = "/src/test/resources/data.txt";

    @Test
    public void testReadFile() {
        String currentPath = System.getProperty("user.dir");
        Result<Input> rInput = FileReader.fileReader(path);
        Result<Stream<Person>> rStream = rInput.map(input -> Stream.unfold(input, ReadConsole::person));

        rStream.forEachOrFail(stream -> stream.toList().forEach(System.out::println)).forEach(System.out::println);
    }
}
