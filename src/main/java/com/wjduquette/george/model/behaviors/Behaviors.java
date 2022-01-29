package com.wjduquette.george.model.behaviors;

import com.wjduquette.george.model.Behavior;

/**
 * This enumeration defines the set of behaviors that can be assigned to
 * creatures automatically based on the data in creatures.keyfile.
 */
public enum Behaviors {
    IMMOBILE(new Immobile()),
    NAIVE_TIMID(new NaiveTimid());

    //-------------------------------------------------------------------------
    // Instance Variables

    // The actual behavior
    private final Behavior trait;

    //-------------------------------------------------------------------------
    // Constructor

    private Behaviors(Behavior trait) {
        this.trait = trait;
    }

    //-------------------------------------------------------------------------
    // API

    /**
     * Gets the actual trait associated with the behavior.
     * @return The trait.
     */
    public Behavior trait() {
        return trait;
    }

}
