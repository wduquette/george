package com.wjduquette.george.model;

/**
 * This sealed interface contains variants that describe the kinds of
 * animations that can be handled by the Animation system.
 */
public sealed interface Animation {
    /**
     * Slides entity ID from its current location to the given cell at the
     * given speed.  The speed is used to compute the number of pixels slid
     * during each game loop iteration.
     * @param id The ID of the entity to slide.
     * @param speed The speed, nominally 1.0.
     * @param cell The cell to slide the mobile to.
     */
    record Slide(int id, double speed, Cell cell) implements Animation {}
}
