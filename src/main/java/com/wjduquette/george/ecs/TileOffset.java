package com.wjduquette.george.ecs;

import javafx.geometry.Point2D;

/**
 * Where the entity's Tile should be drawn, relative to its nominal position.
 * This is used by animations.
 */
public record TileOffset(double rowOffset, double colOffset) {
    public static final TileOffset ZERO = new TileOffset(0.0, 0.0);
}
