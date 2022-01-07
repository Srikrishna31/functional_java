package com.io;

import com.functional.Tuple;
import com.util.Result;

import java.io.BufferedReader;

public class AbstractReader implements Input {
    protected final BufferedReader reader;

    public AbstractReader(BufferedReader reader) {
        this.reader = reader;
    }

    @Override
    public Result<Tuple<String, Input>> readString() {
        return Result.empty();
    }

    @Override
    public Result<Tuple<Integer, Input>> readInt() {
        return Result.empty();
    }

}
