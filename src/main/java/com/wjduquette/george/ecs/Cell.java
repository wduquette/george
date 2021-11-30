package com.wjduquette.george.ecs;

/**
 * A (row,column) cell: the location of an entity in the world
 * @param row The cell's row in the array of tiles
 * @param col The cell's column in the array of tiles
 */
public record Cell(int row, int col) {
    /**
     * Creates new cell at a delta from this cell.
     * @param rowDelta The row delta
     * @param colDelta The column delta
     * @return The new cell.
     */
    public Cell adjust(int rowDelta, int colDelta) {
        return new Cell(row + rowDelta, col + colDelta);
    }

    @Override public String toString() {
        return "(Cell " + row + " " + col + ")";
    }
}
