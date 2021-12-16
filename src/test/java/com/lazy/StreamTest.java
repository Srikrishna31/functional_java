package com.lazy;

import com.functional.Tuple;
import com.util.List;
import com.util.Result;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import static com.util.List.list;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public class StreamTest {
    private Stream<Integer> intStream = Stream.from(1);

    @Test
    public void testStream() {
        System.out.println(intStream.head());
        System.out.println(intStream.tail().head());
        System.out.println(intStream.tail().tail().head());

        assertEquals(Integer.valueOf(1), intStream.head());
        assertEquals(Integer.valueOf(2), intStream.tail().head());
        assertEquals(Integer.valueOf(3), intStream.tail().tail().head());
    }

    @Test
    public void testTake() {

        var stream = intStream.take(10);

        assertEquals(stream.toList().toString(), list(1,2,3,4,5,6,7,8,9,10).toString());

        assertEquals(list(1,2,3,4,5).toString(), intStream.take(5).toList().toString());
    }

    @Test
    public void testDropWhile() {

        assertEquals(intStream.drop(5).take(5).toList().toString(), list(6,7,8,9,10).toString());
    }

    @Test
    public void testTakeWhile() {

        assertEquals(intStream.takeWhile(i -> i < 6).toList().toString(), list(1,2,3,4,5).toString());
    }

    @Test
    public void testFoldRight() {
        Stream<Integer> stream = Stream.cons(() -> 1,
                                        Stream.cons(() -> 2,
                                                Stream.cons(() -> 3,
                                                Stream.cons(() -> 4,
                                                        Stream.cons(() -> 5, Stream.<Integer>empty())))));
        
        BiFunction<Integer, Supplier<String>, String> addIS = (i, s) -> "(" + i + " + " + s.get() + ")";

        assertEquals("(1 + (2 + (3 + (4 + (5 + 0)))))", stream.foldRight(() -> "0", v -> acc -> addIS.apply(v, acc)));
    }

    @Test
    public void testExists() {
        assertTrue(intStream.exists(i -> i == 5));
    }

    @Test
    public void testMap() {
        var res = intStream.map(i -> i * 10).take(10);
        var expected =
                Stream.cons(() -> 10,
                        Stream.cons(()-> 20,
                                Stream.cons(() -> 30,
                                        Stream.cons(() -> 40,
                                                Stream.cons(() -> 50,
                                                        Stream.cons(() -> 60,
                                                                Stream.cons(() -> 70,
                                                                        Stream.cons(() -> 80,
                                                                                Stream.cons(() -> 90,
                                                                                        Stream.cons(() -> 100,
                                                                                                Stream.empty()))))))))));

        assertEquals(expected.toList().toString(), res.toList().toString());
    }

    @Test
    public void testFilter() {
        // overflowing the stack. Fix it.
        var res = intStream.filter(i -> i <= 15).take(14);
        var expected = List.unfold(1, i -> i < 15 ? Result.success(Tuple.create(i, i + 1)) : Result.empty());

        assertEquals(expected.toString(), res.toList().toString());
    }

    @Test
    public void testFlatMap() {
        assertEquals(list().toString(),
                Stream.<Integer>empty().flatMap(x -> Stream.from(x).take(x)).toList().toString());

        var stream = intStream.takeWhile(x -> x < 50_000);

        var res = stream.flatMap(x -> Stream.from(x).take(3));

        assertEquals(Integer.valueOf(1), res.head());
        assertEquals(Integer.valueOf(2), res.tail().head());
        assertEquals(Integer.valueOf(3), res.tail().tail().head());
    }

    private Stream<Integer> fibStream() {
        return Stream.iterate(Tuple.create(0, 1), t -> Tuple.create(t._2, t._2 + t._1)).map(x -> x._1);
    }

    @Test
    public void testFibStream() {
        var res = fibStream().take(20);

        System.out.println(res.toList().toString());
    }
}
