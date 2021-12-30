package com.wjduquette.george.ecs;

import com.wjduquette.george.model.Inventory;

/**
 * This component represents a player character, with its name, stats, status,
 * etc.
 */
public class Player implements Component {
    public static final int INVENTORY_SIZE = 20;

    //-------------------------------------------------------------------------
    // Instance variables

    // The player's name.  This will be used to set its Label.
    private final String name;

    // Hit Points: Possibly, this should be a component shared with
    // monsters.
    private int hitPoints = 0;
    private int maxHitPoints = 0;

    // Inventory
    private final Inventory inventory;

    //-------------------------------------------------------------------------
    // Constructor

    /**
     * Creates a new player with the given name.
     * @param name The name.
     */
    public Player(String name) {
        this.name = name;
        this.inventory = new Inventory(INVENTORY_SIZE);
    }

    //-------------------------------------------------------------------------
    // Player API

    public String name() { return name; }
    public Inventory inventory() { return inventory; }

    /**
     * Initializes the player's hit point stats.
     * @param hitPoints The number of hit points
     * @param maxHitPoints The maximum number of hit points.
     */
    public void setHitPoints(int hitPoints, int maxHitPoints) {
        this.hitPoints = hitPoints;
        this.maxHitPoints = maxHitPoints;
    }

    /**
     * Indicates whether the player has taken damage or not.
     * @return true or false.
     */
    public boolean isInjured() {
        return hitPoints < maxHitPoints;
    }

    //-------------------------------------------------------------------------
    // Component API

    @Override
    public String toString() {
        return "(Player " + name + ")";
    }
}
