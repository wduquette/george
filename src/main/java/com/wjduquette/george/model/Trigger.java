package com.wjduquette.george.model;

public sealed interface Trigger {

    /**
     * Triggers the interrupt when the player moves within the given
     * radius of the entity whose trigger this is.
     * TODO: Add a condition variable and a mode, e.g., "only once"
     * TODO: Or just more types? Variants are cheap
     * @param radius A radius in cells.
     * @param interrupt An interrupt.
     */
    record RadiusInterrupt(
        int radius,
        Interrupt interrupt
    ) implements Trigger {
        @Override public String toString() {
            return "(InteractionRadius " + radius + ")";
        }
    }
}
