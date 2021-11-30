package com.wjduquette.george.ecs;

import com.wjduquette.george.model.TerrainTile;
import com.wjduquette.george.model.TerrainType;
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
    public Feature feature() { return components.get(Feature.class); }
    public Tile    tile()    { return components.get(Tile.class); }
    public Mobile  mobile()  { return components.get(Mobile.class); }
    public Point   point()   { return components.get(Point.class); }

    public Entity putCell(Cell cell) {
        put(cell);
        return this;
    }

    public Entity putCell(int row, int col) {
        put(new Cell(row, col));
        return this;
    }

    public Entity putTile(Image img) {
        put(new Tile(img));
        return this;
    }

    public Entity putMobile() {
        put(new Mobile());
        return this;
    }

    public Entity putFeature(TerrainType type) {
        put(new Feature(type));
        return this;
    }

    public Entity putPoint(String name) {
        put(new Point(name));
        return this;
    }

    public Entity putSign(String text) {
        put(new Sign(text));
        return this;
    }
}
