package com.wjduquette.george;

import com.wjduquette.george.ecs.Entity;
import com.wjduquette.george.ecs.Plan;
import com.wjduquette.george.ecs.TileOffset;
import com.wjduquette.george.ecs.VisualEffect;
import com.wjduquette.george.model.Animation;
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
        while (!mob.plan().isEmpty()) {
            Step nextStep = mob.plan().pollFirst();
            assert nextStep != null;

            System.out.println("Executing " + nextStep);
            switch (nextStep) {
                case Step.WaitUntilGone step:
                    if (region.find(step.id()).isPresent()) {
                        // Keep waiting
                        mob.plan().addFirst(step);
                        return;
                    }
                    break;
                case Step.MoveTo step:
                    var anim = new Animation.Slide(
                        mob.id(), mob.cell(), step.cell(), 1.0);
                    var effect = region.getEntities().make()
                        .put(new VisualEffect(anim));

                    // These will execute in reverse order.
                    // TODO: Need a method to add steps to the front, in order.
                    mob.plan().addFirst(new Step.SetCell(step.cell()));
                    mob.plan().addFirst(new Step.WaitUntilGone(effect.id()));
                    return;
                case Step.SetCell step:
                    // This completes a motion; clear the tile offset.
                    mob.put(step.cell());
                    // TODO: Not happy with having to do this.
                    mob.remove(TileOffset.class);
                    break;
                case Step.Trigger step:
                    System.out.println("Trigger " + step.id());
                    break;
            }
        }

        mob.remove(Plan.class);
    }
}
