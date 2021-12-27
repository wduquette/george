package com.wjduquette.george.ecs;

/**
 * A "sprite": a thing that can be drawn in a Cell over the terrain.
 * Sprites are associated with many other components, but almost always
 * with Loc, since the rendering system renders sprites at the their locs.
 * @param name The sprite's name in the application sprites table.
 */
public record Sprite(String name) implements Component {
    @Override public String toString() {
        // We'd need the tile's name to do more than this.
        return "(Sprite " + name + ")";
    }
}
