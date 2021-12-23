package com.wjduquette.george.ecs;

/**
 * A Mannikin is a feature that you can talk to.  Its key is a prefix for
 * entries in the region's info table:
 *
 * <ul>
 *     <li>{@code <key>.label}: The mannikin's display name.</li>
 *     <li>{@code <key>.sprite}: The name of the mannikin's sprite.</li>
 *     <li>{@code <key>.description}: The mannikin's description.</li>
 *     <li>{@code <key>.greeting*}: Things the mannikin can say.</li>
 * </ul>
 * @param key The mannikin's info key
 */
public record Mannikin(String key) implements Component {
    @Override public String toString() { return "(Mannikin " + key + ")"; }
}
