package com.wjduquette.george.ecs;

import com.wjduquette.george.model.Step;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * A "mobile": a entity that can do things, e.g., move around the world.  It
 * has a step queue.  The Planning system schedules steps to execute; the
 * Movement system carries them out (and can add steps of its own).
 *
 * <p>Mobiles will usually have an associated Cell and Tile.</p>
 */
public class Mobile {
    //-------------------------------------------------------------------------
    // Instance Data

    // The name, for display
    private final String name;

    // The step queue
    private final Deque<Step> steps = new ArrayDeque<>();

    /**
     * Creates a new Mobile
     * @param name The mobile's name, for display
     */
    public Mobile(String name) { this.name = name; }

    /**
     * Gets the mobile's name.
     * @return The name
     */
    public String name() { return name; }

    /**
     * Does this mobile have any steps scheduled?
     * @return true or false
     */
    public boolean isActive() { return !steps.isEmpty(); }

    /** Halt: clear the steps queue. */
    public void halt() {
        steps.clear();
    }

    /**
     * Gets the mobile's step queue.
     * @return The queue
     */
    public Deque<Step> steps() {
        return steps;
    }

    @Override public String toString() {
        return "(Mobile " + name + " " + steps.size() + ")";
    }
}
