package com.wjduquette.george;

import com.wjduquette.george.graphics.TileSet;

/**
 * This is a singleton containing all of the standard tile sets.
 */
public class TileSets {
    private TileSets() {} // Not instantiable.

    /** Standard tiles for terrain features */
    public final static TileSet FEATURES =
        new TileSet(App.class, "assets/tilesets/Features.tileset");

    /** Standard tiles for mobiles */
    public final static TileSet MOBILES =
        new TileSet(App.class, "assets/tilesets/mobiles.tileset");
}
