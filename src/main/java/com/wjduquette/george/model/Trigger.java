package com.wjduquette.george.model;

public sealed interface Trigger {

    /**
     * Triggers when a Player first moves within the given radius of the entity
     * whose trigger this is.
     * TODO: Add a condition variable and a mode, e.g., "only once"
     * TODO: Or just more types? Variants are cheap
     * @param radius A radius in cells.
     */
    record RadiusOnce(int radius) implements Trigger {
        @Override public String toString() {
            return "(Trigger.RadiusOnce " + radius + ")";
        }
    }
}
