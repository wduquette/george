package com.wjduquette.george.ecs;

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
     * Determines whether the entity has any components.
     * @return true or false
     */
    public boolean isEmpty() {
        return components.isEmpty();
    }

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
    public <T> Entity add(T component) {
        components.put(component);
        return this;
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

    public Entity cell(int row, int col) {
        add(new Cell(row, col));
        return this;
    }

    public Entity tile(Image img) {
        add(new Tile(img));
        return this;
    }

    public Entity mobile(String name) {
        add(new Mobile(name));
        return this;
    }

    public Entity feature(TerrainType type) {
        add(new Feature(type));
        return this;
    }

    public Entity point(String name) {
        add(new Point(name));
        return this;
    }

    public Entity sign(String text) {
        add(new Sign(text));
        return this;
    }

    @Override
    public String toString() {
        StringBuilder buff = new StringBuilder();
        buff.append("(entity " + id);
        for (Class<?> cls : components.keySet()) {
            buff.append("\n ")
                .append(components.get(cls).toString());
        }
        buff.append(")");

        return buff.toString();
    }
}
