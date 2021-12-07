package com.wjduquette.george;

import com.wjduquette.george.ecs.Entity;
import com.wjduquette.george.ecs.VisualEffect;
import com.wjduquette.george.model.Animation;
import com.wjduquette.george.model.Region;

import java.util.List;

/**
 * The visual effects system is responsible for animating effects over time.
 * Each effect is a `VisualEffect` entity containing an instance of the
 * sealed `Animation` interface.  For each effect, the system updates the
 * effect and deletes it if it is complete.
 */
public class VisualEffects {
    private VisualEffects() {} // Not instantiable.

    /**
     * Animates visual effects for the region.
     * @param region The region
     */
    public void doAnimate(Region region) {
        List<Entity> effects = region.query(VisualEffect.class).toList();

        for (Entity effect : effects) {
            doUpdate(region, effect);
        }
    }

    public void doUpdate(Region region, Entity entity) {
        var effect = entity.get(VisualEffect.class);
        assert effect != null;

        switch (effect.animation()) {
            case Animation.Slide anim:
                // Stuff needed here:
                // A rate in pixels.
                // The pixel offset of the mobile: new component?
                break;
        }
    }
}
