package com.wjduquette.george.ecs;

import com.wjduquette.george.model.Cell;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

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
    }

    public Set<Long> ids() {
        return entities.keySet();
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
     * Adds and returns a new entity with a unique ID.
     * @return the entity
     */
    public Entity make() {
        var entity = new Entity();
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
     * Finds the first entity with a given set of components at the given
     * cell.
     * @param cell The cell
     * @param components The required components
     * @return The entity, if found
     */
    public Optional<Entity> findAt(Cell cell, Class<?>...components) {
        return query(components).filter(e -> e.isAt(cell)).findFirst();
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
        for (Entity e : entities.values()) {
            System.out.println(e);
        }
    }
}
