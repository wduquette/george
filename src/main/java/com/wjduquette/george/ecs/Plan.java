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
public class Plan {
    //-------------------------------------------------------------------------
    // Instance Data

    // The step queue
    private final Deque<Step> steps = new ArrayDeque<>();

    /**
     * Creates a new plan
     */
    public Plan() {}

    /**
     * Creates a new plan given a number of steps.
     * @param firstStep The first step in the plan
     * @param otherSteps The later steps.
     */
    public Plan(Step firstStep, Step... otherSteps) {
        this.steps.add(firstStep);
        for (Step step : otherSteps) {
            this.steps().addLast(step);
        }
    }

    /**
     * Gets the step queue.
     * @return The queue
     */
    public Deque<Step> steps() {
        return steps;
    }

    @Override public String toString() {
        return "(Plan " + " " + steps + ")";
    }
}
