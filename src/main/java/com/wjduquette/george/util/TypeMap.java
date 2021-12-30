package com.wjduquette.george.util;

import java.util.*;

public final class TypeMap {
    //-------------------------------------------------------------------------
    // Instance Variables

    private final Map<Class<?>,Object> map = new HashMap<>();

    //-------------------------------------------------------------------------
    // Constructor

    /**
     * Creates an empty type map.
     */
    public TypeMap() {
        // nothing to do
    }

    /**
     * Creates a shallow copy of the type map.
     * @param other The other type map.
     */
    public TypeMap(TypeMap other) {
        map.putAll(other.map);
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
     * Remove the given class, if it's present.
     * @param cls The class
     */
    public void remove(Class<?> cls) {
        map.remove(cls);
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
        return Collections.unmodifiableSet(map.keySet());
    }

    /**
     * The TypeMap's values.
     * @return The collection of values
     */
    public Collection<Object> values() {
        return Collections.unmodifiableCollection(map.values());
    }
}

