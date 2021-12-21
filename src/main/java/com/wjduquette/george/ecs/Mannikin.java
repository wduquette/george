package com.wjduquette.george.ecs;

/**
 * A Mannikin is a feature that you can talk to.  Its name is a prefix for
 * entries in the region's strings table:
 *
 * <ul>
 *     <li>{@code <name>.name}: The mannikin's display name.</li>
 *     <li>{@code <name>.description}: The mannikin's description.</li>
 *     <li>{@code <name>.greeting*}: Things the mannikin can say.</li>
 * </ul>
 * @param name The mannikin's name
 */
public record Mannikin(String name) implements Component {
    @Override public String toString() { return "(Mannikin " + name + ")"; }
}
