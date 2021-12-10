package com.wjduquette.george.ecs;

/**
 * A "mobile": a entity that can do things, e.g., move around the world.  It
 * has a step queue.  The Planning system schedules steps to execute; the
 * Movement system carries them out (and can add steps of its own).
 *
 * <p>Mobiles will usually have an associated Loc and Tile.</p>
 */
public record Mobile(String name) implements Component {
    @Override public String toString() {
        return "(Mobile " + name + ")";
    }
}
