package com.assertion;

import com.functional.Function;
import com.util.Result;

import java.util.Objects;

public final class Assertion {
    private Assertion() {}

    public static <T> Result<T> assertCondition(T value, Function<T, Boolean> f) {
        return assertCondition(value, f, "Assertion error: condition should evaluate to true");
    }

    public static <T> Result<T> assertCondition(T value, Function<T, Boolean> f, String message) {
        return f.apply(value) ? Result.success(value) : Result.failure(new IllegalStateException(message));
    }

    public static Result<Boolean> assertTrue(boolean condition) {
        return assertTrue(condition, "Assertion error: condition should be true");
    }

    public static Result<Boolean> assertTrue(boolean condition, String message) {
        return assertCondition(condition, x -> x, message);
    }

    public static Result<Boolean> assertFalse(boolean condition) {
        return assertFalse(condition, "Assertion error: condition should be false");
    }

    public static Result<Boolean> assertFalse(boolean condition, String message) {
        return assertCondition(condition, x -> !x, message);
    }

    public static <T> Result<T> assertNotNull(T t) {
        return assertNotNull(t, "Assertion error: object should not be null");
    }

    public static <T> Result<T> assertNotNull(T t, String message) {
        return assertCondition(t, Objects::nonNull, message);
    }

    public static Result<Integer> assertPositive(int value) {
        return assertPositive(value, String.format("Assertion error: value %s must be positive", value));
    }

    public static Result<Integer> assertPositive(int value, String message) {
        return assertCondition(value, x -> x > 0, message);
    }

    public static Result<Integer> assertInRange(int value, int min, int max) {
        return assertCondition(value, x -> x >= min && x < max,
                String.format("Assertion error: value %s should be between %s and %s(exclusive)", value, min, max));
    }

    public static Result<Integer> assertPositiveOrZero(int value) {
        return assertPositiveOrZero(value, String.format("Assertion error: value %s must not be negative", 0));
    }

    public static Result<Integer> assertPositiveOrZero(int value, String message) {
        return assertCondition(value, x -> x >=0, message);
    }

    public static <A> void assertType(A element, Class<?> clazz) {
        assertType(element, clazz,
                String.format("Wrong type: %s, expected %s", element.getClass().getName(), clazz.getName()));
    }

    public static <A> void assertType(A element, Class<?> clazz, String message) {
        assertCondition(element, e -> e.getClass().equals(clazz), message);
    }
}
