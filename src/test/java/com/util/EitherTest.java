package com.util;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import com.functional.Function;
import static com.util.List.list;

public class EitherTest {

    @Test
    public void testEither() {
        var res = max().apply(list());

        assertEquals(res.toString(), Either.left("max called on an empty list").toString());
    }

    private static <A extends Comparable<A>> Function<List<A>, Either<String, A>> max () {
        return xs -> xs.isEmpty() ? Either.left("max called on an empty list") :
                Either.right(xs.foldLeft(xs.head(), x -> y -> x.compareTo(y) < 0 ? x : y));
    }
}
