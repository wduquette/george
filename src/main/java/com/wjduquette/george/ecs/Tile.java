package com.wjduquette.george.ecs;

import javafx.scene.image.Image;

/**
 * A "tile": a thing that can be drawn in a Cell.  Tiles are associated with
 * many other components.
 * @param image The tile's image
 */
public record Tile(Image image) { }
