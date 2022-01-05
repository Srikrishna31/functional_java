package com.state;

import org.junit.Test;

class Point {
    public final int x;
    public final int y;
    public final int z;

    public Point(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public String toString() {
        return String.format("Point(%s, %s, %s)", x, y, z);
    }
}
public class StateTest {
    @Test
    public void testState() {
        var ns = Random.integer.flatMap(x ->
                                        Random.integer.flatMap(y ->
                                                Random.integer.map(z ->
                                                        new Point(x,y,z))));
        var pt = ns.run.apply(JavaRNG.rng(0))._1;

        System.out.println(pt);
    }
}
