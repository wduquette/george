package com.wjduquette.george;

import com.wjduquette.george.ecs.Entity;
import com.wjduquette.george.ecs.Plan;
import com.wjduquette.george.model.Cell;
import com.wjduquette.george.model.Player;
import com.wjduquette.george.model.Region;
import com.wjduquette.george.model.Step;

import java.util.List;

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
            List<Cell> route = Region.findRoute(c -> region.isWalkable(c),
                george.cell(), targetCell);

            // TODO Is there an addAll?
            var plan = new Plan();
            for (Cell cell : route) {
                plan.add(new Step.MoveTo(cell));
            }
            george.put(plan);
        }
    }
}
