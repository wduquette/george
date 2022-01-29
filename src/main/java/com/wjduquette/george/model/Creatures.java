package com.wjduquette.george.model;

import com.wjduquette.george.ecs.Entity;
import com.wjduquette.george.ecs.Health;
import com.wjduquette.george.model.behaviors.Behaviors;
import com.wjduquette.george.util.KeyFile;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * This class provides factories for all creature types, by info key.
 */
public class Creatures {
    public static final String BEHAVIOR = "behavior";
    public static final String EXPERIENCE = "experience";
    public static final String HP = "hp";
    public static final String LABEL = "label";
    public static final String LEVEL = "level";
    public static final String MP = "mp";
    public static final String NOTICE_RANGE = "noticeRange";
    public static final String POSTURE = "posture";
    public static final String SPRITE = "sprite";

    /**
     * The interface for creature factories
     */
    public interface Factory extends Function<String,Entity> {}

    //-------------------------------------------------------------------------
    // Instance Variables

    // The game info table for creatures
    protected final KeyFile info;

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
        info = new KeyFile(cls, relPath);

        // Hand Weapons
        define("creature.lady_bug", this::makeSimple);
        define("creature.manly_bug", this::makeSimple);
    }

    //-------------------------------------------------------------------------
    // Factory Helpers

    /**
     * Makes a basic creature entity, reading the creature's data from the game
     * info file.
     * @param recordKey The creature's record key
     * @return The entity
     */
    private Entity makeSimple(String recordKey) {
        var record = info.with(recordKey);
        var creature = new CreatureData(recordKey)
            .behavior(record.get(BEHAVIOR).as(Behaviors::valueOf).trait())
            .experience(record.get(EXPERIENCE).asInt())
            .level(record.get(LEVEL).asInt())
            .mp(record.get(MP).asInt())
            .noticeRange(record.get(NOTICE_RANGE).asInt())
            .posture(record.get(POSTURE).as(Posture::valueOf));
        return new Entity()
            .tagAsMobile(recordKey)
            .tagAsCreature(creature)
            .label(record.get(LABEL).asIs())
            .sprite(record.get(SPRITE).asIs())
            .put(new Health(record.get(HP).asInt()));
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
