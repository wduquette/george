package com.wjduquette.george;

import com.wjduquette.george.ecs.Entity;
import com.wjduquette.george.ecs.Mobile;
import com.wjduquette.george.ecs.Plan;
import com.wjduquette.george.ecs.Sign;
import com.wjduquette.george.model.Cell;
import com.wjduquette.george.model.Player;
import com.wjduquette.george.model.Region;
import com.wjduquette.george.model.Step;

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

    public static void doPlanning(Cell targetCell, Region region) {
        Entity george = region.query(Player.class).findFirst().orElseThrow();

        // Eventually: if the mover is a user-controlled character,
        // and the mover is currently moving, and the target cell is
        // not null, then replan using the current click.  Maybe.
        if (george.plan() == null && targetCell != null) {
            // Planning System (for player characters)

            // FIRST, is there a route?
            // TODO: Define predicate.
            var route = Region.findRoute(c -> isPassable(region, george, c),
                george.cell(), targetCell);

            if (route.isEmpty()) {
                return;
            }

            // NEXT, what's there?
            var plan = new Plan();

            Optional<Entity> sign = region.query(Sign.class)
                .filter(e -> e.isAt(targetCell)).findFirst();

            if (sign.isPresent()) {
                plan.add(new Step.Trigger(sign.get().id()));
            } else {
                // Nothing, so we just move there.
                addRoute(plan, route);
            }

            george.put(plan);
        }
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
