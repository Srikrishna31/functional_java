package com.functional;

/**
 * A convenient interface to represent the executable instructions.
 * It captures a complex function which can be stored to be processed later.
 */
public interface Executable {
    void exec();

    /**
     * A convenience function, that returns a function that composes two
     * Executable programs into one. Note that it returns a curried function of executables.
     * With forEach, the effect can be abstracted away, but we need better facilities
     * to compose the effects, so that they can be applied once. It is equivalent to
     * folding the effects into one.
     * To achieve this, we use the Executable interface to store the instructions,
     * and compose them.
     */
    Function<Executable, Function<Executable, Executable>> compose =
        x -> y -> () -> {
            x.exec();
            y.exec();
        };

    /**
     * This represents the identity of the executables, which is a function that
     * does nothing. This is useful in folds.
     */
    Executable identity = () -> {};
}
