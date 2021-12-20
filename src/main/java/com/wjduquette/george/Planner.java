package com.wjduquette.george;

import com.wjduquette.george.ecs.*;
import com.wjduquette.george.model.*;
import com.wjduquette.george.widgets.UserInput;

import java.util.List;
import java.util.Optional;

/**
 * This static class is the planning system for the game.  Eventually the
 * planner will have to manage the queue of movers; for now, it just manages
 * George
 */
public class Planner {
    private Planner() {} // Not instantiable

    //-------------------------------------------------------------------------
    // Instance Variables

    //-------------------------------------------------------------------------
    // The System

    public static Optional<Interrupt> doPlanning(UserInput input, Region region) {
        Entity george = region.query(Player.class).findFirst().orElseThrow();

        switch (input) {
            case UserInput.CellClick click ->
                doPlanMove(region, george, click.cell());
            case UserInput.StatusBox status ->
                System.out.println("Clicked on status box for " + status.playerId());
        }

        return Optional.empty();
    }

    private static void doPlanMove(Region region, Entity player, Cell targetCell) {
        // Eventually: if the mover is a user-controlled character,
        // and the mover is currently moving, and the target cell is
        // not null, then replan using the current click.  Maybe.
        if (player.plan() != null || targetCell == null) {
            return;
        }

        // Planning System (for player characters)

        // FIRST, is there a route?
        System.out.println("Looking for route");
        var route = Region.findRoute(c -> isPassable(region, player, c),
            player.cell(), targetCell);
        System.out.println("Found route: " + route);

        if (route.isEmpty()) {
            return;
        }

        // NEXT, what's there?  Could be a normal cell, a feature, or a mobile
        var plan = new Plan();
        player.put(plan);

        Optional<Entity> result;

        // TODO: check for mobiles before features.

        if ((result = region.findAt(targetCell, Feature.class)).isPresent()) {
            Entity entity = result.get();

            if (entity.sign() != null) {
                plan.add(new Step.Trigger(entity.id()));
            } else if (entity.door() != null && entity.door().isClosed()) {
                plan.add(new Step.Open(entity.id()));
            }
        } else {
            plan.add(new Step.MoveTo(targetCell));
        }
//
//        if (plan.isEmpty()) {
//            player.remove(plan);
//        }
    }

    // Adds the route to the end of the given plan as a series of MoveTo steps.
    private static void addRoute(Plan plan, List<Cell> route) {
        route.forEach(cell -> plan.add(new Step.MoveTo(cell)));
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
