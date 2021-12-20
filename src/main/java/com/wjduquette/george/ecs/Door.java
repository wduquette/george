package com.wjduquette.george.ecs;

import com.wjduquette.george.model.DoorState;
import com.wjduquette.george.model.TerrainType;

/**
 * A Door.  A Door can be open or closed, and has a named sprite for each
 * state.  A Door should also be a Feature.  When closed, the Feature's
 * terrain type will control passage and sight, e.g., DOOR or GATE. When
 * open, it should usually have a terrain type of NONE.
 * TODO: We will add an "unlockCondition" to some doors.
 * @param state The door's state, CLOSED or OPEN
 * @param closedTerrain The door's terrain type, when closed.
 * @param closedSprite The name of the sprite to display when closed.
 * @param openSprite The name of the sprite to display when open.
 */
public record Door(
    DoorState state,
    TerrainType closedTerrain,
    String closedSprite,
    String openSprite
) implements Component {
    /**
     * Returns a door that's open, all else held equal.
     * @return The modified door.
     */
    public Door open() {
        return new Door(DoorState.OPEN, closedTerrain, closedSprite, openSprite);
    }

    /**
     * Returns a door that's closed, all else held equal.
     * @return The modified door.
     */
    public Door close() {
        return new Door(DoorState.CLOSED, closedTerrain, closedSprite, openSprite);
    }

    /**
     * Whether the door is open or not
     * @return true or false
     */
    public boolean isOpen() {
        return state == DoorState.OPEN;
    }

    /**
     * Whether the door is closed or not
     * @return true or false
     */
    public boolean isClosed() {
        return state == DoorState.CLOSED;
    }

    /**
     * Returns the expected value of the Door's feature component given its
     * state.
     * @return The Feature
     */
    public Feature feature() {
        if (isClosed()) {
            return new Feature(closedTerrain);
        } else {
            return new Feature(TerrainType.NONE);
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
