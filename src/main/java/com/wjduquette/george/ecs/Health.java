package com.wjduquette.george.ecs;

import com.wjduquette.george.model.Condition;

import java.util.HashSet;
import java.util.Set;

public final class Health implements Component {
    //-------------------------------------------------------------------------
    // Instance Variables

    private int currentHP;
    private int maxHP;
    // TODO: Probably want something better here.
    private final Set<Condition> conditions = new HashSet<>();

    //-------------------------------------------------------------------------
    // Constructor

    /**
     * Creates a health component, setting the maximum hitpoints.
     * @param maxHP The maximum hit points.
     */
    public Health(int maxHP) {
        this.currentHP = maxHP;
        this.maxHP = maxHP;
    }

    //-------------------------------------------------------------------------
    // API

    /**
     * Has the mobile lost any hitpoints?
     * @return true or false
     */
    public boolean isInjured() {
        return currentHP < maxHP;
    }

    /**
     * Has the mobile lost all of its hitpoints?
     * @return true or false
     */
    public boolean isDead() {
        return currentHP == 0;
    }

    /**
     * Get the mobile's current HP
     * @return the HP
     */
    public int currentHP() {
        return currentHP;
    }

    /**
     * Explicitly set the mobile's current HP.
     * @param hp The new HP.
     */
    public void setCurrentHP(int hp) {
        this.currentHP = hp;
    }

    /**
     * Get the mobile's maximum HP
     * @return The maximum HP
     */
    public int maxHP() {
        return maxHP;
    }

    /**
     * Set the mobile's maximum HP.  Current is set to max.
     * @param max The maximum HP
     */
    public void setMaxHP(int max) {
        this.maxHP = max;
        this.currentHP = maxHP;
    }

    /**
     * Remove some number of hit points, but not below zero.
     * @param hp The amount of damage
     */
    public void damage(int hp) {
        currentHP = Math.max(currentHP - hp, 0);
    }

    /**
     * Add some number of hit points, but not above max
     * @param hp The amount of healing
     */
    public void heal(int hp) {
        currentHP = Math.min(maxHP, currentHP + hp);
    }

    /**
     * Heal the mobile all of the way.
     */
    public void healFully() {
        currentHP = maxHP();
    }
}
