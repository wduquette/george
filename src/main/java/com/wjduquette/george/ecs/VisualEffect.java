package com.wjduquette.george.ecs;

import javafx.animation.Animation;

/**
 * A visual effect component contains an animation that's in progress.
 * The VisualEffects system will animate the animation over successive game
 * loop iterations, and delete the effect when it is finished.
 */
public record VisualEffect(Animation animation) { }
