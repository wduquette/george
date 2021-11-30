package com.wjduquette.george.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class TypeMap {
    //-------------------------------------------------------------------------
    // Instance Variables

    private final Map<Class<?>,Object> map = new HashMap<>();

    //-------------------------------------------------------------------------
    // Constructor

    public TypeMap() {
        // nothing to do
    }

    //-------------------------------------------------------------------------
    // Public Methods

    /**
     * Determines whether the entity has any components.
     * @return true or false
     */
    public boolean isEmpty() {
        return map.isEmpty();
    }

    /**
     * Adds an object to the map.
     * @param value The object.
     */
    public void put(Object value) {
        this.map.put(value.getClass(), value);
    }

    /**
     * Retrieves the object of a given leaf type from the map.
     * @param cls The class
     * @param <T> The class type
     * @return The object, or null
     */
    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> cls) {
        return (T) this.map.get(cls);
    }

    /**
     * The TypeMap's key set.
     * @return The set
     */
    public Set<Class<?>> keySet() {
        return map.keySet();
    }
}

