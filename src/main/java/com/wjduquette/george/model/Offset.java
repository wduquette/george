package com.wjduquette.george.model;

/**
 * A cell offset in fractional rows and columns.  Used by the
 * Animator system.
 */
public record Offset(double rowOffset, double colOffset) {
    @Override
    public String toString() {
        return "(Offset " +
            String.format(" %.2f %.2f", rowOffset, colOffset) + ")";
    }
}
