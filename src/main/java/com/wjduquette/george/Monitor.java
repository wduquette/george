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

        for (var trigger : tripwires) {
            // A tripwire can throw an InterruptException.
            for (var player : region.query(Player.class).toList()) {
                // Mustn't interrupt a transition.
                if (!player.isTransitionInProgress()) {
                    doTripwire(region, player, trigger);
                }
            }
        }
    }

    // See whether the tripwire fires, based on its content and the
    // circumstances.
    private static void doTripwire(Region region, Entity player, Entity wire) {
        var step = wire.tripwire().step();

        switch (wire.tripwire().trigger()) {
            case Trigger.RadiusOnce trigger -> {
                if (region.conditions().isSet(trigger.flag())) {
                    return;
                }

                // If the player character is within the radius, make it
                // execute the step; and forget the tripwire.
                var dist = region.passableDistance(player, wire.cell());

                if (dist <= trigger.radius()) {
                    player.put(new Plan());
                    player.plan().add(step);

                    // Mark the trigger triggered.
                    region.conditions().set(trigger.flag());
                }
            }
        }
    }
}
