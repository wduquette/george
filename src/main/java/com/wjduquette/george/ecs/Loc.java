package com.wjduquette.george.ecs;

import com.wjduquette.george.model.Cell;

/**
 * A Loc is a location, combining the logical location (a Cell) with
 * offsets in row and column coordinates, which are used to animate the
 * entity's Tile.
 * @param cell The cell
 * @param rowOffset The row offset, in fractional rows
 * @param colOffset The column offset, in fractional columns
 */
public record Loc(Cell cell, double rowOffset, double colOffset)
    implements Component
{
    /**
     * Creates a new location at the cell with zero offsets.
     * @param cell The cell
     * @return The Loc
     */
    public static Loc of(Cell cell) {
        return new Loc(cell, 0.0, 0.0);
    }

    /**
     * Returns a new Loc with an updated offset.
     * @param newRowOffset The new row offset, in fractional rows
     * @param newColOffset The new column offset, in fractional columns
     * @return The new Loc
     */
    public Loc offset(double newRowOffset, double newColOffset) {
        return new Loc(cell, newRowOffset, newColOffset);
    }

    @Override
    public String toString() {
        var start = "(Loc " + cell.row() + " " + cell.col();

        String offset = "";
        if (rowOffset != 0.0 || colOffset != 0.0) {
            offset = String.format(" %.2f %.2f", rowOffset, colOffset);
        }

        return start + offset + ")";
    }
}
