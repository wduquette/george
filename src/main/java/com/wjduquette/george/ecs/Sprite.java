package com.wjduquette.george.ecs;

import javafx.scene.image.Image;

/**
 * A "sprite": a thing that can be drawn in a Cell over the terrain.
 * Sprites are associated with many other components, but almost always
 * with Loc, since the rendering system renders sprites at the their locs.
 * @param image The tile's image
 * @param name The tile's name
 */
public record Sprite(Image image, String name) implements Component {
    @Override public String toString() {
        // We'd need the tile's name to do more than this.
        return "(Sprite " + name + ")";
    }

    public double height() { return image.getHeight(); }
    public double width() { return image.getWidth(); }
}
