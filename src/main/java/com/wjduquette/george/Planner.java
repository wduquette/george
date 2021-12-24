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
        var route = Region.findRoute(c -> isPassable(region, player, c),
            player.cell(), targetCell);

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

        App.println("doPlanInteraction: " + player + ", " + targetCell);

        // FIRST, how far away is the target cell?
        var distance = Region.distance(
            c -> isPassable(region, player, c),
            player.cell(), targetCell);

        // NEXT, what's there?  Could be a normal cell, a feature, or a mobile
        var plan = new Plan();
        player.put(plan);

        Optional<Entity> result;

        if (region.findAt(targetCell, Mobile.class).isPresent()) {
            // We don't currently have any interactions involving mobiles.
            return;
        }

        if ((result = region.findAt(targetCell, Feature.class)).isPresent()) {
            Entity entity = result.get();

            if (entity.sign() != null) {
                if (distance > MAX_PHYSICAL_RANGE) {
                    region.log("That's too far.");
                } else {
                    plan.add(new Step.Interact(entity.id()));
                }
            } else if (entity.mannikin() != null) {
                if (distance > MAX_TALKING_RANGE) {
                    region.log("That's too far.");
                } else {
                    plan.add(new Step.Interact(entity.id()));
                }
            } else if (entity.door() != null && entity.door().isClosed()) {
                if (distance > MAX_PHYSICAL_RANGE) {
                    region.log("That's too far.");
                } else {
                    plan.add(new Step.Open(entity.id()));
                }
            } else if (entity.door() != null && entity.door().isOpen()) {
                if (distance > MAX_PHYSICAL_RANGE) {
                    region.log("That's too far.");
                } else {
                    plan.add(new Step.Close(entity.id()));
                }
            }
        }
    }


    // From a planning perspective, can this mobile expect to be able to
    // enter the given cell given the player's current capabilities and the
    // cell's content?
    //
    // At present, all mobiles can walk, and that's all they can do.
    private static boolean isPassable(Region region, Entity mob, Cell cell) {
        // FIRST, if there's a mobile blocking the cell, he can't enter.
        // Is the mobile trying to block?  Can the mover move around it?
        // NOTE: If mobiles can move simultaneously, we can maybe ignore
        // mobiles.
        // THOUGHT: Perhaps we don't need to save the route.  There are two
        // questions: is there a route, and what's the first cell?  We could
        // give him a Step.MoveTo(target, nextCell).  He won't try if there's
        // no path right now; but if there is, he'll take the next step and
        // replan.  Hmmm.
        if (region.query(Mobile.class).anyMatch(m -> m.isAt(cell))) {
            return false;
        }

        // NEXT, otherwise it's a matter of the effective terrain and the
        // mover's capabilities.
        return region.getTerrainType(cell).isWalkable();
    }

}
