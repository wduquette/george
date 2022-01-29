package com.wjduquette.george.util;

import java.util.function.Function;

public class StringResult {
    public final static StringResult EMPTY = new StringResult(null);

    //-------------------------------------------------------------------------
    // Instance Variables

    private final String value;

    //-------------------------------------------------------------------------
    // Constructor

    public StringResult(String value) {
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

    public String asIs() {
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
        return converter.apply(asIs());
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
