package com.wjduquette.george.widgets;

import com.wjduquette.george.model.Cell;

public sealed interface UserInput {
    /**
     * The user clicked on a map cell.
     * @param cell The cell
     */
    public record CellClick(Cell cell) implements UserInput {}

    /**
     * The user clicked on a player character's status box.
     * @param playerId The player character's entity ID
     */
    public record StatusBox(long playerId) implements UserInput {}

    /**
     * Requests that the debugger window be popped up.
     */
    public record ShowDebugger() implements UserInput {}
}
