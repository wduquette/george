package com.wjduquette.george.ecs;

import com.wjduquette.george.model.TerrainType;

/**
 * A component that marks the entity as a Feature entity.
 * @param name A name for display.
 */
public record Feature(String name) implements Component {
    @Override public String toString() {
        return "(Feature " + name + ")";
    }
}
