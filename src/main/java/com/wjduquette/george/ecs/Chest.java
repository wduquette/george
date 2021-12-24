package com.wjduquette.george.ecs;

import com.wjduquette.george.model.Opening;

/**
 * A chest feature on the map.
 */
public record Chest(
    String key,
    Opening state,
    String closedSprite,
    String openSprite
) implements Component {
    @Override public String toString() {
        return "(Chest " + key + " " + state + " " + closedSprite + " " +
            openSprite + ")";
    }

    /**
     * Returns a chest that's open, all else held equal.
     * @return The modified chest.
     */
    public Chest open() {
        return new Chest(key, Opening.OPEN, closedSprite, openSprite);
    }

    /**
     * Returns a chest that's closed, all else held equal.
     * @return The modified chest.
     */
    public Chest close() {
        return new Chest(key, Opening.CLOSED, closedSprite, openSprite);
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
}
