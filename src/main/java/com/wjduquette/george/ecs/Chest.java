package com.wjduquette.george.ecs;

import com.wjduquette.george.model.Opening;

/**
 * A chest feature on the map.
 */
public class Chest implements Component {
    public static final int INVENTORY_SIZE = 16;

    //-------------------------------------------------------------------------
    // Instance Variables

    // The chest's info key.  (Does it need one?)
    private final String key;

    // The sprites to use when the chest is open or closed.
    private final String openSprite;
    private final String closedSprite;

    // The chest's current state.
    private Opening state;

    //-------------------------------------------------------------------------
    // Constructor

    public Chest(
        String key,
        String closedSprite,
        String openSprite,
        Opening state)
    {
        this.key = key;
        this.closedSprite = closedSprite;
        this.openSprite = openSprite;
        this.state = state;
    }

    //-------------------------------------------------------------------------
    // Chest API

    /**
     * Opens the chest.
     */
    public void open() {
        state = Opening.OPEN;
    }

    /**
     * Closes the chest.
     */
    public void close() {
        state = Opening.CLOSED;
    }

    /**
     * Whether the chest is open or not
     * @return true or false
     */
    public boolean isOpen() {
        return state == Opening.OPEN;
    }

    /**
     * Whether the chest is closed or not
     * @return true or false
     */
    public boolean isClosed() {
        return state == Opening.CLOSED;
    }

    /**
     * Returns the expected value of the chest's label component given its
     * state.
     * @return The Label
     */
    public Label label() {
        if (isClosed()) {
            return new Label("Chest");
        } else {
            return new Label("Open chest");
        }
    }

    /**
     * Returns the expected value of the Door's sprite component given its
     * state.
     * @return the Sprite
     */
    public Sprite sprite() {
        if (isClosed()) {
            return new Sprite(closedSprite);
        } else {
            return new Sprite(openSprite);
        }
    }

    //-------------------------------------------------------------------------
    // Component API

    @Override public String toString() {
        return "(Chest " + key + " " + state + " " + closedSprite + " " +
            openSprite + ")";
    }

}
