package com.chertar.util;

public class OrderIdGenerator {
    private int id = 0;
    public String next() {
        id++;
        return String.valueOf(id);
    }
}
