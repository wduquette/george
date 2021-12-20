package com.wjduquette.george;

import com.wjduquette.george.ecs.Door;
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

    /**
     * Animates visual effects for the region.
     * @param region The region
     */
    public static void doAnimate(Region region) {
        // Update all effect animations
        List<Entity> effects = region.query(VisualEffect.class).toList();

        for (Entity effect : effects) {
            doUpdate(region, effect);
        }

        // Set the each door's sprite based on its state.
        List<Entity> doors = region.query(Door.class).toList();

        for (Entity e : doors) {
            e.sprite(e.door().isClosed() ? e.door().closedSprite() : e.door().openSprite());
        }
    }

    public static void doUpdate(Region region, Entity effect) {
        var animation = effect.get(VisualEffect.class).animation();

        switch (animation) {
            case Animation.Slide anim -> {
                var target = region.get(anim.target());
                var newLoc = anim.update(target);
                target.put(newLoc);
            }
        }

        if (animation.isComplete()) {
            region.getEntities().remove(effect.id());
        }
    }
}
