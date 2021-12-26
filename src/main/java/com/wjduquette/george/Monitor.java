package com.wjduquette.george;

import com.wjduquette.george.ecs.Entity;
import com.wjduquette.george.ecs.Plan;
import com.wjduquette.george.ecs.Player;
import com.wjduquette.george.ecs.Tripwire;
import com.wjduquette.george.model.Region;
import com.wjduquette.george.model.Trigger;

/**
 * The Monitor system, which watches for TripWires and other interesting things.
 */
public class Monitor {
    public static void analyze(Region region) {
        var tripwires = region.query(Tripwire.class).toList();

        for (var entity : tripwires) {
            // A tripwire can throw an InterruptException.
            doTrip(region, entity);
        }
    }

    // See whether the tripwire fires, based on what it is.
    private static void doTrip(Region region, Entity entity) {
        var step = entity.tripwire().step();

        switch (entity.tripwire().trigger()) {
            case Trigger.RadiusOnce trigger -> {
                // If there is a player character within the radius, make it
                // execute the step; and forget the tripwire.
                var leader = region.query(Player.class).findFirst().orElseThrow();
                var dist = Region.distance(
                    region::isWalkable,
                    entity.cell(),
                    leader.cell());

                if (dist <= trigger.radius()) {
                    App.println("Executing " + trigger);
                    leader.put(new Plan());
                    leader.plan().add(step);
                    entity.remove(entity.tripwire());
                }
            }
        }
    }
}
