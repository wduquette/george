package com.wjduquette.george;

import com.wjduquette.george.graphics.SpriteSet;

/**
 * This is a singleton containing all of the standard sprites.
 */
public class Sprites {
    private Sprites() {} // Not instantiable.

    /** Standard tiles for terrain features */
    public final static SpriteSet FEATURES =
        new SpriteSet(App.class, "assets/sprites/Features.sprite");

    /** Standard tiles for mobiles */
    public final static SpriteSet MOBILES =
        new SpriteSet(App.class, "assets/sprites/mobiles.sprite");
}
