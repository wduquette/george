package com.wjduquette.george.ecs;

/**
 * A chest feature on the map.
 */
public record Chest(String key) implements Component {
    @Override public String toString() {
        return "(Chest " + key + ")";
    }
}
