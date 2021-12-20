package com.wjduquette.george.model;

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
     * Display a sign in a box over the map; wait for user input.
     * @param signId The entity ID of the sign
     */
    public record DisplaySign(long signId) implements Interrupt {}

    /**
     * Move the party to the given point in the given region.
     * @param region The region's name.
     * @param point The point in the region.
     */
    public record GoToRegion(String region, String point) implements Interrupt {}
}
