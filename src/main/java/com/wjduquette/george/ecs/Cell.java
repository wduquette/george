package com.wjduquette.george.ecs;

/**
 * A (row,column) cell: the location of an entity in the world
 * @param row The cell's row in the array of tiles
 * @param col The cell's column in the array of tiles
 */
public record Cell(int row, int col) { }
