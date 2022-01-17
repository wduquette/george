package com.wjduquette.george.ecs;

import com.wjduquette.george.model.Dice;

/**
 * Tags an Item entity as a Weapon.  For now this is just a tag;
 * later this will have weapon parameters.
 * @param damage The damage dice
 * @param range The range, 1 for melee weapons
 */
public record Weapon(Dice damage, int range) implements Component {
    @Override public String toString() {
        return "(Weapon " + damage + " " + range + ")";
    }
}
