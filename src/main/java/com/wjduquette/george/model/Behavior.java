package com.wjduquette.george.model;

import com.wjduquette.george.ecs.Entity;

/**
 * This is a trait interface, to capture the behavior of creatures.
 */
public interface Behavior {
    /**
     * The entity plans its move.
     * @param region
     * @param entity
     */
    void doPlan(Region region, Entity entity);
}
