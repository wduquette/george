package com.wjduquette.george.ecs;

import javafx.scene.image.Image;

/**
 * A "tile": a thing that can be drawn.
 * @param image The tile's image
 */
public record Tile(Image image) { }
