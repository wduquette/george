package com.wjduquette.george.world;

import java.util.*;
import java.util.stream.Stream;

/**
 * A World contains the entities of an Entity/Component/System.  It
 * represents a data set that can be operated on by the app's
 * Systems: or, more specifically, an area in the game.
 *
 * <p>The World is organized as a rectangular (but potentially sparse) array
 * of (row,column) cells. Available components include:</p>
 *
 * <ul>
 *     <li>Cell: A location in the world</li>
 *     <li>Tile: The tile used to draw a thing</li>
 *     <li>Mobile: A visible thing that can move about the world</li>
 *     <li>Terrain: An underlying terrain tile</li>
 * </ul>
 *
 * <p>TODO: How to efficiently support route and proximity computations?
 * Aha! I have a System that computes the needed data and passes it along to
 * subsequent systems.  Not there yet.</p>
 */
public class World {
    //-------------------------------------------------------------------------
    // Instance Variables

    // A counter used to generate entity IDs
    private long entityCounter = 0;

    // The entities in the world
    private Map<Long,Entity> entities = new HashMap<>();

    //-------------------------------------------------------------------------
    // Constructor

    public World() {
        // Nothing to do
    }

    //-------------------------------------------------------------------------
    // Public Methods

    /**
     * Makes a new entity in the world.
     * @return the entity
     */
    public Entity.Builder make() {
        var entity = new Entity(++entityCounter);
        entities.put(entity.id(), entity);
        return new Entity.Builder(entity);
    }

    /**
     * Removes the entity from the world.
     * @param id The ID
     */
    public void remove(long id) {
        entities.remove(id);
    }

    /**
     * Remove all data from the World, resetting the ID counter.
     */
    public void clear() {
        entities.clear();
        entityCounter = 0;
    }

    /**
     * Query for the entities that contain all of the given components.  This
     * allows systems to find the entities they care about.
     * @param components The list of component types
     * @return A stream of the entities
     */
    public Stream<Entity> query(Class<?>... components) {
        var set = Set.of(components);

        return entities.values().stream()
            .filter(e -> e.hasAll(set));
    }

    /**
     * Gets a stream of entities.
     * @return the stream.
     */
    public Stream<Entity> stream() {
        return entities.values().stream();
    }
}
