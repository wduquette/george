package com.wjduquette.george.ecs;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * A "mobile": a entity that can do things, e.g., move around the world.  It
 * has a goal queue, and will execute its goals until it completes them or it
 * is told to {@code halt()}.  While executing a goal can add new goals to the
 * head or tail of the queue.
 *
 * <p>Mobiles will usually have an associated Cell and Tile.</p>
 */
public class Mobile {
    //-------------------------------------------------------------------------
    // Instance Data

    // The name, for display
    private final String name;

    // The goal queue
    private final Deque<Runnable> goals = new ArrayDeque<>();

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
     * Does this mobile have a goal?
     * @return true or false
     */
    public boolean hasGoal() { return !goals.isEmpty(); }

    /**
     * Do this goal before anything else in the queue.
     * @param goal The goal
     */
    public void doFirst(Runnable goal) {
        goals.addFirst(goal);
    }

    /**
     * Do this goal after everything else in the queue.
     * @param goal The goal
     */
    public void doLast(Runnable goal) {
        goals.addLast(goal);
    }

    /**
     * Do the next goal.  It's an error if there are none.
     */
    public void doNext() {
        goals.getFirst().run();
    }

    /** Halt: clear the goal queue. */
    public void halt() {
        goals.clear();
    }

    @Override public String toString() {
        return "(Mobile " + name + " " + goals.size() + ")";
    }
}
