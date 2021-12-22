package com.wjduquette.george.ecs;

import com.wjduquette.george.model.TerrainType;

/**
 * A component for an entity that effects its terrain.  Such an entity
 * will always have a Loc, and will usually have a Sprite.
 *
 * @param terrainType The entity's terrain type, which may differ from the
 *                    underlying terrain cell, or NONE for no effect.
 */
public record Terrain(TerrainType terrainType) implements Component {
    @Override public String toString() {
        return "(Terrain " + terrainType + ")";
    }
}
