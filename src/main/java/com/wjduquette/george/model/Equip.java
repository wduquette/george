package com.wjduquette.george.model;

/**
 * Equipment slot types
 */
public enum Equip {
    /** Armor worn on the PC's body. */
    BODY("equip.armor"),

    /** Armor worn on the PC's head. */
    HEAD("equip.helmet"),

    /** Armor worn on the PC's feet. */
    FEET("equip.footwear"),

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
