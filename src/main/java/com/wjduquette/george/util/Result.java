package com.wjduquette.george.util;

import java.util.function.Function;

public class Result {
    //-------------------------------------------------------------------------
    // Instance Variables

    private final String value;

    //-------------------------------------------------------------------------
    // Constructor

    public Result(String value) {
        this.value = value;
    }

    //-------------------------------------------------------------------------
    // API

    public boolean isPresent() {
        return value != null;
    }

    public boolean isEmpty() {
        return value == null;
    }

    public String value() {
        if (value != null) {
            return value;
        } else {
            throw new IllegalArgumentException("Result has no value");
        }
    }

    public String or(String defaultValue) {
        return value != null ? value : defaultValue;
    }

    public <T> T as(Function<String,T> converter) {
        return converter.apply(value());
    }

    public int asInt() {
        return as(Integer::parseInt);
    }

    public double asDouble() {
        return as(Double::parseDouble);
    }

    public <T> T asOr(Function<String,T> converter, T defaultValue) {
        return value != null ? converter.apply(value) : defaultValue;
    }

    public int orInt(int defaultValue) {
        return asOr(Integer::parseInt, defaultValue);
    }

    public double orDouble(double defaultValue) {
        return asOr(Double::parseDouble, defaultValue);
    }
}
