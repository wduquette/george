package com.wjduquette.george.model;

import com.wjduquette.george.util.AStar;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * A (row,column) cell: the location of an entity in the world
 * @param row The cell's row in the array of tiles
 * @param col The cell's column in the array of tiles
 */
public record Cell(int row, int col) implements AStar.Point<Cell> {
    /**
     * Creates new cell at a delta from this cell.
     * @param rowDelta The row delta
     * @param colDelta The column delta
     * @return The new cell.
     */
    public Cell adjust(int rowDelta, int colDelta) {
        return new Cell(row + rowDelta, col + colDelta);
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

    //-------------------------------------------------------------------------
    // AStar.Point methods

    /**
     * Compute the Cartesian distance between this and the other cell for
     * purposes of the A* algorithm.
     *
     * @param other The other cell.
     * @return The Cartesian distance between the two cells.
     */
    @Override
    public double distance(Cell other) {
        return Cell.cartesianDistance(this, other);
    }

    /**
     * Computes adjacent cells for the purposes of the A* algorithm.  It
     * doesn't matter whether or not the returned cells are out-of-bounds for
     * the  relevant map; the Assessor function will mark out-of-bounds cells
     * impassable.
     * @return The list of adjacent points.
     */
    @Override
    public List<Cell> getAdjacent() {
        List<Cell> neighbors = new ArrayList<>();

        for (int r = row - 1; r <= row + 1; r++) {
            for (int c = col -1; c <= col + 1; c++) {
                Cell cell = new Cell(r,c);
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
        return "(Cell " + row + " " + col + ")";
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
