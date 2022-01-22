package com.wjduquette.george.model;

import com.wjduquette.george.ecs.Entity;
import com.wjduquette.george.model.behaviors.NaiveTimid;
import com.wjduquette.george.util.KeyDataTable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * This class provides factories for all creature types, by info key.
 */
public class Creatures {
    /**
     * The interface for creature factories
     */
    public interface Factory extends Function<String,Entity> {}

    //-------------------------------------------------------------------------
    // Instance Variables

    // The game info table for creatures
    protected final KeyDataTable info;

    // The factory functions, by info key.
    private final Map<String, Factory> factories =
        new HashMap<>();

    //-------------------------------------------------------------------------
    // Constructor

    /**
     * Creates the Items object.  For now, all creatures are defined here.  In
     * time, we might read them from a file.
     */
    public Creatures(Class<?> cls, String relPath) {
        info = new KeyDataTable(cls, relPath);

        // Hand Weapons
        define("creature.lady_bug", this::makeLadyBug);
    }

    //-------------------------------------------------------------------------
    // Factory Helpers

    /**
     * Makes a basic creature entity, reading the creature's data from the game
     * info file.
     * @param key The creature's key
     * @return The entity
     */
    private Entity makeSimple(String key) {
        var sprite = info.get(key, "sprite").orElseThrow();
        var label = info.get(key, "label").orElseThrow();
        return new Entity()
            .tagAsMobile(key)
            .label(label)
            .sprite(sprite);
    }

    /**
     * Makes "creature.lady_bug"
     * @param key The creature's key
     * @return The entity
     */
    private Entity makeLadyBug(String key) {
        // TODO: Could set default posture from info file.
        // TODO: Could maybe set default behavior from info file
        var creature = new CreatureData(key)
            .behavior(NaiveTimid.TRAIT)
            .posture(Posture.WANDERING)
            .noticeRange(12)
            .mp(4);
        return makeSimple(key).tagAsCreature(creature);
    }

    //-------------------------------------------------------------------------
    // Public API

    /**
     * Adds a factory to the map.
     * @param key The creature's info key
     * @param factory The creature's factory
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
            throw new IllegalArgumentException("Unknown creature key: " + key);
        }

        return factory.apply(key);
    }
}
