package com.wjduquette.george.model.behaviors;

import com.wjduquette.george.ecs.Entity;
import com.wjduquette.george.model.Behavior;
import com.wjduquette.george.model.Region;

/**
 * This class implements naive, timid behavior, e.g., for LadyBugs.
 * NaiveTimid creatures are neutral unless alerted by an attack
 * on themselves or other LadyBugs; then they attempt to run away, but
 * will fight if cornered. They move naively toward goals and away from
 * enemies.
 *
 * <h3>Transition Rules</h3>
 *
 * <ul>
 * <li>If WANDERING and alerted, start RUNNING_AWAY.</li>
 * <li>If RUNNING_AWAY and no enemy is within the notice range, start
 * WANDERING.</li>
 * </ul>
 *
 * <h3>Specific Behaviors</h3>
 *
 * <ul>
 * <li>While WANDERING, sometimes move randomly.</li>
 * <li>While RUNNING_AWAY, move naively away from enemies, and
 *     attack only if cornered.</li>
 * </ul>
 *
 * @author will
 */
public class NaiveTimid implements Behavior {
    public static final Behavior TRAIT = new NaiveTimid();

    private NaiveTimid() {}

    @Override
    public void doPlan(Region region, Entity entity) {

    }
}
