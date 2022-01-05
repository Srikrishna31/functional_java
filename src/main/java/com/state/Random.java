package com.state;

import com.functional.Tuple;
import com.functional.Function;

import com.util.List;

/**
 * A utility interface to random number generation. This interface defines
 * a set of utility functions, that allow one to do repeatable comptuations
 * on the Random numbers. All the functions in this interface return a function
 * taking the RNG as a parameter, so the clients can plug any Random number
 * generator (based on various implementations).
 * @param <A>: The type parameter of the Random elements.
 */
public class Random<A> extends State<A, RNG> {
    public Random(Function<RNG, Tuple<A, RNG>> run) {
        super(run);
    }
    
    public static State<Integer, RNG> integer = new Random<>(RNG::nextInt);


    public static State<Boolean, RNG> booleanRnd = integer.map(i -> i % 2 == 0);
    
    public static State<Double, RNG> doubleRnd = integer.map(d -> d / (((double) Integer.MAX_VALUE) + 1.0));

    public static State<Tuple<Integer, Integer>, RNG> intPairRnd = integer.map2(integer, x -> y -> Tuple.create(x,y));

    public static Function<Integer, State<List<Integer>, RNG>> integersRnd = length -> sequence(List.fill(length,
            () -> integer));

    public static State<Integer, RNG> notMultipleOfFiveRnd = integer.flatMap(x -> {
        int mod = x % 5;
        return mod != 0 ? unit(x) : Random.notMultipleOfFiveRnd;
    });
}


