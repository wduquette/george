package com.wjduquette.george.ecs;

import com.wjduquette.george.model.Step;

import java.util.ArrayDeque;

/**
 * A Plan is a series of steps, usually created by the Planning system,
 * to be carried out over a series of loop iterations by the Movement system
 * (which can add steps of its own). Plans usually belong to Mobiles, but
 * that's not required.  When a plan is completed, it, it is removed from
 * the entity.
 */
public class Plan extends ArrayDeque<Step> {
    //-------------------------------------------------------------------------
    // Constructor

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
        this.add(firstStep);
        for (Step step : otherSteps) {
            this.addLast(step);
        }
    }
}
