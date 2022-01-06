package com.state;

import com.util.List;

class Outcome {
    final Integer account;
    final List<Integer> operations;

    Outcome(Integer account, List<Integer> operations) {
        this.account = account;
        this.operations = operations;
    }

    @Override
    public String toString() {
        return "(" + account.toString() + ", " + operations.toString() + ")";
    }
}
