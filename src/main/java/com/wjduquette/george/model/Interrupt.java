package com.wjduquette.george.model;

/**
 * An interrupt is something that interrupts the normal course of the game
 * loop.  If there is an interrupt on the interrupt stack, it is handled
 * instead.
 */
public sealed interface Interrupt {
    /**
     * Display a sign in a box over the map; wait for user input.
     */
    public record DisplaySign(long signId) implements Interrupt {}
}
