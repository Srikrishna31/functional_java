package com.state;

import com.functional.Tuple;
import com.util.List;

public class Generator {
    public static Tuple<Integer, RNG> integer(RNG rng) {
        return rng.nextInt();
    }
    
    public static Tuple<Integer, RNG> integer(RNG rng, int limit) {
        var random = rng.nextInt();
        return Tuple.create(random._1 % limit, random._2);
    }
    
    public static Tuple<List<Integer>, RNG> integers(RNG rng, int length) {
        return List.iterate(0, i ->  0,length).foldLeft(Tuple.create(List.list(), rng), acc -> v -> {
            var res = acc._2.nextInt();
            return Tuple.create(acc._1.cons(res._1), res._2);
        });
    }
 }
