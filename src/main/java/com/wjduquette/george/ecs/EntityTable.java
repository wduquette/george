package com.wjduquette.george.ecs;

import java.util.*;
import java.util.stream.Stream;

/**
 * An EntityTable contains the entities of an Entity/Component/System.  It
 * represents a data set that can be operated on by the app's
 * Systems.
 *
 * <p>The EntityTable is organized as a map of Entity objects, each of which
 * has a unique entity ID. Each Entity is a TypeMap from component types to
 * component values.</p>
 *
 * <p>It bugs me that I'm using a Map rather than an array: we step over
 * entities in no particular order.  Possibly it shouldn't bug be.</p>
 */
public class EntityTable {
    //-------------------------------------------------------------------------
    // Instance Variables

    // A counter used to generate entity IDs
    private long entityCounter = 0;

    // The entities in the world
    private final Map<Long,Entity> entities = new HashMap<>();

    //-------------------------------------------------------------------------
    // Constructor

    public EntityTable() {
        // Nothing to do
    }

    //-------------------------------------------------------------------------
    // Public Methods

    /**
     * Remove all data from the table, resetting the ID counter.
     */
    public void clear() {
        entities.clear();
        entityCounter = 0;
    }

    /**
     * Gets the entity with the given ID.
     * @param id The ID
     * @return The entity, or null if not found.
     */
    public Entity get(long id) {
        return entities.get(id);
    }

    /**
     * Removes the entity with the given ID.
     * @param id The ID
     */
    public void remove(long id) {
        entities.remove(id);
    }

    /**
     * Makes a new entity in the world, assigning it the next ID.
     * @return the entity
     */
    public Entity make() {
        var entity = new Entity(++entityCounter);
        entities.put(entity.id(), entity);
        return entity;
    }

    /**
     * Query for the entities that contain all of the given componentsm and
     * returns a stream. This allows systems to find the entities they care
     * about.
     * @param components The list of component types
     * @return A stream of the entities
     */
    public Stream<Entity> query(Class<?>... components) {
        var set = Set.of(components);

        return entities.values().stream()
            .filter(e -> e.hasAll(set));
    }

    /**
     * Gets a stream of entities.  This is equivalent to query() with no
     * arguments.
     * @return The stream
     */
    public Stream<Entity> stream() {
        return entities.values().stream();
    }

    /**
     * Dump the current set of entities to System.out.
     */
    public void dump() {
        for (long i = 0; i <= entityCounter; i++) {
            Entity e = entities.get(i);

            if (e != null) {
                System.out.println(e);
            }
        }
    }
}
