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

    private static class Toon {
        private final String firstName;
        private final String lastName;
        private final Option<String> email;

        Toon(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = Option.none();
        }

        Toon(String firstName, String lastName, String email) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = Option.some(email);
        }

        Option<String> getEmail() {
            return email;
        }
    }

    @Test
    public void testOptionComposition() {
        var toons = new Map<String, Toon>()
                .put("Mickey", new Toon("Mikey", "Mouse", "mickey@disney.com"))
                .put("Minnie", new Toon("Minnie", "Mouse"))
                .put("Donald", new Toon("Donald", "Duck", "donald@disney.com"));

        //Below code deomonstrates Option composition through Option::flatMap
        Option<String> mickey = toons.get("Mickey").flatMap(Toon::getEmail);
        Option<String> minnie = toons.get("Minnie").flatMap(Toon::getEmail);
        Option<String> goofy = toons.get("Goofy").flatMap(Toon::getEmail);

        /**
         * The problem here is that Option hides the fact that there is no data
         * associated with goofy in the map, whereas minnie has no email.
         */
        System.out.println(mickey.getOrElse(() -> "No data"));
        System.out.println(minnie.getOrElse(() -> "No data"));
        System.out.println(goofy.getOrElse(() -> "No data"));

        assertEquals(mickey.getOrElse(() -> "No data"), "mickey@disney.com");
        assertEquals(minnie.getOrElse(() -> "No data"), "No data");
        assertEquals(goofy.getOrElse(() -> "No data"), "No data");
    }

    @Test
    public void testVariance() {

        //Calculation of mean, demonstrating the way of composing Lists and
        //Options to compute mean.
        Function<List<Double>, Option<Double>> mean = xs -> xs.isEmpty() ? Option.none() : xs.foldLeft(Option.some(0.0),
                acc -> v -> acc.map(a -> a + v)).map(v -> v / xs.length());

        //This function implements a variance of series, using options for
        //non existent values, and demonstrates how to compose them.
        Function<List<Double>, Option<Double>> variance = xs -> {
            var m = mean.apply(xs);
            return m.map(mn -> xs.map(x -> Math.pow(x - mn, 2)).foldLeft(0.0, acc -> v -> acc + v) / mn);
        };

        System.out.println("Mean: " + mean.apply(list(1.0,2.0,3.0,4.0,5.0,6.0,7.0,8.0,9.0)));
        System.out.println("Variance: " + variance.apply(list(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0)));
        System.out.println("Mean on empty list: " + mean.apply(list()));

        assertEquals(mean.apply(list()), Option.none());
        assertEquals(mean.apply(list(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0)).getOrElse(() -> 0.0),
                Double.valueOf(5.0));
    }
}
