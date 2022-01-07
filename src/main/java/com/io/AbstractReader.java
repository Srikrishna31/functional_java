package com.io;

import com.functional.Tuple;
import com.util.Result;

import java.io.BufferedReader;

public class AbstractReader implements Input {
    protected final BufferedReader reader;

    /**
     * The class will be built with a reader, allowing for
     * different sources of input.
     * @param reader
     */
    protected AbstractReader(BufferedReader reader) {
        this.reader = reader;
    }

    @Override
    public Result<Tuple<String, Input>> readString() {
        try {
            String s = reader.readLine();
            return s.length() == 0 ? Result.empty()
                    : Result.success(Tuple.create(s, this));
        } catch (Exception e) {
            return Result.failure(e);
        }
    }

    @Override
    public Result<Tuple<Integer, Input>> readInt() {
        try {
            String s = reader.readLine();
            return s.length() == 0 ? Result.empty()
                    : Result.success(Tuple.create(Integer.parseInt(s), this));
        } catch (Exception e) {
            return Result.failure(e);
        }
    }

}
