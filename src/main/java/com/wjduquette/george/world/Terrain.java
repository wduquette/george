package com.wjduquette.george.world;

import javafx.scene.image.Image;

/**
 * A terrain tile in the world.
 * TODO: Should include other terrain info, regarding passability, etc.
 * @param tile The image to display for this tile.
 */
public record Terrain(Image tile) { }

