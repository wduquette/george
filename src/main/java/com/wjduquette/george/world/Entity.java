package com.wjduquette.george.world;

import com.wjduquette.george.util.TypeMap;
import javafx.scene.image.Image;

import java.util.Optional;
import java.util.Set;

/**
 * An Entity is an entity in a World.  It contains multiple components of
 * various types; the components determine what kind of entity it is.
 */
public class Entity {
    //-------------------------------------------------------------------------
    // Instance Variables

    // The Entity's ID
    private final long id;

    // The TypeMap containing the components
    private final TypeMap components = new TypeMap();

    //-------------------------------------------------------------------------
    // Constructor

    /**
     * Creates a new entity with no components.
     * @param id The entity's ID
     */
    public Entity(long id) {
        this.id = id;
    }

    //-------------------------------------------------------------------------
    // Getters/Setters

    /**
     * Gets the Entity's ID.
     * @return The ID
     */
    public long id() {
        return id;
    }

    /**
     * Get the component of the given class
     * @param cls The desired component class.
     * @param <T> The class
     * @return The component
     */
    public <T> Optional<T> get(Class<T> cls) {
        return Optional.ofNullable(components.get(cls));
    }

    /**
     * Puts a component into the entity, replacing any previous component
     * of the same type.
     * @param component The new component value
     * @param <T> The component type.
     */
    public <T> void put(T component) {
        components.put(component);
    }

    /**
     * Returns true if the entity has all the components in the set.
     * @param componentSet A set of component classes
     * @return true or false
     */
    public boolean hasAll(Set<Class<?>> componentSet) {
        return components.keySet().containsAll(componentSet);
    }

    public boolean has(Class<?> component) {
        return components.get(component) != null;
    }

    //-------------------------------------------------------------------------
    // Specific component accessors

    public Cell    cell()    { return components.get(Cell.class); }
    public Tile    tile()    { return components.get(Tile.class); }
    public Mobile  mobile()  { return components.get(Mobile.class); }
    public Terrain terrain() { return components.get(Terrain.class); }

    //-------------------------------------------------------------------------
    // Entity Builder

    // TODO: I'd like to just roll this into Entity...but then mobile() and
    // terrain() are oversubscribed.  I want to keep the queries brief, so
    // these would be, what: putMobile()?  setMobile()?
    // And this isn't really a Builder, exactly....
    public static record Builder(Entity entity) {
        public Builder cell(int row, int col) {
            entity.put(new Cell(row, col));
            return this;
        }

        public Builder mobile() {
            entity.put(new Mobile());
            return this;
        }

        public Builder terrain() {
            entity.put(new Terrain());
            return this;
        }

        public Builder tile(Image img) {
            entity.put(new Tile(img));
            return this;
        }
    }
}
