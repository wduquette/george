package com.wjduquette.george.model;

import com.wjduquette.george.ecs.Entity;
import com.wjduquette.george.ecs.Loc;

/**
 * An algorithm that slides an offset from a starting cell to an ending cell
 * at a given speed.
 */
public class Slider {
    private final double baseRate = 0.2;
    private final int numSteps;
    private final double deltaR;
    private final double deltaC;
    private int step = 0;

    /**
     * Creates the slider.
     * @param start The entity's current cell
     * @param end The target cell.
     * @param speed The speed, nominally 1.0.
     */
    public Slider(Cell start, Cell end, double speed) {
        double totalR = end.row() - start.row();
        double totalC = end.col() - start.col();
        var rate = baseRate * speed;
        numSteps = (int)Math.ceil(Math.max(
            Math.abs(totalR/rate), Math.abs(totalC/rate)));
        deltaR = totalR/numSteps;
        deltaC = totalC/numSteps;
    }

    /**
     * Gets the next offset.
     */
    public Offset next() {
        if (step < numSteps) { ++step; }
        return new Offset(step * deltaR, step * deltaC);
    }

    public boolean hasNext() {
        return step < numSteps;
    }

    @Override
    public String toString() {
        return "(Slider " + step + " " + numSteps + ")";
    }
}
