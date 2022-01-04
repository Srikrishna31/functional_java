package com.state;
import com.functional.Tuple;
import com.util.List;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class JavaRNGTest {
    @Test
    public void testInteger() {
        RNG rng = JavaRNG.rng(0);
        Tuple<Integer, RNG> t1 = RNG.integer(rng);
        assertEquals(Integer.valueOf(-1155484576),  t1._1);
        var t2 = RNG.integer(t1._2);
        assertEquals(Integer.valueOf(-723955400), t2._1);
        var t3 = RNG.integer(t2._2);
        assertEquals(Integer.valueOf(1033096058), t3._1);
    }

    @Test
    public void testIntegers() {
        RNG rng = JavaRNG.rng(0);
        Tuple<List<Integer>, RNG> result = Generator.integers(rng, 3);
        assertEquals(List.list(1033096058, -723955400, -1155484576), result._1);
    }

    @Test
    public void testIntegersLength1() {
        RNG rng = JavaRNG.rng(0);
        Tuple<List<Integer>, RNG> result = Generator.integers(rng, 1);
        assertEquals(List.list(-1155484576), result._1);
    }

    @Test
    public void testIntegersLength0() {
        RNG rng = JavaRNG.rng(0);
        Tuple<List<Integer>, RNG> result = Generator.integers(rng, 0);
        assertEquals(0, result._1.length());
    }

    @Test
    public void testIntegersNegativeLength() {
        RNG rng = JavaRNG.rng(0);
        Tuple<List<Integer>, RNG> result = Generator.integers(rng, -3);
        assertEquals(0, result._1.length());
    }
}
