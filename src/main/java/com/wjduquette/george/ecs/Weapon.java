package com.wjduquette.george.ecs;

/**
 * Tags an Item entity as a Weapon.  For now this is just a tag;
 * later this will have weapon parameters.
 */
public class Weapon implements Component {
    @Override public String toString() {
        return "(Weapon)";
    }

}
