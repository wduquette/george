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
     * @param record The creature's record key
     * @return The entity
     */
    private Entity makeSimple(String record) {
        var creature = new CreatureData(record)
            .level(info.requireInt(record, "level"))
            .experience(info.requireInt(record, "experience"))
            .posture(Posture.valueOf(info.require(record, "posture").toUpperCase()))
            .noticeRange(info.requireInt(record, "noticeRange"))
            .mp(info.requireInt(record, "mp"));
        return new Entity()
            .tagAsMobile(record)
            .tagAsCreature(creature)
            .label(info.require(record, "label"))
            .sprite(info.require(record, "sprite"));
    }

    /**
     * Makes "creature.lady_bug"
     * @param record The creature's record key
     * @return The entity
     */
    private Entity makeLadyBug(String record) {
        // TODO: allow most behaviors to be defined in creature file.
        var entity = makeSimple(record);
        entity.creature().behavior(NaiveTimid.TRAIT);
        return entity;
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
