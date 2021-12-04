package com.wjduquette.george.ecs;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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

    /** Compute the Cartesian distance between this and the other cell.
     *
     * @param cell The other cell.
     * @return The Cartesian distance between the two cells.
     */
    public double cartesian(Cell cell) {
        return cartesianDistance(row, col, cell.row, cell.col);
    }

    /** Compute the cartesian distance between two points.
     *
     * @param r1 Row coordinate of point 1
     * @param c1 Column coordinate of point 1
     * @param r2 Row coordinate of point 2
     * @param c2 Column coordinate of point 2
     * @return The cartesian distance.
     */
    public static double cartesianDistance(int r1, int c1, int r2, int c2) {
        return Math.sqrt((r2 - r1)*(r2 - r1) + (c2 - c1)*(c2 - c1));
    }

    /** Compute the "diagonal" distance between this and the other cell: the
     * maximum of the horizontal and vertical distances.
     * This is the number of moves required to get between the
     * two cells in open terrain when diagonal moves are allowed.
     *
     * @param cell The other cell
     * @return The diagonal distance.
     */
    public int diagonal(Cell cell) {
        return diagonal(row, col, cell.row, cell.col);
    }

    /** Compute the "diagonal" distance between two cells: the
     * maximum of the horizontal and vertical distances.
     * This is the number of moves required to get between the
     * two cells in open terrain when diagonal moves are allowed.
     *
     * @param r1 Row coordinate of point 1
     * @param c1 Column coordinate of point 1
     * @param r2 Row coordinate of point 2
     * @param c2 Column coordinate of point 2
     * @return The diagonal distance.
     */
    public int diagonal(int r1, int c1, int r2, int c2) {
        return Math.max(Math.abs(r1 - r2), Math.abs(c1 - c2));
    }

    /** Sort a list of cells by diagonal distance from
     * this cell, closest first.
     * @param list  The list to sort.
     */
    public void sortByDiagonal(List<Cell> list) {
        // Sort cells by distance from this.
        Collections.sort(list, new Comparator<Cell>() {
            public int compare(Cell c1, Cell c2) {
                return diagonal(c1) - diagonal(c2);
            }});
    }

    @Override public String toString() {
        return "(Cell " + row + " " + col + ")";
    }
}
