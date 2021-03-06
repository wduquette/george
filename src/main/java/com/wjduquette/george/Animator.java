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
 * effect and deletes it if it is complete.  The animator also handles other
 * visual changes.
 */
public class Animator {
    private Animator() {} // Not instantiable.
    public static final int MAX_MESSAGES = 3;
    public static final long MESSAGE_DURATION = 40;

    /**
     * Animates visual effects for the region.
     * @param gameTick The current game tick
     * @param region The region
     */
    public static void doAnimate(long gameTick, Region region) {
        // Update all effect animations
        for (Entity effect : region.query(VisualEffect.class).toList()) {
            doUpdate(region, effect);
        }
    }

    public static void doUpdate(Region region, Entity effect) {
        var animation = effect.get(VisualEffect.class).animation();

        switch (animation) {
            case Animation.Slide anim -> {
                var target = region.get(anim.target());
                if (target != null) {
                    var newLoc = anim.update(target);
                    target.put(newLoc);
                }
            }
        }

        if (animation.isComplete()) {
            region.entities().remove(effect.id());
        }
    }
}
