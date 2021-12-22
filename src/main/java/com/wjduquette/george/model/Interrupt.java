package com.wjduquette.george.model;

import com.wjduquette.george.ecs.Exit;

/**
 * An interrupt is something that interrupts the normal course of the game
 * loop.  If there is an interrupt on the interrupt stack, it is handled
 * instead.
 */
public sealed interface Interrupt {
    /**
     * Used to just wait for a click.
     */
    public record WaitForInput() implements Interrupt {}

    /**
     * Displays a description of a feature in a box over the map.
     * @param id The feature's entity ID
     */
    public record DescribeFeature(long id) implements Interrupt {}

    /**
     * Move the party to the given point in the given region.
     * @param exit The exit to the new region.
     */
    public record GoToRegion(Exit exit) implements Interrupt {}
}
