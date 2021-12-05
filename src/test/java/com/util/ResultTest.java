package com.util;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import java.io.IOException;

public class ResultTest {
    @Test
    public void testResult() {
        var toons = new Map<String, Toon>()
                .put("Mickey", new Toon("Mickey", "Mouse", "mikey@disney.com"))
                .put("Minnie", new Toon("Minnie", "Mouse"))
                .put("Donald", new Toon("Donald", "Duck", "donald@disney.com"));

        assertEquals("mikey@disney.com", Toon.getName().flatMap(toons::getResult).flatMap(Toon::getEmail).getOrElse(
                ""));

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
    }
}
