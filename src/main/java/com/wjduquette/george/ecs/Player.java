package com.wjduquette.george.ecs;

import com.wjduquette.george.ecs.Component;

/**
 * This component represents a player character, with its name, stats, status,
 * etc. For the moment it's just a tag component; that will change.
 * It will be used as a component in the entities table.
 */
public record Player() implements Component {
    @Override
    public String toString() {
        return "(Player)";
    }
}
