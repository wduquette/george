package com.wjduquette.george.model;

import java.util.*;

public class ConditionTable {
    //-------------------------------------------------------------------------
    // Instance Variables

    // The table from variable name to variable
    private final Map<String, ConditionVariable> table =
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
    public void define(ConditionVariable var) {
        table.put(var.name(), var);
    }

    /**
     * Gets the named variable.
     * @param name The variable name
     * @return The variable, or null if unknown
     */
    public ConditionVariable get(String name) {
        return table.get(name);
    }

    /**
     * Finds the named variable.
     * @param name The variable name
     * @return The variable, or empty if unknown
     */
    public Optional<ConditionVariable> find(String name) {
        return Optional.ofNullable(table.get(name));
    }

    /**
     * Gets the current value of the variable, which must exist.
     * @param name The named variable
     * @return The value
     */
    public String getValue(String name) {
        return table.get(name).get();
    }

    /**
     * Sets the current value of the variable, which must exist.
     * @param name The named variable
     * @param value The new value
     */
    public void setValue(String name, String value) {
        get(name).set(value);
    }

    public Set<String> getNames() {
        return table.keySet();
    }

}
