package com.wjduquette.george;

import com.wjduquette.george.ecs.Entity;
import com.wjduquette.george.ecs.Mobile;
import com.wjduquette.george.model.Region;
import com.wjduquette.george.model.Step;

import java.util.List;

/**
 * This is the Movement system.
 */
public class Movement {
    private Movement() {} // Not instantiable

    /**
     * Execute the movement system for the region.
     * @param region The region
     */
    public static void doMovement(Region region) {
        List<Entity> activeMobiles = region.query(Mobile.class)
            .filter(e -> e.mobile().isActive())
            .toList();

        for (Entity mob : activeMobiles) {
            step(region, mob);
        }
    }

    /**
     * Execute steps for the given mobile.
     * @param region The region
     * @param mob A mobile within that region
     */
    public static void step(Region region, Entity mob) {
        // Execute steps until there are no more or a step decides to return.
        while (mob.mobile().isActive()) {
            Step nextStep = mob.mobile().steps().pollFirst();
            assert nextStep != null;

//        switch (nextStep) {
//            case Step.MoveTo move:
//                break;
//            case Step.SetCell setCell:
//                break;
//            case Step.WaitForVisualEffect wait:
//                break;
//        }

            if (nextStep instanceof Step.WaitForVisualEffect step) {
                // If the given visual effect entity is still present, we aren't
                // done waiting.
                if (region.find(step.id()).isPresent()) {
                    mob.mobile().steps().addFirst(step);
                    return;
                }
            } else if (nextStep instanceof Step.MoveTo step) {
                // TODO: Later, create animation and wait for it.
                mob.put(step.cell());
                return;
            } else if (nextStep instanceof Step.SetCell step) {
                // TODO: Not implemented yet
                System.out.println("SetCell " + step.cell());
            } else if (nextStep instanceof Step.Trigger step) {
                System.out.println("Trigger " + step.id());
            }
        }
    }
}
