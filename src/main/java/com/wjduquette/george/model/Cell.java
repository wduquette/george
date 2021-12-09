package com.wjduquette.george.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * A (row,column) cell: the location of an entity in the world.
 *
 * <ul>
 *     <li>The game rules look only at (row,col).</li>
 *     <li>The rendering system draws the entity at (row + rowOffset,
 *         col + colOffset).</li>
 *     <li>The visual effects system can move the entity about on the
 *         screen, relative to its cell, by setting the offsets.</li>
 *     <li>When the game rules set an entity's cell, the offsets should be
 *         zeroed.</li>
 * </ul>
 *
 * <p>equals() and hashCode() look only at the logical row and column.</p>
 * @param row The cell's row index
 * @param col The cell's column index
 * @param rowOffset The cell's visible row offset in fractional tiles
 * @param colOffset The cell's visible column offset in fractional tiles
 */
public record Cell(int row, int col, double rowOffset, double colOffset) {
    /**
     * Creates a new Cell with offsets of 0.0.
     * @param row The row index
     * @param col The column index
     * @return Creates a new Cell with zeroed offsets.
     */
    public static Cell of(int row, int col) {
        return new Cell(row, col, 0, 0);
    }

    /**
     * Creates new cell at a delta from this cell.
     * @param rowDelta The row delta
     * @param colDelta The column delta
     * @return The new cell.
     */
    public Cell adjust(int rowDelta, int colDelta) {
        return new Cell(row + rowDelta, col + colDelta, 0, 0);
    }

    /**
     * Compute the Cartesian distance between this and the other cell for
     * purposes of the A* algorithm.
     *
     * @param other The other cell.
     * @return The Cartesian distance between the two cells.
     */
    public double distance(Cell other) {
        return Cell.cartesianDistance(this, other);
    }

    /** Compute the "diagonal" distance between this and the other cell: the
     * maximum of the horizontal and vertical distances.
     * This is the number of moves required to get between the
     * two cells in open terrain when diagonal moves are allowed.
     *
     * @param other The other cell
     * @return The diagonal distance.
     */
    public int diagonal(Cell other) {
        return Cell.diagonalDistance(this, other);
    }

    /**
     * Sort a list of cells by diagonal distance from
     * this cell, closest first.
     * TODO: possibly just return sorted list rather than sort in place.
     * @param list  The list to sort.
     */
    public void sortByDiagonal(List<Cell> list) {
        // Sort cells by distance from this.
        list.sort(Comparator.comparingInt(this::diagonal));
    }

    /**
     * Computes adjacent cells for the purposes of the A* algorithm.  It
     * doesn't matter whether or not the returned cells are out-of-bounds for
     * the  relevant map; the Assessor function will mark out-of-bounds cells
     * impassable.
     * @return The list of adjacent points.
     */
    public List<Cell> getAdjacent() {
        List<Cell> neighbors = new ArrayList<>();

        for (int r = row - 1; r <= row + 1; r++) {
            for (int c = col -1; c <= col + 1; c++) {
                Cell cell = new Cell(r,c, 0, 0);
                if (!cell.equals(this)) {
                    neighbors.add(cell);
                }
            }
        }

        return neighbors;
    }

    //-------------------------------------------------------------------------
    // Object Methods

    @Override public String toString() {
        if (rowOffset == 0.0 && colOffset == 0) {
            return "(Cell " + row + " " + col + ")";
        } else {
            return "(Cell " + row + " " + col + " " +
                rowOffset + " " + colOffset + ")";
        }
    }
    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (other == null)
            return false;
        if (getClass() != other.getClass())
            return false;
        Cell cell = (Cell) other;
        return row == cell.row && col == cell.col;
    }


    //-------------------------------------------------------------------------
    // Static Methods

    /**
     * Compute the cartesian distance between two cells.
     *
     * @param a The first cell
     * @param b The second cell
     * @return The cartesian distance.
     */
    public static double cartesianDistance(Cell a, Cell b) {
        return Math.sqrt(
            (b.row() - a.row())*(b.row() - a.row()) +
            (b.col() - a.col())*(b.col() - a.col()));
    }

    /**
     * Compute the "diagonal" distance between two cells: the
     * maximum of the horizontal and vertical distances.
     * This is the number of moves required to get between the
     * two cells in open terrain when diagonal moves are allowed.
     *
     * @param a The first cell
     * @param b The second cell
     * @return The diagonal distance.
     */
    public static int diagonalDistance(Cell a, Cell b) {
        return Math.max(
            Math.abs(a.row() - b.row()),
            Math.abs(a.col() - b.col()));
    }
}
