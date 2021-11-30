package com.wjduquette.george.ecs;

import com.wjduquette.george.model.TerrainType;

/**
 * A terrain feature, along with its terrain type.  It will also have
 * a Cell and a Tile.  If the terrain type is NONE, the feature has the
 * same terrain type as the underlying terrain.
 *
 * @param terrainType The feature's terrain type, which may differ from the
 *                    underlying terrain cell, or NONE.
 */
public record Feature(TerrainType terrainType) {
    @Override public String toString() {
        return "(Feature " + terrainType + ")";
    }
}
