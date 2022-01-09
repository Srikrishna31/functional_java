package com.functional;

/**
 * A class to represent a Nothing type, which can be useful to represent a
 * noop or nothing type.
 */
public final class Nothing {
    private Nothing() {}

    public static final Nothing instance = new Nothing();
}
