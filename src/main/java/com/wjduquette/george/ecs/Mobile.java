package com.wjduquette.george.ecs;

/**
 * A "mobile": an entity that can do things, e.g., move around the world.
 * @param key The mobile's info key
 */
public record Mobile(String key) implements Component {
    @Override public String toString() {
        return "(Mobile " + key + ")";
    }
}
