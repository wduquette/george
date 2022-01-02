package com.wjduquette.george.util;

import java.util.ArrayList;
import java.util.List;

public class Combinator {
    private Combinator() {} // Not instantiable.

    /**
     * Returns a list of the non-null items.
     * @param items Input items, possibly null
     * @param <T> the item type
     * @return The list of non-null items
     */
    @SafeVarargs
    public static <T> List<T> listOf(T... items) {
        var list = new ArrayList<T>();
        for (var item : items) {
            if (item != null) {
                list.add(item);
            }
        }

        return list;
    }

    /**
     * Returns the value if the condition is true, and null otherwise.
     * @param condition The condition
     * @param value The value
     * @param <T> The value type
     * @return The value or null
     */
    public static <T> T when(boolean condition, T value) {
        return condition ? value : null;
    }

    /**
     * Returns the value if the object is non-null, and null otherwise.
     * @param o The object
     * @param value The value
     * @param <T> The value type
     * @return The value or null
     */
    public static <T> T with(Object o, T value) {
        return o != null ? value : null;
    }
}
