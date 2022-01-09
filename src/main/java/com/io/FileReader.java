package com.io;

import com.util.Result;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FileReader extends AbstractReader {
    private FileReader(BufferedReader reader) {
        super(reader);
    }

    public static Result<Input> fileReader(String path) {
        try {
            return fileReader(new FileInputStream(path));
        } catch (Exception e) {
            return Result.failure(e);
        }
    }

    public static Result<Input> fileReader(InputStream stream) {
        try {
            return Result.success(new FileReader(new BufferedReader(new InputStreamReader(stream))));
        } catch (Exception e) {
            return Result.failure(e);
        }
    }


}
