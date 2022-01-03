package com.wjduquette.george.model;

/**
 * Equipment roles.  The player has an equipment slot for each role.
 */
public enum Role {
    /** Armor worn on the PC's body. */
    BODY(Items.Type.BODY_ARMOR),

    /** Armor worn on the PC's head. */
    HEAD(Items.Type.HEAD_GEAR),

    /** Armor worn on the PC's feet. */
    FEET(Items.Type.FOOT_WEAR),

    /** Hand-to-hand weapon wielded by the PC. */
    HAND(Items.Type.HAND_WEAPON),

    /** Ranged-weapon wielded by the PC. */
    RANGED(Items.Type.RANGED),

    /** Shield wielded by the PC. */
    SHIELD(Items.Type.SHIELD);

    private final Items.Type itemType;

    Role(Items.Type type) {
        this.itemType = type;
    }

    /**
     * The type of item that can go in this slot.
     * @return The type
     */
    public Items.Type itemType() {
        return itemType;
    }
}
