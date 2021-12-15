package com.lazy;

import org.junit.Test;

public class StreamTest {
    @Test
    public void testStream() {
        var intStrema = Stream.from(1);

        System.out.println(intStrema.head());
        System.out.println(intStrema.tail().head());
        System.out.println(intStrema.tail().tail().head());
    }
}
