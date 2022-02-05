package com.wjduquette.george.model;

/**
 * A creature's attitude toward the Player Characters.  This informs the
 * creature's behavior.  Attitude is set by creature type, and may be
 * changed by status effects.
 */
public enum Attitude {
    /**
     * The PCs are seen as friends; the creature will regard the PCs' enemies
     * as their enemies.
     */
    LOVES,

    /**
     * The creature doesn't care about the PCs and will ignore them.
     */
    IGNORES,

    /**
     * The creature hates the PCs; the creature will regard the PCs and their
     * friends as enemies.
     */
    HATES
}
