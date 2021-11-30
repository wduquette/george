package com.wjduquette.george.ecs;

/**
 * A "mobile": a thing that can move around the world.  It will have a
 * Cell and a Tile.  The Mobile-specific data is TODO.
 * @param name A stopgap parameter
 */
public record Mobile(String name) {
    @Override public String toString() {
        return "(Mobile " + name + ")";
    }
}
