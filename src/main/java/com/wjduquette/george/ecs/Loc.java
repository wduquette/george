package com.wjduquette.george.ecs;

import com.wjduquette.george.model.Cell;
import com.wjduquette.george.model.Offset;

/**
 * A Loc is a location, combining the logical location (a Cell) with
 * offsets in row and column coordinates, which are used to animate the
 * entity's sprite.
 * @param cell The cell
 * @param offset The offset, in fractional rows and columns
 */
public record Loc(Cell cell, Offset offset)
    implements Component
{
    /**
     * Creates a new location at the cell with zero offsets.
     * @param cell The cell
     * @return The Loc
     */
    public static Loc of(Cell cell) {
        return new Loc(cell, Offset.ZERO);
    }

    /**
     * Returns a new Loc with an updated offset.
     * @param newOffset The new offset
     * @return The new Loc
     */
    public Loc offset(Offset newOffset) {
        return new Loc(cell, newOffset);
    }

    @Override
    public String toString() {
        var start = "(Loc " + cell.row() + " " + cell.col();

        String offsetString = "";
        if (!offset.equals(Offset.ZERO)) {
            offsetString = String.format(" %.2f %.2f", offset.row(), offset.col());
        }

        return start + offsetString + ")";
    }
}
