package com.wjduquette.george.model;

import javafx.scene.image.Image;

/**
 * A terrain tile.
 *
 * @param name        The name by which it's known in the tile set.
 * @param description The descriptive text
 * @param type        The terrain type
 * @param image       The actual image.
 */
public record TerrainTile(
    String name,
    TerrainType type,
    String description,
    Image image
) {
}
