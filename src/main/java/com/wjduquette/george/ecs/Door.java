package com.wjduquette.george.ecs;

import com.wjduquette.george.model.DoorState;

/**
 * A Door.  A Door can be open or closed, and has a named sprite for each
 * state.  A Door should also be a Feature.  When closed, the Feature's
 * terrain type will control passage and sight, e.g., DOOR or GATE. When
 * open, it should usually have a terrain type of NONE.
 * TODO: We will add an "unlockCondition" to some doors.
 */
public record Door(
    DoorState state,
    String closedSprite,
    String openSprite
) implements Component {
    /**
     * Returns a door that's open, with the same visuals.
     * @return The modified door.
     */
    public Door open() {
        return new Door(DoorState.OPEN, closedSprite, openSprite);
    }

    /**
     * Returns a door that's closed, with the same visuals.
     * @return The modified door.
     */
    public Door close() {
        return new Door(DoorState.CLOSED, closedSprite, openSprite);
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
}
