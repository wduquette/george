package com.wjduquette.george.model;

import com.wjduquette.george.model.behaviors.Immobile;

/**
 * This class defines a variety of information about a creature: everything
 * that Creatures do not have in common with other Mobiles.
 */
public class CreatureData {
    //-------------------------------------------------------------------------
    // Instance Variables

    private final String key;

    // The creature's level
    private int level = 1;

    // Experience points awarded for slaying the creature
    private int experience = 1;

    // The creature's behavior
    private Behavior behavior = Immobile.TRAIT;

    // The creature's current posture
    private Posture posture = Posture.SITTING;

    // Whether the creature has been alerted or not.
    private boolean alerted = false;

    // The creature's notice range, in cells
    private int noticeRange = 0;

    // The creature's movement points
    private int mp = 0;

    //-------------------------------------------------------------------------
    // Constructor

    public CreatureData(String key) {
        this.key = key;
    }

    //-------------------------------------------------------------------------
    // API

    public int level() {
        return level;
    }

    public CreatureData level(int level) {
        this.level = level;
        return this;
    }

    public int experience() {
        return experience;
    }

    public CreatureData experience(int points) {
        this.experience = points;
        return this;
    }

    public Behavior behavior() {
        return behavior;
    }

    public CreatureData behavior(Behavior behavior) {
        this.behavior = behavior;
        return this;
    }

    public Posture posture() {
        return posture;
    }

    public CreatureData posture(Posture posture) {
        this.posture = posture;
        return this;
    }

    public boolean isAlerted() {
        return alerted;
    }

    public CreatureData alerted(boolean alerted) {
        this.alerted = alerted;
        return this;
    }

    public int noticeRange() {
        return noticeRange;
    }

    public CreatureData noticeRange(int noticeRange) {
        this.noticeRange = noticeRange;
        return this;
    }

    public int mp() {
        return mp;
    }

    public CreatureData mp(int mp) {
        this.mp = mp;
        return this;
    }

    @Override
    public String toString() {
        return "(Creature " + key + " " + posture + " " +
            behavior.getClass().getSimpleName() + ")";
    }
}
