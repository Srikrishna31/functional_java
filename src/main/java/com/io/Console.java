package com.io;

import com.functional.Nothing;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Console {
    private static BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

    public static IO<String> readLine(Nothing nothing) {
        return new IO.Suspend<>(() -> {
            try {
                return bufferedReader.readLine();
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        });
    }

    public static IO<String> readLine() {
        return readLine(Nothing.instance);
    }
    public static IO<Nothing> printLine(Object o) {
        return new IO.Suspend<>(() -> {
            System.out.println(o.toString());
            return Nothing.instance;
        });
    }
}
