package com.wjduquette.george.model;

/**
 * Steps that can be scheduled for Mobiles.
 */
public sealed interface Step {
    /**
     * Waits until VisualEffect id has finished.
     * @param id The entity ID of a VisualEffect entity
     */
    record WaitForVisualEffect(int id) implements Step {}

    /**
     * The mobile moves smoothly to the cell using its current capabilities.
     * This will usually expand to a VisualEffect plus the steps
     * WaitForVisualEffect and SetCell.
     * @param cell The cell to which to move the mobile.
     */
    record MoveTo(Cell cell) implements Step {}

    /**
     * The mobile "triggers" the entity with the given ID, e.g., reads a sign.
     * Triggering is the result of the basic "interact with" control.
     * @param id The ID of the entity to trigger.
     */
    record Trigger(int id) implements Step {}

    /**
     * Simply sets the mobile's cell, with no other game effects.  This is
     * often used following a movement animation to actually update the mobile's
     * logical location.
     * @param cell The cell
     */
    record SetCell(Cell cell) implements Step {}
}
