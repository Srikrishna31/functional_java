package com.state;

import com.functional.Tuple;
import com.functional.Function;

import com.util.List;

public interface Random<A> extends Function<RNG, Tuple<A, RNG>>{
    Random<Integer> integer = RNG::nextInt;

    static <A> Random<A> unit(A a) {
        return rng -> Tuple.create(a, rng);
    }

    static <A, B> Random<B> map(Random<A> a, Function<A, B> f) {
        return rng -> {
            var res = a.apply(rng);
            return Tuple.create(f.apply(res._1), res._2);
        };
    }

    Random<Boolean> booleanRnd = Random.map(integer, i -> i % 2 == 0);
    
    Random<Double> doubleRnd = Random.map(integer, d -> d / (((double) Integer.MAX_VALUE) + 1.0));

    static <A, B, C> Random<C> map2(Random<A> a, Random<B> b, Function<A, Function<B, C>> f) {
        return rng -> {
            var res1 = a.apply(rng);
            var res2 = b.apply(res1._2);

            return Tuple.create(f.apply(res1._1).apply(res2._1), res2._2);
        };
    }

    Random<Tuple<Integer, Integer>> intPairRnd = map2(integer, integer, x -> y -> Tuple.create(x,y));
    
    static <A> Random<List<A>> sequence(List<Random<A>> rs) {
        return rs.foldLeft(unit(List.list()), acc -> v -> map2(acc, v, ls -> l -> ls.cons(l)));
    }

    Function<Integer, Random<List<Integer>>> integersRnd = length -> sequence(List.fill(length, () -> integer));
}


