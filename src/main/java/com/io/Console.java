package com.io;

import com.functional.Nothing;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Console {
    private static BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

    public static IO<String> readLine(Nothing nothing) {
        return () -> {
            try {
                return bufferedReader.readLine();
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        };
    }

    public static IO<Nothing> printLine(Object o) {
        return () -> {
            System.out.println(o.toString());
            return Nothing.instance;
        };
    }
}
