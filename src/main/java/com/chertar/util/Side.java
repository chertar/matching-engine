package com.chertar.util;

public enum Side {
    BUY(true), SELL(false);
    private boolean isBuy;
    Side(boolean isBuy) {
        this.isBuy = isBuy;
    }
    public boolean isBuy() {
        return this.isBuy;
    }
}
