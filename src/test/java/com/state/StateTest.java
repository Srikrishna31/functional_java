package com.state;

import com.util.List;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

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

    /**
     * This class tests the Atm example, which shows the sample implementation of
     * a statemachine.
     */
    @Test
    public void testSimulateMachine1() {
        var outcome = test(List.list());
        System.out.println(test(List.list()).toString());
        assertEquals("(0, [NIL])", test(List.list()).toString());
    }

    @Test
    public void testSimulateMachine2() {
        assertEquals("(100, [100, NIL])", test(List.list(new Deposit(100))).toString());
    }

    @Test
    public void testSimulateMachine3() {
        assertEquals("(50, [-50, 100, NIL])", test(List.list(new Deposit(100), new WithDraw(50))).toString());
    }

    @Test
    public void testSimulateMachine4() {
        assertEquals("(50, [-50, 100, NIL])",
                test(List.list(new Deposit(100), new WithDraw(50), new WithDraw(150))).toString());
    }

    @Test
    public void testSimulateMachine5() {
        assertEquals("(100, [-150, 200, -50, 100, NIL])", test(List.list(new Deposit(100), new WithDraw(50),
                new WithDraw(150), new Deposit(200), new WithDraw(150))).toString());
    }

    private Outcome test(List<Input> inputs) {
        return Atm.createMachine().process(inputs).eval(new Outcome(0, List.list()));
    }
}
