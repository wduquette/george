package com.wjduquette.george.ecs;

/**
 * A sign on the map.  A sign is Feature entity.
 * Its key is a prefix for entries in the region's info table:
 *
 * <ul>
 * <li>{@code <key>.text}: The sign's text.</li>
 * <li>{@code <key>.sprite}: The name of the sign's sprite.</li>
 * </ul>
 * @param key The sign's info key
 */
public record Sign(String key) implements Component {
    @Override public String toString() {
        return "(Sign " + key + ")";
    }
}
