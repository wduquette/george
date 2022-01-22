package com.wjduquette.george.ecs;

import com.wjduquette.george.model.CreatureData;

/**
 * Wraps a Creature as a component.
 */
public record Creature(CreatureData creature) implements Component {
    @Override public String toString() { return creature.toString(); }
}
