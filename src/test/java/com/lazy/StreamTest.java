package com.lazy;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import static com.util.List.list;

public class StreamTest {
    @Test
    public void testStream() {
        var intStrema = Stream.from(1);

        System.out.println(intStrema.head());
        System.out.println(intStrema.tail().head());
        System.out.println(intStrema.tail().tail().head());
    }

    @Test
    public void testTake() {
        var intStream = Stream.from(1);

        var stream = intStream.take(10);

        assertEquals(stream.toList().toString(), list(1,2,3,4,5,6,7,8,9,10).toString());
    }

    @Test
    public void testDropWhile() {
        var intStream = Stream.from(1);

        assertEquals(intStream.drop(5).take(5).toList().toString(), list(6,7,8,9,10).toString());
    }

    @Test
    public void testTakeWhile() {
        var intStream = Stream.from(1);

        assertEquals(intStream.takeWhile(i -> i < 6).toList().toString(), list(1,2,3,4,5).toString());
    }
}
