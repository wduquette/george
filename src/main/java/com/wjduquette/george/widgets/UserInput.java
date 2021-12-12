package com.wjduquette.george.widgets;

import com.wjduquette.george.model.Cell;

public sealed interface UserInput {
    /**
     * The user clicked on a map cell.
     */
    public record CellClick(Cell cell) implements UserInput {}

    /**
     * The user clicked on a player character's status box.
     */
    public record StatusBox(long playerId) implements UserInput {}
}
