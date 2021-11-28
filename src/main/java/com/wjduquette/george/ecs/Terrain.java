package com.wjduquette.george.ecs;

import com.wjduquette.george.model.TerrainType;

/**
 * A terrain tile in the world.  It will have a Cell and a Tile.
 */
public record Terrain(TerrainType type) { }

