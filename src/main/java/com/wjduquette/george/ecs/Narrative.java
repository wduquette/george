package com.wjduquette.george.ecs;

/**
 * A bit of narrative in a region, usually triggered by a TripWire executing
 * Step.Interact.
 */
public record Narrative(String key) implements Component {
    @Override public String toString() { return "(Narrative " + key + ")"; }
}
