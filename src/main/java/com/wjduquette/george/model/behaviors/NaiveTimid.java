package com.wjduquette.george.model.behaviors;

import com.wjduquette.george.App;
import com.wjduquette.george.ecs.Entity;
import com.wjduquette.george.model.Behavior;
import com.wjduquette.george.model.Posture;
import com.wjduquette.george.model.Region;

import java.util.List;

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
    @Override
    public void doPlan(Region region, Entity mob) {
        var creature = mob.creature();

        // FIRST, is it alerted, and should it be?
        Entity enemy = null;
        // TODO
//        Entity enemy = region.getClosestEnemy(mob);

        if (mob.creature().isAlerted()) {
            if (enemy == null ||
                mob.loc().distance(enemy.loc()) > creature.noticeRange()) {
                creature.alerted(false);
            }
        }

        // NEXT, determine the current posture, according to the rules.
        Posture newPosture = creature.posture();

        // Rule: creature runs away when alerted.
        if (creature.isAlerted() &&
            (creature.posture() == Posture.SITTING ||
             creature.posture() == Posture.WANDERING))
        {
            newPosture = Posture.RUNNING_AWAY;
        }

        // Rule: creature stops running away when not alerted.
        if (!creature.isAlerted() &&
            creature.posture() == Posture.RUNNING_AWAY)
        {
            newPosture = Posture.WANDERING;
        }

        creature.posture(newPosture);

        // NEXT, act according to the current posture.
        switch (creature.posture()) {
            case SITTING:
                // Just sitting; nothing to do.
                App.println("mobile " + mob.id() + ": SITTING");
                return;

            case WANDERING:
                App.println("mobile " + mob.id() + ": WANDERING");
                // TODO: Wander.  Need Step.WanderNaively
//                mob.wander(mp, mob.restlessness(), script);
                return;

            case RUNNING_AWAY:
                // FIRST, do this if it makes sense.
                // TODO: Not sure what this does:
//                mob.onEnemy(enemy);

                // NEXT, Alert friends.
                // TODO: Need a mechanism for this.  Probably a Monitor thing.
//                mob.alert(script);

                // NEXT, schedule a naive step
                // TODO: Need Step.runAwayNaively
//                Cell here = mob.runAwayNaively(enemy, mp, script);

                // NEXT, attack if cornered.
                // TODO: This depends on the outcome of the step.  ???
//                List<Combatant> enemies = mob.getVisibleEnemies(here,1);
//
//                if (enemies.size() > 0) {
//                    Combatant victim = random.pickFrom(enemies);
//
//                    mob.attack(here, victim, script);
//                }
                return;
            default:
                // TBD: Throw error?
                throw new IllegalArgumentException(
                    "Unexpected posture for creature " +
                    mob.id() + ": " + creature.posture());
        }
    }
}
