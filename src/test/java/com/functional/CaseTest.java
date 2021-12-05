package com.functional;

import com.util.Result;
import org.junit.Test;
import java.util.regex.Pattern;

import static com.functional.Case.mcase;
import static com.functional.Case.match;

import static com.util.Result.success;
import static com.util.Result.failure;

public class CaseTest {
    static Pattern emailPattern =
            Pattern.compile("^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}$");

    static Effect<String> success = s -> System.out.println("Mail sent to " + s);
    static Effect<String> failure = s -> System.err.println("Error message logged: " + s);

    @Test
    public void testCase() {
        //TODO: Use mockito to expect correct function call here.
        emailChecker.apply("this.is@my.email").bind(success, failure);
        emailChecker.apply(null).bind(success, failure);
        emailChecker.apply("").bind(success, failure);
        emailChecker.apply("john.doe@acme.com").bind(success, failure);
    }

    /**
     * This function shows an example of implementing pattern matching in java.
     * Note that, match and mcase are static functions imported from Case class.
     * match is a function which takes a default case, and the rest of the arguments
     * as different cases.
     * Note that, each boolean supplier is a closure which closes on the s parameter.
     * Similarly each value supplier is also a closure which encapsulates the scope it is
     * defined in.
     * Excepting the default case, the other cases must be provided in the order in which
     * the checks are desired. In this scenario, we want to check for null first, and then
     * for the length, and only after that apply the regex pattern.
     * If we reverse the order for example, it could lead to NPE for a null argument
     * invocation, since it would try to apply the regex on a null value.
     */
    static Function<String, Result<String>> emailChecker = s -> match(
            mcase(() -> success(s)), // The default case
            mcase(() -> s == null, () -> failure("email must not be null")),
            mcase(() -> s.length() == 0, () -> failure("email must not be empty")),
            mcase(() -> !emailPattern.matcher(s).matches(), () -> failure("email" + s + " is invalid"))
    );
};
