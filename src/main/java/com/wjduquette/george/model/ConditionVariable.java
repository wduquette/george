package com.wjduquette.george.model;

/**
 * A condition variable represent the state of some thing in the game:
 * the state of a quest, whether the player has ever entered a certain
 * region, etc.
 */
public class ConditionVariable<T extends Enum<T>> {
    //-------------------------------------------------------------------------
    // Instance Variables

    private final String name;
    private T value;

    //-------------------------------------------------------------------------
    // Constructor

    /**
     * Creates a variable for the given enum with the name and initial value.
     * @param name The variable name
     * @param initialValue The variable's initial value
     */
    public ConditionVariable(
        String name,
        T initialValue)
    {
        this.name = name;
        this.value = initialValue;
    }

    //-------------------------------------------------------------------------
    // Public API

    /**
     * Gets the variable's name.
     * @return The name`
     */
    public String name() {
        return name;
    }

    /**
     * Gets the value.
     * @return The value
     */
    public T get() {
        return value;
    }

    /**
     * Sets the value.
     * @param newValue The value
     */
    public void set(T newValue) {
        this.value = newValue;
    }
}
