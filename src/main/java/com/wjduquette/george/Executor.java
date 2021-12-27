package com.wjduquette.george;

import com.wjduquette.george.ecs.*;
import com.wjduquette.george.model.*;

import java.util.List;

/**
 * This is the Executor system; it executes the plans made by the Planner.
 */
public class Executor {
    private Executor() {} // Not instantiable

    // The result of executing a step.
    //
    // DO_NEXT indicates that a logical action isn't yet complete; the executor
    // most also execute the following step in order to complete it.
    //
    // PAUSE indicates either that a logical action has been completed, or that
    // mover is waiting for a transition to complete in order to complete the
    // logical action; either way, the Executor is done with this mover for
    // this iteration of the game loop.  Entity::isTransitionInProgress
    // distinguishes between the two cases.
    //
    // HALT means that the plan has failed; e.g., the path to the destination
    // is now blocked.  This should only occur at a point where the data model
    // is logically consistent, i.e., not during a transition.
    private enum Result {
        DO_NEXT,   // Go on to the next step of this plan
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
                        region.log("The way is blocked.");
                        return Result.HALT;
                    }
                } else {
                    return result;
                }
            }

            case Step.OpenChest chest: {
                region.get(chest.id()).openChest();
                var contents = region.query(Item.class, Owner.class)
                    .filter(e -> e.owner().ownerId() == chest.id())
                    .toList();

                if (contents.isEmpty()) {
                    region.log("The chest is empty.");
                } else {
                    // Give the contents to the owner
                    for (var e : contents) {
                        e.owner(mob.id());
                        region.log("You got: " + e.label().text());
                    }
                }
                return Result.DO_NEXT;
            }

            case Step.CloseChest chest: {
                region.get(chest.id()).closeChest();
                return Result.DO_NEXT;
            }

            case Step.OpenDoor door: {
                region.get(door.id()).openDoor();
                return Result.DO_NEXT;
            }

            case Step.CloseDoor door: {
                region.get(door.id()).closeDoor();
                return Result.DO_NEXT;
            }

            case Step.Interact goal: {
                // At present, this is the only kind of interaction.
                throw new InterruptException(new Interrupt.Interact(goal.id()));
            }

            //
            // Primitive Operations: these are used to implement the planned
            // steps
            //

            // Completes a mover's cell step, as initiated by the
            // proceed() method.
            case Step.CompleteCellStep step:
                mob.cell(step.cell());  // Go there.
                // Pause, because we've completed a logical change.  This
                // gives the Monitor a chance to analyze the current state.
                return Result.PAUSE;

            case Step.Transition wait:
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
        mob.plan().addFirst(new Step.CompleteCellStep(cell));
        mob.plan().addFirst(new Step.Transition(effect.id()));
    }
}
