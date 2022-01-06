package com.state;

class WithDraw implements Input {
    private final int amount;

    WithDraw(int amount) {
        this.amount = amount;
    }

    @Override
    public Type type() {
        return Type.WITHDRAW;
    }

    @Override
    public boolean isDeposit() {
        return false;
    }

    @Override
    public boolean isWithDraw() {
        return true;
    }

    @Override
    public int getAmount() {
        return amount;
    }
}
