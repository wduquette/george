package com.wjduquette.george.model.behaviors;

import com.wjduquette.george.ecs.Entity;
import com.wjduquette.george.model.Behavior;
import com.wjduquette.george.model.Region;

/**
 * This class implements no behavior at all.  The creature never does
 * anything.
 * @author will
 */
public final class Immobile implements Behavior {
    public static final Behavior TRAIT = new Immobile();

    private Immobile() {}

    @Override
    public void doPlan(Region region, Entity entity) {
        // Nothing to do
    }
}
