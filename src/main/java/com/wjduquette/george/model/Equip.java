package com.wjduquette.george.model;

/**
 * Equipment slot types
 */
public enum Equip {
    /** Armor worn on the PC's body. */
    ARMOR("equip.armor"),

    /** Armor worn on the PC's head. */
    HELMET("equip.helmet"),

    /** Armor worn on the PC's feet. */
    FOOTWEAR("equip.footwear"),

    /** Hand-to-hand weapon wielded by the PC. */
    HAND("equip.weapon"),

    /** Ranged-weapon wielded by the PC. */
    RANGED("equip.box"),

    /** Shield wielded by the PC. */
    SHIELD("equip.shield");

    // The slot's info key
    private final String key;

    Equip(String key) { this.key = key; }
}
