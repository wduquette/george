package com.wjduquette.george.model;

/**
 * Steps that can be scheduled for Mobiles.
 */
public sealed interface Step {
    /**
     * Indicates whether this step is a transition or not.
     * A transition is a step that simply waits for a transition animation to
     * complete, after which the next step will complete a logical action.
     */
    default boolean isTransition() {
        return false;
    }

    //
    // Goals: Steps scheduled by the planner
    //

    /**
     * The mobile moves smoothly to the cell using its current capabilities.
     * @param cell The cell to which to move the mobile.
     */
    record MoveTo(Cell cell) implements Step {
        @Override public String toString() { return "(Step.MoveTo " + cell + ")";}
    }

    /**
     * The mobile opens the chest with the given ID.
     * @param id The ID of the entity to open.
     */
    record OpenChest(long id) implements Step {
        @Override public String toString() { return "(Step.OpenChest " + id + ")";}
    }

    /**
     * The mobile closes the chest with the given ID.
     * @param id The ID of the entity to open.
     */
    record CloseChest(long id) implements Step {
        @Override public String toString() { return "(Step.CloseChest " + id + ")";}
    }

    /**
     * The mobile opens the door with the given ID.
     * @param id The ID of the entity to open.
     */
    record OpenDoor(long id) implements Step {
        @Override public String toString() { return "(Step.OpenDoor " + id + ")";}
    }

    /**
     * The mobile closes the door with the given ID.
     * @param id The ID of the entity to open.
     */
    record CloseDoor(long id) implements Step {
        @Override public String toString() { return "(Step.CloseDoor " + id + ")";}
    }

    /**
     * The mobile picks up the items in the ItemStack with the given ID.
     * @param id The ID of the ItemStack entity
     */
    record PickUp(long id) implements Step {
        @Override public String toString() { return "(Step.Pickup " + id + ")";}
    }

    /**
     * The mobile interacts with the entity with the given ID, i.e., reads
     * a sign, talks to an NPC.
     * @param id The ID of the entity to interact with
     */
    record Interact(long id) implements Step {
        @Override public String toString() { return "(Step.Interact " + id + ")";}
    }

    /**
     * The mobile moves to the cell containing the Exit, and is transferred
     * to the relevant region.
     * @param id The ID of the exit entity.
     */
    record Exit(long id) implements Step {
        @Override public String toString() { return "(Step.Exit " + id + ")";}
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
    record CompleteCellStep(Cell cell) implements Step {
        @Override public String toString() { return "(Step.CompleteCellStep " + cell + ")";}
    }

    /**
     * A transition is an animation related to a step that is being taken:
     * the slide to a new a cell, the flight of an arrow.  The Transition
     * step waits until the VisualEffect with the given ID no longer exists.
     * It will be followed immediately by a step that completes the action,
     * e.g., Step.CompleteCellStep after a SlideTo animation.
     * @param id An entity ID, e.g. of a VisualEffect entity
     */
    record Transition(long id) implements Step {
        @Override public String toString() { return "(Step.Transition " + id + ")";}
        @Override public boolean isTransition() { return true; }
    }
}
