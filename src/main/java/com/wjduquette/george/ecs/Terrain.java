package com.wjduquette.george.ecs;

import com.wjduquette.george.model.TerrainType;

/**
 * A component for an entity that effects the terrain type of its cell.  Such
 * an entity will always have a Loc, and will usually have a Sprite.  There's no
 * difference between having a Terrain(TerrainType.NONE) and having no terrain
 * component.
 *
 * @param terrainType The entity's terrain type
 */
public record Terrain(TerrainType terrainType) implements Component {
    @Override public String toString() {
        return "(Terrain " + terrainType + ")";
    }
}
