package com.wjduquette.george;

import com.wjduquette.george.ecs.Entity;
import com.wjduquette.george.ecs.Mobile;
import com.wjduquette.george.ecs.Plan;
import com.wjduquette.george.ecs.VisualEffect;
import com.wjduquette.george.model.*;

import java.util.List;

/**
 * This is the Executor system; it executes the plans made by the Planner.
 */
public class Executor {
    private Executor() {} // Not instantiable

    // The result of executing a step.
    private enum Result {
        DO_NEXT,   // Go on to the next step of the plan
        PAUSE,     // Go on to the next mover
        HALT       // Clear this plan, and go on to the next mover
    }

    /**
     * Execute the movement system for the region.
     * @param region The region
     */
    public static void doMovement(Region region) {
        List<Entity> active = region.query(Plan.class)
            .toList();

        for (Entity mob : active) {
            doMoveMob(region, mob);
        }
    }

    private static void doMoveMob(Region region, Entity mob) {
        while (!mob.plan().isEmpty()) {
            switch (doStep(region, mob)) {
                case DO_NEXT:
                    // Go on to the next step
                    break;
                case PAUSE:
                    // This mover is waiting; go on to the next mover
                    return;
                case HALT:
                    // This mover can't complete its plan.  Throw it away and
                    // go on to the next mover
                    mob.plan().clear();
                    break;
            }
        }

        // The Mob's plan is empty; remove the Plan component.
        mob.remove(Plan.class);
    }

    /**
     * Execute plan steps for the given mobile.
     * @param region The region
     * @param mob A mobile within that region
     */
    public static Result doStep(Region region, Entity mob) {
        Step nextStep = mob.plan().pollFirst();
        assert nextStep != null;

        Cell targetCell;
        List<Cell> route;

        switch (nextStep) {
            //
            // Planned Steps
            //
            case Step.MoveTo goal: {
                var result = proceed(region, mob, goal, goal.cell());

                if (result == Result.DO_NEXT) {
                    if (isPassable(region, mob, goal.cell())) {
                        slideTo(region, mob, goal.cell());
                        return Result.PAUSE;
                    } else {
                        return Result.HALT;
                    }
                } else {
                    return result;
                }
            }

            case Step.Exit goal: {
                var feature = region.get(goal.id());
                targetCell = feature.cell();
                var result = proceed(region, mob, goal, targetCell);

                if (result == Result.DO_NEXT) {
                    if (isPassable(region, mob, targetCell)) {
                        throw new InterruptException(
                            new Interrupt.GoToRegion(feature.exit()));
                    } else {
                        App.println("Exit is blocked.");
                        return Result.HALT;
                    }
                } else {
                    return result;
                }
            }

            case Step.Open goal: {
                targetCell = region.get(goal.id()).cell();
                var result = proceed(region, mob, goal, targetCell);

                if (result == Result.DO_NEXT) {
                    var that = region.get(goal.id());
                    var door = that.door().open();

                    if (that.door() != null) {
                        that.put(door)
                            .put(door.feature())
                            .put(door.sprite());
                    }
                }
                return result;
            }

            case Step.Trigger goal: {
                targetCell = region.get(goal.id()).cell();
                var result = proceed(region, mob, goal, targetCell);
                if (result == Result.DO_NEXT) {
                    throw new InterruptException(new Interrupt.DescribeFeature(goal.id()));
                } else {
                    return result;
                }
            }

            //
            // Primitive Operations: these are used to implement the planned
            // steps
            //
            case Step.SetCell step:
                mob.cell(step.cell());  // Go there.
                App.println("At " + step.cell());
                break;

            case Step.WaitUntilGone wait:
                if (region.find(wait.id()).isPresent()) {
                    mob.plan().addFirst(wait); // Keep waiting
                    return Result.PAUSE;
                }
                break;
        }

        return Result.DO_NEXT;
    }

    /**
     * Moves the mob towards its goal.
     *
     * <ul>
     *     <li>If the mob is blocked from reaching its goal, returns HALT.</li>
     *     <li>If the mob has reached its goal, i.e., it's adjacent to the
     *     target cell, returns DO_NEXT.</li>
     *     <li>If the mob hasn't yet reached its goal, and isn't blocked,
     *     schedules the move to the next cell on the way and returns PAUSE.</li>
     * </ul>
     * @param region The region
     * @param mob The mob
     * @param goal The goal step
     * @param target The target cell
     * @return A result
     */
    private static Result proceed(
        Region region,
        Entity mob,
        Step goal,
        Cell target
    ) {
        var route = Region.findRoute(c -> isPassable(region, mob, c),
            mob.cell(), target);

        if (route.size() == 1) {
            return Result.DO_NEXT;
        } else if (route.size() > 1) {
            // We aren't there yet.  Take the next step.
            mob.plan().addFirst(goal);
            slideTo(region, mob, route.get(0));
            return Result.PAUSE;
        } else {
            return Result.HALT;
        }
    }

    //-------------------------------------------------------------------------
    // Utilities for use by steps

    // Can this mobile enter the given cell given the player's capabilities
    // and the cell's content?
    // At present, all mobiles can walk, and that's all they can do.
    private static boolean isPassable(Region region, Entity mob, Cell cell) {
        // FIRST, if there's a mobile blocking the cell, he can't enter.
        // TODO: need compare the blocker against the mover.  Is the blocker
        // trying to block?  Can the mover move around it?
        if (region.query(Mobile.class).anyMatch(m -> m.isAt(cell))) {
            return false;
        }

        // NEXT, otherwise it's a matter of the effective terrain and the
        // mover's capabilities.
        return region.getTerrainType(cell).isWalkable();
    }

    // Moves the mob smoothly to the given cell:
    //
    // - Creates a Slide animation
    // - Waits until it is complete
    // - Puts the mob in the cell
    private static void slideTo(Region region, Entity mob, Cell cell) {
        var anim = new Animation.Slide(
            mob.id(), mob.cell(), cell, 1.0);
        var effect = region.getEntities().make().put(new VisualEffect(anim));

        // These will execute in reverse order: we complete
        // the slide, move the next cell, and repeat our initial
        // goal
        mob.plan().addFirst(new Step.SetCell(cell));
        mob.plan().addFirst(new Step.WaitUntilGone(effect.id()));
    }
}
