package com.state;

class Deposit implements Input {
    private final int amount;

    Deposit(int amount) {
        this.amount = amount;
    }

    @Override
    public Type type() {
        return Type.DEPOSIT;
    }

    @Override
    public boolean isDeposit() {
        return true;
    }

    @Override
    public boolean isWithDraw() {
        return false;
    }

    @Override
    public int getAmount() {
        return amount;
    }
}
