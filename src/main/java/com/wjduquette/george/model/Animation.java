package com.wjduquette.george.model;

import com.wjduquette.george.ecs.Entity;
import com.wjduquette.george.ecs.Loc;

/**
 * This sealed interface contains variants that describe the kinds of
 * animations that can be handled by the Animation system.
 */
public sealed interface Animation {
    /**
     * Gets whether the animation is complete or not.
     * @return true or false
     */
    boolean isComplete();

    /**
     * Slides the target entity from its current location to the given cell
     * at the given speed.
     */
    final record Slide(long targetID, Slider slider) implements Animation {
        public Offset update() { return slider.next(); }
        @Override public boolean isComplete() { return !slider.hasNext(); }
        @Override
        public String toString() {
            return "(Animation.Slide " + targetID + " " + slider + ")";
        }
    }
}
