package com.wjduquette.george.widgets;

import com.wjduquette.george.model.Cell;

public sealed interface UserInput {
    /**
     * The user clicked on a map cell.
     */
    public record CellClick(Cell cell) implements UserInput {}
}
