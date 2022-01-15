package com.wjduquette.george.ecs;

import com.wjduquette.george.model.Cell;
import com.wjduquette.george.model.Offset;

/**
 * A Loc is the logical location of an entity.
 * @param cell The cell
 */
public record Loc(Cell cell)
    implements Component
{
    @Override
    public String toString() {
        return "(Loc " + cell.row() + " " + cell.col() + ")";
    }
}
