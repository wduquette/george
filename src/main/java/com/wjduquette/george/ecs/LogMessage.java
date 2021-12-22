package com.wjduquette.george.ecs;

/**
 * A log message, to be displayed over the map.
 */
public record LogMessage(long lastTick, String message) implements Component {
    @Override public String toString() {
        return "(LogMessage " + lastTick + " " + message + ")";
    }
}
