package com.wjduquette.george;

import com.wjduquette.george.ecs.Entity;
import com.wjduquette.george.ecs.VisualEffect;
import com.wjduquette.george.model.Animation;
import com.wjduquette.george.model.Region;

import java.util.List;

/**
 * The animator system is responsible for animating effects over time.
 * Each effect is a `VisualEffect` entity containing an instance of the
 * sealed `Animation` interface.  For each effect, the system updates the
 * effect and deletes it if it is complete.
 */
public class Animator {
    private Animator() {} // Not instantiable.

    /**
     * Animates visual effects for the region.
     * @param region The region
     */
    public static void doAnimate(Region region) {
        List<Entity> effects = region.query(VisualEffect.class).toList();

        for (Entity effect : effects) {
            doUpdate(region, effect);
        }
    }

    public static void doUpdate(Region region, Entity effect) {
        var animation = effect.get(VisualEffect.class).animation();

        switch (animation) {
            case Animation.Slide anim -> anim.update(region.get(anim.target()));
        }

        if (animation.isComplete()) {
            region.getEntities().remove(effect.id());
        }
    }
}
