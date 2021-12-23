package com.wjduquette.george.ecs;

/**
 * A Label provides the display name for an entity.
 * @param text The label's text
 */
public record Label(String text) implements Component {
    @Override public String toString() { return "(Label " + text + ")"; }
}
