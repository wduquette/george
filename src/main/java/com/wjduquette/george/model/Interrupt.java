package com.wjduquette.george.model;

import com.wjduquette.george.ecs.Exit;

/**
 * An interrupt is something that interrupts the normal course of the game
 * loop.  If there is an interrupt on the interrupt stack, it is handled
 * instead.
 */
public sealed interface Interrupt {
    /**
     * Wait for a click before proceeding.
     */
    record WaitForInput() implements Interrupt {}

    /**
     * Interact with the given entity in a way appropriate for its kind.
     * E.g., for signs, display the sign's text in a box.
     * @param id The feature's entity ID
     */
    record Interact(long id) implements Interrupt {}

    /**
     * Move the party to the given point in the given region.
     * @param exit The exit to the new region.
     */
    record GoToRegion(Exit exit) implements Interrupt {}
}
