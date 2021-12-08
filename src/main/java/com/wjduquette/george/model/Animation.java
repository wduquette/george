package com.wjduquette.george.model;

import com.wjduquette.george.ecs.Entity;
import com.wjduquette.george.ecs.TileOffset;

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
    final class Slide implements Animation {
        private final double baseRate = 0.2;
        private final long target;
        private final int numSteps;
        private final double deltaR;
        private final double deltaC;
        private int step = 0;

        /**
         * Creates the animation.
         * @param target The ID of the entity to slide.
         * @param start The entity's current cell
         * @param end The target cell.
         * @param speed The speed, nominally 1.0.
         */
        public Slide(long target, Cell start, Cell end, double speed) {
            this.target = target;
            double totalR = end.row() - start.row();
            double totalC = end.col() - start.col();
            var rate = baseRate * speed;
            numSteps = (int)Math.ceil(Math.max(
                Math.abs(totalR/rate), Math.abs(totalC/rate)));
            deltaR = totalR/numSteps;
            deltaC = totalC/numSteps;
        }

        /**
         * Gets the target's entity ID.
         * @return The id
         */
        public long target() {
            return target;
        }

        /**
         * Updates the target entity.
         * @param target The target
         */
        public void update(Entity target) {
            if (target == null) {
                step = numSteps;
            } else {
                if (step < numSteps) { ++step; }
                target.put(new TileOffset(step * deltaR, step * deltaC));
            }
            System.out.println(this);
        }

        @Override
        public boolean isComplete() {
            return step >= numSteps;
        }

        @Override
        public String toString() {
            return "(Animation.Slide " + target + " " + step + " " + numSteps + ")";
        }
    }
}
