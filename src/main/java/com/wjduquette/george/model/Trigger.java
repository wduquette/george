package com.wjduquette.george.model;

public sealed interface Trigger {
    /**
     * Triggers when a Player first moves within the given radius of the entity
     * whose trigger this is.
     * @param radius A radius in cells.
     * @param flag The condition flag to set when the trigger is triggered.
     */
    record RadiusOnce(int radius, String flag) implements Trigger {
        @Override public String toString() {
            return "(Trigger.RadiusOnce " + radius + " " + flag + ")";
        }
    }
}
