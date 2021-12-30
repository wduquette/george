package com.wjduquette.george.model;

import com.wjduquette.george.ecs.Entity;
import com.wjduquette.george.util.KeyDataTable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * This class provides factories for all item types, by info key.
 */
public class Items {
    /**
     * The interface for item factories
     */
    public interface Factory extends Function<String,Entity> {}

    /** The available item types. */
    public enum Type {
        //               stacks?
        NONE             (false),   // No item here
        KEY_ITEM         (false),
        VIAL_OF_HEALING  (true),
        SCROLL_OF_MAPPING(true);

        // Whether or not items of this type can be stacked.  If they can,
        // all items of this type must be effectively identical.
        private final boolean stacks;

        Type(boolean stacks) {
            this.stacks = stacks;
        }

        public boolean stacks() { return stacks; }
    }

    //-------------------------------------------------------------------------
    // Instance Variables

    // The game info table for items
    protected final KeyDataTable info;

    // The factory functions, by info key.
    private final Map<String, Factory> factories =
        new HashMap<>();

    //-------------------------------------------------------------------------
    // Constructor

    /**
     * Creates the Items object.  For now, all items are defined here.  In
     * time, we might read them from a file.
     */
    public Items(Class<?> cls, String relPath) {
        info = new KeyDataTable(cls, relPath);

        define("scroll.mapping", this::makeSimple);
        define("vial.healing", this::makeSimple);
    }

    //-------------------------------------------------------------------------
    // Factory Helpers

    /**
     * Makes an Item entity, reading the item's data from the game info file.
     * @param key The item's key
     * @return The entity
     */
    private Entity makeSimple(String key) {
        var type = Type.valueOf(info.get(key, "type").orElseThrow().toUpperCase());
        var sprite = info.get(key, "sprite").orElseThrow();
        var label = info.get(key, "label").orElseThrow();
        return new Entity()
            .item(key, type)
            .label(label)
            .sprite(sprite);
    }

    //-------------------------------------------------------------------------
    // Public API

    /**
     * Adds a factory to the map.
     * @param key The item's info key
     * @param factory The item's factory
     */
    public void define(String key, Factory factory) {
        factories.put(key, factory);
    }

    /**
     * Makes an entity of the given type.
     * @param key The key
     * @return The entity
     */
    public Entity make(String key) {
        Factory factory = factories.get(key);

        if (factory == null) {
            throw new IllegalArgumentException("Unknown item key: " + key);
        }

        return factory.apply(key);
    }

    /**
     * Makes a key item entity
     * @param key The item key
     * @param label The label
     * @param spriteName The sprite
     * @return The entity
     */
    public Entity makeKeyItem(String key, String label, String spriteName) {
        return new Entity()
            .item(key, Type.KEY_ITEM)
            .label(label)
            .sprite(spriteName);
    }
}
