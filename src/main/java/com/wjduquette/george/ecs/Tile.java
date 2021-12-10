package com.wjduquette.george.ecs;

import javafx.scene.image.Image;

/**
 * A "tile": a thing that can be drawn in a Cell.  Tiles are associated with
 * many other components, including Loc.
 * @param image The tile's image
 */
public record Tile(Image image) implements Component {
    @Override public String toString() {
        // We'd need the tile's name to do more than this.
        return "(Tile)";
    }

    public double height() { return image.getHeight(); }
    public double width() { return image.getWidth(); }
}
