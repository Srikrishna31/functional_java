package com.state;
import com.functional.Tuple;
import com.util.List;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


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

    @Test
    public void testBoolean() {
        RNG rng = JavaRNG.rng(0);
        Tuple<Boolean, RNG> result1 = Random.booleanRnd.apply(rng);
        assertTrue(result1._1);
        Tuple<Boolean, RNG> result2 = Random.booleanRnd.apply(result1._2);
        assertTrue(result2._1);
        Tuple<Boolean, RNG> result3 = Random.booleanRnd.apply(result2._2);
        assertTrue(result3._1);
    }

    @Test
    public void testDoubleRnd() {
        RNG rng = JavaRNG.rng(0);
        Tuple<Double, RNG> result1 = Random.doubleRnd.apply(rng);
        assertTrue(Math.abs(-0.5380644351243973 - result1._1) < 0.0001);
        Tuple<Double, RNG> result2 = Random.doubleRnd.apply(result1._2);
        assertTrue(Math.abs(-0.3371180035173893 - result2._1) < 0.0001);
        Tuple<Double, RNG> result3 = Random.doubleRnd.apply(result2._2);
        assertTrue(Math.abs(0.48107284028083086 - result3._1) < 0.0001);
    }

    @Test
    public void testMap2() {
        RNG rng = JavaRNG.rng(0);
        Tuple<Tuple<Integer, Integer>, RNG> result = Random.intPairRnd.apply(rng);
        assertEquals(Integer.valueOf(-1155484576), result._1._1);
        assertEquals(Integer.valueOf(-723955400), result._1._2);
        Tuple<Integer, RNG> t = Random.integer.apply(result._2);
        assertEquals(Integer.valueOf(1033096058), t._1);
    }

    @Test
    public void testSequence() {
        RNG rng = JavaRNG.rng(0);
        Tuple<List<Integer>, RNG> result = Random.integersRnd.apply(3).apply(rng);
        assertEquals(List.list(1033096058, -723955400, -1155484576), result._1);
        Tuple<Integer, RNG> t = Random.integer.apply(result._2);
        assertEquals(Integer.valueOf(-1690734402), t._1);
    }

    @Test
    public void testSequenceLength1() {
        RNG rng = JavaRNG.rng(0);
        Tuple<List<Integer>, RNG> result = Random.integersRnd.apply(1).apply(rng);
        assertEquals(List.list(-1155484576), result._1);
        Tuple<Integer, RNG> t = Random.integer.apply(result._2);
        assertEquals(Integer.valueOf(-723955400), t._1);
    }

    @Test
    public void testSequenceLength0() {
        RNG rng = JavaRNG.rng(0);
        Tuple<List<Integer>, RNG> result = Random.integersRnd.apply(0).apply(rng);
        assertEquals(0, result._1.length());
        Tuple<Integer, RNG> t = Random.integer.apply(result._2);
        assertEquals(Integer.valueOf(-1155484576), t._1);
    }

    @Test
    public void testSequenceNegativeLength() {
        RNG rng = JavaRNG.rng(0);
        Tuple<List<Integer>, RNG> result = Random.integersRnd.apply(-3).apply(rng);
        assertEquals(0, result._1.length());
        Tuple<Integer, RNG> t = Random.integer.apply(result._2);
        assertEquals(Integer.valueOf(-1155484576), t._1);
    }

    @Test
    public void testFlatMap() {
        RNG rng = JavaRNG.rng(0);
        Tuple<List<Integer>, RNG> result =
                Random.sequence(List.fill(300, () -> Random.notMultipleOfFiveRnd)).apply(rng);
        assertTrue(result._1.forAll(i -> i % 5 != 0));
    }
}
