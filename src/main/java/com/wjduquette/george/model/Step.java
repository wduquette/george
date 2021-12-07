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
     * The mobile moves smoothly to the cell using its current capabilities.
     * This will usually expand to an animation plus the steps WaitForAnimation
     * and SetCell.
     */
    record MoveTo(Cell cell) implements Step {}

    /**
     * The mobile "triggers" the entity with the given ID, e.g., reads a sign.
     */
    record Trigger(int id) implements Step {}

    /**
     * Simply sets the mobile's cell, with no other game effects.
     */
    record SetCell(Cell cell) implements Step {}
}
