package com.wjduquette.george.model;

/**
 * Steps that can be scheduled for Mobiles.
 */
public sealed interface Step {
    /**
     * Waits until animation id has finished.
     */
    record WaitForAnimation(int id) implements Step {}

    /**
     * The mobile moves smoothly to the cell using its current capabilities
     */
    record MoveTo(Cell cell) implements Step {}

    /**
     * The mobile "triggers" the entity with the given ID, e.g., reads a sign.
     */
    record Trigger(int id) implements Step {}
}
