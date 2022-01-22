package com.wjduquette.george.model;

/**
 * A Posture is something that a mobile is currently doing, i.e.,
 * sleeping, wandering, chasing, sitting.  It is determined by
 * the mobile's assigned behavior, which in turn looks at the
 * mobile's current posture.  The app can sometimes influence
 * the mobile by setting its posture, depending on the assigned behavior.<p>
 *
 * Posture is visible to other mobiles, and might affect their behavior.<p>
 *
 * @author will
 */
public enum Posture {
    /** The mobile is actively chasing/attacking an enemy. */
    CHASING,

    /** The mobile is guarding a particular spot. */
    GUARDING,

    /** The mobile is running away from all obvious threats. */
    RUNNING_AWAY,

    /** The mobile is sitting in one place, not doing much of anything.
     * This is distinct from GUARDING, as guards can attack enemies. */
    SITTING,

    /** The mobile is sleeping, and unaware of what is going on around it. */
    SLEEPING,

    /** The mobile is going about its business, with no immediate
     * concern for other mobiles. */
    WANDERING;
}
