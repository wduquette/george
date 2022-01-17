package com.wjduquette.george.ecs;

import com.wjduquette.george.model.Items;

/**
 * An item in the game.
 * @param key Its key in the items.keydata file
 * @param type Its Items.Type
 * @param value Its base sale value
 */
public record Item(String key, Items.Type type, int value) implements Component {
    public boolean stacks() { return type.stacks(); }

    @Override public String toString() {
        return "(Item " + key + " " + type + " " + value + ")";
    }
}
