package com.wjduquette.george.ecs;

import com.wjduquette.george.model.Equip;

/**
 * Tags an Item entity as Armor.
 * Later this will have additional armor parameters.
 */
public record Armor() implements Component {
    @Override public String toString() {
        return "(Armor)";
    }
}
