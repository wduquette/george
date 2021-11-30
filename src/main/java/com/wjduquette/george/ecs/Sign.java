package com.wjduquette.george.ecs;

/**
 * A sign on the map.  A sign is also a Feature, and has a Cell and a Tile.
 * TODO: This should probably be a more general kind of object: something that
 * can be interacted with.
 * @param name The name of the sign's string in the strings table.
 */
public record Sign(String name) {
    @Override public String toString() {
        return "(Sign " + name + ")";
    }
}
