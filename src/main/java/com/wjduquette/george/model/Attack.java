package com.wjduquette.george.model;

/**
 * The variants of this interface represent kinds of attack, and may be
 * associated with Weapon and Creature components.
 */
public sealed interface Attack {
    /**
     * A melee attack.
     * @param skill The attacker's skill level with this attack
     * @param damage The damage dice
     * @param verb The verb to use in the UI
     * @param hitSprite The sprite to use to animate the hit.
     */
    record Melee(
        int skill,
        Dice damage,
        String verb,
        String hitSprite
    ) implements Attack {}

    /**
     * A ranged attack.
     * @param skill The attacker's skill level with this attack
     * @param damage The damage dice
     * @param verb The verb to use in the UI
     * @param bulletSprite The sprite to use to animate the missile
     * @param hitSprite The sprite to use to animate the hit.
     */
    record Ranged(
        int skill,
        Dice damage,
        String verb,
        String bulletSprite,
        String hitSprite
    ) implements Attack {}
}
