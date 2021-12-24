package com.wjduquette.george.model;

/**
 * Steps that can be scheduled for Mobiles.
 */
public sealed interface Step {
    //
    // Goals: Steps scheduled by the planner
    //

    /**
     * The mobile moves smoothly to the cell using its current capabilities.
     * @param cell The cell to which to move the mobile.
     */
    record MoveTo(Cell cell) implements Step {
        @Override public String toString() { return "(MoveTo " + cell + ")";}
    }

    /**
     * The mobile opens the chest with the given ID.
     * @param id The ID of the entity to open.
     */
    record OpenChest(long id) implements Step {
        @Override public String toString() { return "(OpenChest " + id + ")";}
    }

    /**
     * The mobile closes the chest with the given ID.
     * @param id The ID of the entity to open.
     */
    record CloseChest(long id) implements Step {
        @Override public String toString() { return "(CloseChest " + id + ")";}
    }

    /**
     * The mobile opens the door with the given ID.
     * @param id The ID of the entity to open.
     */
    record OpenDoor(long id) implements Step {
        @Override public String toString() { return "(OpenDoor " + id + ")";}
    }

    /**
     * The mobile closes the door with the given ID.
     * @param id The ID of the entity to open.
     */
    record CloseDoor(long id) implements Step {
        @Override public String toString() { return "(CloseDoor " + id + ")";}
    }

    /**
     * The mobile interacts with the entity with the given ID, i.e., reads
     * reads a sign.
     * @param id The ID of the entity to interact with
     */
    record Interact(long id) implements Step {
        @Override public String toString() { return "(Interact " + id + ")";}
    }

    /**
     * The mobile moves to the cell containing the Exit, and is transferred
     * to the relevant region.
     * @param id The ID of the exit entity.
     */
    record Exit(long id) implements Step {
        @Override public String toString() { return "(Exit " + id + ")";}
    }

    //
    // Primitive Operations: Scheduled by the Executor while executing
    // goals

    /**
     * Simply sets the mobile's cell, with no other game effects.  This is
     * often used following a movement animation to actually update the mobile's
     * logical location.
     * @param cell The cell
     */
    record SetCell(Cell cell) implements Step {
        @Override public String toString() { return "(SetCell " + cell + ")";}
    }

    /**
     * Waits until the entity with the given ID no longer exists.
     * @param id An entity ID, e.g. of a VisualEffect entity
     */
    record WaitUntilGone(long id) implements Step {
        @Override public String toString() { return "(WaitUntilGone " + id + ")";}
    }
}
