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
        //               stacks? usable? equip?
        NONE             (false, false,  false),   // No item here
        KEY_ITEM         (false, false,  false),
        BODY_ARMOR       (false, false,  true),
        HEAD_GEAR        (false, false,  true),
        FOOT_WEAR        (false, false,  true),
        HAND_WEAPON      (false, false,  true),
        RANGED           (false, false,  true),
        SHIELD           (false, false,  true),
        VIAL_OF_HEALING  (true,  true,   false),
        SCROLL_OF_MAPPING(true,  true,   false);

        // Whether or not items of this type can be stacked.  If they can,
        // all items of this type must be effectively identical.
        private final boolean stacks;

        // Whether or not this is an item that can be "Used", e.g.,
        // a potion that can be quaffed.
        private final boolean usable;

        // Whether or not this is an item that can be equipped.
        private final boolean equip;

        Type(boolean stacks, boolean usable, boolean equip) {
            this.stacks = stacks;
            this.usable = usable;
            this.equip = equip;
        }

        public boolean stacks() { return stacks; }
        public boolean isUsable() { return usable; }
        public boolean isEquippable() { return equip; }
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

        // Hand Weapons
        define("weapon.small_wrench", this::makeWeapon);

        // Ranged Weapons
        define("weapon.staple_gun", this::makeWeapon);

        // Body Armor
        define("body.overalls", this::makeArmor);

        // Headgear
        define("head.hat", this::makeArmor);

        // Footgear
        define("foot.shoes", this::makeArmor);

        // Usable Items
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
        var value = Integer.parseInt(info.get(key, "value").orElseThrow());
        return new Entity()
            .tagAsItem(key, type, value)
            .label(label)
            .sprite(sprite);
    }

    /**
     * Makes an armor Item entity, reading the item's data from the game
     * info file.
     * @param key The item's key
     * @return The entity
     */
    private Entity makeArmor(String key) {
        return makeSimple(key).tagAsArmor();
    }

    /**
     * Makes a weapon Item entity, reading the item's data from the game
     * info file.
     * @param key The item's key
     * @return The entity
     */
    private Entity makeWeapon(String key) {
        return makeSimple(key).tagAsWeapon();
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
            .tagAsItem(key, Type.KEY_ITEM, 0)
            .label(label)
            .sprite(spriteName);
    }
}
