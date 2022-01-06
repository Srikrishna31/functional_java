package com.state;

/**
 * Represents the type of transaction to be carried out.
 */
interface Input {
    Type type();

    boolean isDeposit();

    boolean isWithDraw();

    int getAmount();

    enum Type {DEPOSIT, WITHDRAW}
}
