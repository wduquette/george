package com.wjduquette.george.ecs;

/**
 * A component that tags the entity as a Feature entity.
 */
public record Feature() implements Component {
    @Override public String toString() {
        return "(Feature)";
    }
}
