package com.wjduquette.george;

import com.wjduquette.george.graphics.SpriteSet;

/**
 * This is a singleton containing all of the standard sprites.
 */
public class Sprites {
    private Sprites() {} // Not instantiable.

    public final static SpriteSet ALL;

    static {
        ALL = new SpriteSet(App.class, "assets/sprites/mobiles.sprite");
        SpriteSet set = new SpriteSet(App.class, "assets/sprites/Features.sprite");
        ALL.add(set);
    }
}
