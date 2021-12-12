package com.wjduquette.george.model;

/**
 * Steps that can be scheduled for Mobiles.
 */
public sealed interface Step {
    /**
     * Waits until the entity with the given ID no longer exists.
     * @param id An entity ID, e.g. of a VisualEffect entity
     */
    record WaitUntilGone(long id) implements Step {}

    /**
     * The mobile moves smoothly to the cell using its current capabilities.
     * @param cell The cell to which to move the mobile.
     */
    record MoveTo(Cell cell) implements Step {}

    /**
     * The mobile moves adjacent to the entity with the given ID and triggers
     * it, e.g., reads a sign. Triggering is the result of the basic
     * "interact with" control.
     * @param id The ID of the entity to trigger.
     */
    record Trigger(long id) implements Step {}

    /**
     * Simply sets the mobile's cell, with no other game effects.  This is
     * often used following a movement animation to actually update the mobile's
     * logical location.
     * @param cell The cell
     */
    record SetCell(Cell cell) implements Step {}
}
