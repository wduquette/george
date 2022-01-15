package com.wjduquette.george.model;

/**
 * A cell offset in fractional rows and columns.  Used by the
 * Animator system.
 */
public record Offset(double row, double col) {
    public static final Offset ZERO = new Offset(0.0, 0.0);

    @Override
    public String toString() {
        return "(Offset " +
            String.format(" %.2f %.2f", row, col) + ")";
    }
}
