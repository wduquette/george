package com.wjduquette.george;

import com.wjduquette.george.ecs.Entity;
import com.wjduquette.george.ecs.Mobile;
import com.wjduquette.george.ecs.Plan;
import com.wjduquette.george.ecs.VisualEffect;
import com.wjduquette.george.model.Animation;
import com.wjduquette.george.model.Cell;
import com.wjduquette.george.model.Region;
import com.wjduquette.george.model.Step;

import java.util.List;

/**
 * This is the Executor system; it executes the plans made by the Planner.
 */
public class Executor {
    private Executor() {} // Not instantiable

    /**
     * Execute the movement system for the region.
     * @param region The region
     */
    public static void doMovement(Region region) {
        List<Entity> active = region.query(Plan.class)
            .toList();

        for (Entity mob : active) {
            step(region, mob);
        }
    }

    /**
     * Execute plan steps for the given mobile.
     * @param region The region
     * @param mob A mobile within that region
     */
    public static void step(Region region, Entity mob) {
        // Execute steps until there are no more or a step decides to return.
        while (!mob.plan().isEmpty()) {
            Step nextStep = mob.plan().pollFirst();
            assert nextStep != null;

            switch (nextStep) {
                case Step.WaitUntilGone step:
                    if (region.find(step.id()).isPresent()) {
                        // Keep waiting
                        mob.plan().addFirst(step);
                        return;
                    }
                    break;
                case Step.MoveTo step:
                    // FIRST, can the mobile move there?
                    if (isPassable(region, mob, step.cell())) {
                        Entity effect = slideTo(region, mob, step.cell());
                        // These will execute in reverse order.
                        // TODO: Need a method to add steps to the front, in order?
                        mob.plan().addFirst(new Step.SetCell(step.cell()));
                        mob.plan().addFirst(new Step.WaitUntilGone(effect.id()));
                    } else {
                        mob.remove(Plan.class);
                        System.out.println("Bonk!");
                        // TODO: Add an animation, where appropriate
                    }
                    return;
                case Step.SetCell step:
                    // Simply move the mobile to the given cell.
                    mob.cell(step.cell());
                    break;
                case Step.Trigger step:
                    var targetCell = region.get(step.id()).cell();
                    var route = Region.findRoute(c -> isPassable(region, mob, c),
                        mob.cell(), targetCell);
                    if (route.isEmpty()) {
                        // We can't get there
                        return;
                    } else if (route.size() == 1) {
                        // We're adjacent
                        var signName = region.get(step.id()).sign().name();
                        System.out.println("The sign reads: " +
                            region.getString(signName));
                    } else {
                        // We have to move.
                        var nextCell = route.get(0);
                        Entity effect = slideTo(region, mob, nextCell);
                        // These will execute in reverse order: we complete
                        // the slide, move the next cell, and repeat our initial
                        // goal
                        mob.plan().addFirst(step);
                        mob.plan().addFirst(new Step.SetCell(nextCell));
                        mob.plan().addFirst(new Step.WaitUntilGone(effect.id()));
                    }
                    break;
            }
        }

        mob.remove(Plan.class);
    }

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

    private static Entity slideTo(Region region, Entity mob, Cell cell) {
        var anim = new Animation.Slide(
            mob.id(), mob.cell(), cell, 1.0);
        return region.getEntities().make().put(new VisualEffect(anim));
    }
}
