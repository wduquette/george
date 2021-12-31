package com.wjduquette.george;

import com.wjduquette.george.ecs.*;
import com.wjduquette.george.model.*;
import com.wjduquette.george.widgets.UserInput;

import java.util.Optional;

/**
 * This static class is the planning system for the game.  Eventually the
 * planner will have to manage the queue of movers; for now, it just manages
 * George
 */
public class Planner {
    private Planner() {} // Not instantiable

    /** The maximum length for a planned route, in cells. */
    public static final int MAX_ROUTE_LENGTH = 20;

    /** Maximum distance for talking to NPCs. */
    public static final int MAX_TALKING_RANGE = 2;

    /**
     * Maximum distance for physical interactions, e.g., opening a door,
     * reading a sign.
     */
    public static final int MAX_PHYSICAL_RANGE = 1;

    //-------------------------------------------------------------------------
    // Instance Variables

    //-------------------------------------------------------------------------
    // The System

    public static void doPlanning(UserInput input, Region region) {
        Entity george = region.query(Player.class).findFirst().orElseThrow();

        switch (input) {
            case UserInput.MoveTo moveTo ->
                doPlanMove(region, george, moveTo.cell());
            case UserInput.InteractWith with ->
                doPlanInteraction(region, george, with.cell());
            case UserInput.StatusBox box ->
                System.out.println("Clicked on status box for " + box.playerId());
            default -> {}
        }
    }

    // Plan a player character's move to the cell.
    private static void doPlanMove(Region region, Entity player, Cell targetCell) {
        // Eventually: if the mover is a user-controlled character,
        // and the mover is currently moving, and the target cell is
        // not null, then replan using the current click.  Maybe.
        if (player.plan() != null || targetCell == null) {
            return;
        }

        // FIRST, is there a route?
        var route = region.findPassableRoute(player, targetCell);

        if (route.isEmpty()) {
            region.log("That's inaccessible.");
            return;
        } else if (route.size() > MAX_ROUTE_LENGTH) {
            region.log("That's too far.");
            return;
        }

        // NEXT, what's there?  Could be a normal cell, a feature, or a mobile
        var plan = new Plan();
        player.put(plan);

        Optional<Entity> result;

        // The player can walk up to and go through an exit.
        if ((result = region.findAt(targetCell, Exit.class)).isPresent()) {
            plan.add(new Step.Exit(result.get().id()));
            return;
        }

        // The player can move to the cell
        plan.add(new Step.MoveTo(targetCell));
    }

    // Plan a player character's interaction with whatever is at the cell.
    private static void doPlanInteraction(
        Region region,
        Entity player,
        Cell targetCell)
    {
        // If the mover is already doing something, ignore.
        if (player.plan() != null || targetCell == null) {
            return;
        }

        // FIRST, what's there?  Could be a normal cell, a feature, or a mobile
        var plan = new Plan();
        player.put(plan);

        Optional<Entity> result;

        if (region.findAt(targetCell, Mobile.class).isPresent()) {
            // We don't currently have any interactions involving mobiles.
            return;
        }

        if ((result = region.findAt(targetCell, ItemStack.class)).isPresent()) {
            Entity stack = result.get();
            var distance = region.passableDistance(player, stack.cell());
            if (distance > MAX_PHYSICAL_RANGE) {
                region.log("That's too far.");
            } else {
                plan.add(new Step.PickUp(stack.id()));
            }
        }

        if ((result = region.findAt(targetCell, Feature.class)).isPresent()) {
            Entity entity = result.get();

            if (entity.door() != null) {
                var distance = region.passableDistance(player, targetCell);

                if (distance > MAX_PHYSICAL_RANGE) {
                    region.log("That's too far.");
                } else if (entity.door().isClosed()) {
                    plan.add(new Step.OpenDoor(entity.id()));
                } else if (entity.door().isOpen()) {
                    plan.add(new Step.CloseDoor(entity.id()));
                }
            } else if (entity.chest() != null) {
                var distance = region.passableDistance(player, targetCell);

                if (distance > MAX_PHYSICAL_RANGE) {
                    region.log("That's too far.");
                } else if (entity.chest().isClosed()) {
                    plan.add(new Step.OpenChest(entity.id()));
                } else if (entity.chest().isOpen()) {
                    plan.add(new Step.CloseChest(entity.id()));
                }
            } else if (entity.mannikin() != null) {
                var distance = player.cell().distance(targetCell);

                if (distance > MAX_TALKING_RANGE ||
                    !region.isInLineOfSight(player, targetCell))
                {
                    region.log("That's too far.");
                } else {
                    plan.add(new Step.Interact(entity.id()));
                }
            } else if (entity.sign() != null) {
                var distance = region.passableDistance(player, targetCell);

                if (distance > MAX_PHYSICAL_RANGE) {
                    region.log("That's too far.");
                } else {
                    plan.add(new Step.Interact(entity.id()));
                }
            }
        }
    }
}
