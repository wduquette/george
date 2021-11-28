package com.wjduquette.george.ecs;

/**
 * A sign on the map.  A sign is also a Feature, and has a Cell and a Tile.
 * @param text The sign's text.
 */
public record Sign(String text) { }
