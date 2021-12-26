package com.wjduquette.george.ecs;

import com.wjduquette.george.model.Step;
import com.wjduquette.george.model.Trigger;

/**
 * A TripWire component: a component that causes the Analysis system to
 * execute the step when mover meets the trigger condition, replacing any
 * previous plan.
 * TODO: This is extremely preliminary
 */
public record Tripwire(Trigger trigger, Step step) implements Component {
    @Override public String toString() {
        return "(TripWire " + trigger + " " + step + " ";
    }
}
