package com.util;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import java.io.IOException;

import com.functional.Function;

public class ResultTest {
    @Test
    public void testResult() {
        var toons = new Map<String, Toon>()
                .put("Mickey", new Toon("Mickey", "Mouse", "mikey@disney.com"))
                .put("Minnie", new Toon("Minnie", "Mouse"))
                .put("Donald", new Toon("Donald", "Duck", "donald@disney.com"));

        assertEquals("mikey@disney.com",
                Toon.getName().flatMap(toons::getResult).flatMap(Toon::getEmail).getOrElse(""));
        assertEquals("Failure(Input error)",
                Toon.getFailureName().flatMap(toons::getResult).flatMap(Toon::getEmail).toString());
        assertEquals("Failure(Key Goofy not found in map)",
                Toon.getNameAbsentFailure().flatMap(toons::getResult).flatMap(Toon::getEmail).toString());

        assertEquals("Failure(Minnie Mouse has no mail)",
                Toon.getNameEmailFailure().flatMap(toons::getResult).flatMap(Toon::getEmail).toString());
//        assertEquals("Empty()",
//                Toon.getNameEmailFailure().flatMap(toons::get).flatMap(Toon::getEmail).toString());
    }

    @Test
    public void testResultComposition() {
        /*
         * The code shows two ways of creating a toon object from different functions.
         * In the first method, we use a lift3 function, which takes 3 parameters, and then allows us to
         * create a toon object. But the problem with this approach is that it requires to create a function
         * for each additional argument.
         * The second method is more general in the sense that it can be used to comprehend arbitrary
         * number of arguments, without the need to create additional functions in the Result class. For this
         * reason, it is called a comprehension, and languages like scala have syntax to support it.
         */
        Function<String, Function<String, Function<String, Toon>>> createPerson =
                x -> y -> z -> new Toon(x, y, z);

        var toon2 = Result.lift3(createPerson)
                .apply(Toon.getFirstName())
                .apply(Toon.getLastName())
                .apply(Toon.getEmailM());

        var toon =
                Toon.getFirstName().flatMap(firstName -> Toon.getLastName().flatMap(lastName -> Toon.getEmailM().map(email -> new Toon(firstName, lastName, email))));

        /*
         * Another example showing the comprehension of five parameters.
         */
        var result1 = Result.success(1);
        var result2 = Result.success(2);
        var result3 = Result.success(3);
        var result4 = Result.success(4);
        var result5 = Result.success(5);

        /*
         * The fact that the last call (the most deeply nested) is to map instead of flatMap, however is not
         * inherent to the pattern. That's only because the last method returns a value. If it returned a Result
         * we'd have to use flatMap instead. But because this last method is often a constructor, and
         * constructors always return raw values, we often find using map as the last method call.
         */
        var result =
                result1.flatMap(v1 -> result2.flatMap(v2 -> result3.flatMap(v3 -> result4.flatMap(v4 -> result5.map(v5 -> v1 + v2 + v3 + v4 + v5)))));

        assertEquals(result.getOrElse(0), Integer.valueOf(15));
    }

    private static class Toon {
        private final String firstName;
        private final String lastName;
        private final Result<String> email;

        private Toon(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = Result.failure(String.format("%s %s has no mail", firstName, lastName));
        }

        private Toon(String firstName, String lastName, String email) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = Result.success(email);
        }

        Result<String> getEmail() {
            return email;
        }

        static Result<String> getName() {
            return Result.success("Mickey");
        }

        static Result<String> getFailureName() {
            return Result.failure(new IOException("Input error"));
        }

        static Result<String> getNameEmailFailure() {
            return Result.success("Minnie");
        }

        static Result<String> getNameAbsentFailure() {
            return Result.success("Goofy");
        }

        static Result<String> getEmailM() {
            return Result.success("mickey@disney.com");
        }

        static Result<String> getFirstName() {
            return Result.success("Mickey");
        }

        static Result<String> getLastName() {
            return Result.success("Mickey");
        }
    }
}
