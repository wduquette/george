package com.wjduquette.george.model;

import java.util.Collections;
import java.util.List;

public final class ConditionVariable {
    //-------------------------------------------------------------------------
    // Instance Variables

    // The name
    private final String name;

    // The valid values
    private final List<String> values;

    // The current value
    private String value;

    //-------------------------------------------------------------------------
    // Constructor

    /**
     * Creates a new variable with the given name, range of values, and
     * initial value.
     * @param name The name
     * @param values The values
     * @param value The initial value
     */
    public ConditionVariable(String name, List<String> values, String value) {
        this.name = name;
        this.values = values;
        this.value = requireValid(value);
    }

    /**
     * Creates a new variable with the given name and range of values.
     * The initial value will be the first in the list.
     * @param name The name
     * @param values The values
     */
    public ConditionVariable(String name, List<String> values) {
        this(name, values, values.get(0));
    }

    //-------------------------------------------------------------------------
    // Public API

    /**
     * Returns the name.
     * @return The name
     */
    public String name() {
        return name;
    }

    /**
     * Gets the value.
     * @return the value
     */
    public String get() {
        return value;
    }

    /**
     * Sets the value, which must be valid.
     * @param value The new value
     */
    public void set(String value) {
        this.value = requireValid(value);
    }

    /**
     * Gets the list of valid values.
     * @return The list
     */
    public List<String> values() {
        return Collections.unmodifiableList(values);
    }

    // Returns the value, if valid, and throws an exception otherwise.
    private String requireValid(String value) {
        if (!values.contains(value)) {
            throw new IllegalArgumentException("Invalid value: " + value +
                ", should be in " + values);
        } else {
            return value;
        }
    }
}
