package com.wjduquette.george;

import com.wjduquette.george.ecs.Entity;
import com.wjduquette.george.ecs.Mobile;
import com.wjduquette.george.ecs.Plan;
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
        System.out.println(mob.mobile().name() + ": Plan=" + mob.plan().steps());
        while (!mob.plan().steps().isEmpty()) {
            Step nextStep = mob.plan().steps().pollFirst();
            assert nextStep != null;

            switch (nextStep) {
                case Step.WaitForVisualEffect step:
                    if (region.find(step.id()).isPresent()) {
                        mob.plan().steps().addFirst(step);
                        return;
                    }
                    break;
                case Step.MoveTo step:
                    // TODO: Later, create animation and wait for it.
                    mob.put(step.cell());
                    return;
                case Step.SetCell step:
                    // TODO: Not implemented yet
                    System.out.println("SetCell " + step.cell());
                    break;
                case Step.Trigger step:
                    System.out.println("Trigger " + step.id());
                    break;
            }
        }

        mob.remove(Plan.class);
    }
}
