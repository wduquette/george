package com.wjduquette.george.ecs;

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

    //-------------------------------------------------------------------------
    // Constructor

    /**
     * Creates a new player with the given name.
     * @param name The name.
     */
    public Player(String name) {
        this.name = name;
    }

    //-------------------------------------------------------------------------
    // Player API

    public String name() { return name; }

    //-------------------------------------------------------------------------
    // Component API

    @Override
    public String toString() {
        return "(Player " + name + ")";
    }
}
