package com.wjduquette.george.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ConditionTable {
    //-------------------------------------------------------------------------
    // Instance Variables

    // The table from variable name to variable
    private final Map<String, ConditionVariable<?>> table =
        new HashMap<>();

    //-------------------------------------------------------------------------
    // Constructor

    public ConditionTable() {
        // Nothing to do yet
    }

    //-------------------------------------------------------------------------
    // Public API

    /**
     * Puts a condition variable into the table.
     * @param var The variable
     */
    public void put(ConditionVariable<?> var) {
        table.put(var.name(), var);
    }

    /**
     * Gets the named variable.
     * @param name The variable name
     * @param <T> The variable's enumeration
     * @return The variable, or null if unknown
     */
    public <T> T get(String name) {
        return (T)table.get(name);
    }

    /**
     * Finds the named variable.
     * @param name The variable name
     * @param <T> The variable's enumeration
     * @return The variable, or empty if unknown
     */
    public <T> Optional<T> find(String name) {
        return Optional.ofNullable((T)table.get(name));
    }

    /**
     * Gets the current value of the variable, which must exist.
     * @param name The named variable
     * @param <T> The variable's enumeration
     * @return The value
     */
    public <T> T getValue(String name) {
        return (T)table.get(name).get();
    }

    /**
     * Gets the current value of the variable, which must exist.
     * @param name The named variable
     * @param <T> The variable's enumeration
     * @param value The new value
     */
    public <T extends Enum<T>> void setValue(String name, T value) {
        ConditionVariable<T> var = get(name);
        var.set(value);
    }
}
