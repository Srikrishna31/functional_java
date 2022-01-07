package com.io;

import com.functional.Tuple;
import com.util.Result;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ConsoleReader extends AbstractReader {
    protected ConsoleReader(BufferedReader reader) {
        super(reader);
    }

    @Override
    public Result<Tuple<String, Input>> readString(String message) {
        System.out.println(message + " ");
        return readString();
    }

    @Override
    public Result<Tuple<Integer, Input>> readInt(String message) {
        System.out.println(message + " ");
        return readInt();
    }

    public static ConsoleReader consoleReader() {
        return new ConsoleReader(new BufferedReader(new InputStreamReader(System.in)));
    }
}
