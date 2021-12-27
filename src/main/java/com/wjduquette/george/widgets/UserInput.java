package com.wjduquette.george.widgets;

import com.wjduquette.george.model.Cell;

public sealed interface UserInput {
    /**
     * The user has clicked to continue.
     */
    record Continue() implements UserInput {}

    /**
     * The user wants to move to a specific map cell.  Usually a left click.
     * @param cell The cell
     */
    record MoveTo(Cell cell) implements UserInput {}

    /**
     * The user wants to interact with whatever is at the cell.
     * Usually a right-click.
     * @param cell The cell
     */
    record InteractWith(Cell cell) implements UserInput {}

    /**
     * The user clicked on a player character's status box.
     * @param playerId The player character's entity ID
     */
    record StatusBox(long playerId) implements UserInput {}

    /**
     * Requests that the debugger window be popped up.
     */
    record ShowDebugger() implements UserInput {}

    /**
     * Requests that the region map be displayed.
     */
    record ShowMap() implements UserInput {}
}
