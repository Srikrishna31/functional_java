package com.util;

import com.functional.Function;
import static com.util.List.list;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class OptionTest {

    @Test
    public void testOption() {

        var res = max().apply(list());

        assertEquals(Option.none(), res);
    }

    /**
     * This max function is a total function, which is defined for an empty list as
     * well. In the case of an empty list, it returns a None value. This is much
     * better than throwing an exception or returning a special value like -1 etc.
     * @param <A>
     * @return
     */
    private static <A extends Comparable<A>> Function<List<A>, Option<A>> max() {
        return xs -> xs.isEmpty() ? Option.none() : Option.some(xs.foldLeft(xs.head(), x -> y -> x.compareTo(y) > 0 ?
                x : y));
    }

    @Test
    public void testGetOrElse() {
        int max1 = OptionTest.<Integer>max().apply(list(3,5,7,2,1)).getOrElse(() -> 0);
        int max2 = OptionTest.<Integer>max().apply(list()).getOrElse(() -> 0);

        assertEquals(max1, 7);
        assertEquals(max2,0);
    }
}
