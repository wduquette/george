package com.wjduquette.george.ecs;

/**
 * Where the entity's Tile should be drawn, relative to its nominal position.
 * This is used by animations.
 */
public record PixelOffset(double x, double y) { }
