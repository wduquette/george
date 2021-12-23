package com.wjduquette.george.ecs;

import com.wjduquette.george.model.Animation;

/**
 * A visual effect component contains an animation that's in progress.
 * The VisualEffects system will animate the animation over successive game
 * loop iterations, and delete the effect when it is finished.  A
 * VisualEffect entity may have a location and sprite that it animates,
 * or it may animate some other entity.
 */
public record VisualEffect(Animation animation) implements Component { }
