package com.wjduquette.george;

import com.wjduquette.george.ecs.Entity;
import com.wjduquette.george.ecs.LiveImage;
import com.wjduquette.george.ecs.VisualEffect;
import com.wjduquette.george.model.Animation;
import com.wjduquette.george.model.Region;

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
     * @param app The application
     * @param region The region
     */
    public static void doAnimate(App app, Region region) {
        // Update all effect animations
        for (Entity effect : region.query(VisualEffect.class).toList()) {
            doUpdate(app, region, effect);
        }
    }

    public static void doUpdate(App app, Region region, Entity effect) {
        // TODO: Add better accessor
        var animation = effect.get(VisualEffect.class).animation();

        switch (animation) {
            case Animation.Slide anim -> {
                var target = region.get(anim.targetID());
                if (target == null) {
                    region.entities().remove(effect.id());
                    return;
                }

                var offset = anim.update();

                if (!anim.isComplete()) {
                    var img = app.sprites().get(target.sprite());
                    target.liveImage(new LiveImage("slide", img, offset));
                } else {
                    target.remove(LiveImage.class);
                }
            }
        }

        if (animation.isComplete()) {
            region.entities().remove(effect.id());
        }
    }
}
