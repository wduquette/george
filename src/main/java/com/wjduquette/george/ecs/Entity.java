package com.wjduquette.george.ecs;

import com.wjduquette.george.model.Cell;
import com.wjduquette.george.model.TerrainType;
import com.wjduquette.george.util.TypeMap;
import javafx.scene.image.Image;

import java.util.Objects;
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
    // Component Getters

    /**
     * Get the component of the given class
     * @param cls The desired component class.
     * @param <T> The class
     * @return The component
     */
    public <T> Optional<T> get(Class<T> cls) {
        return Optional.ofNullable(components.get(cls));
    }

    public Cell cell()    { return components.get(Cell.class); }
    public Feature feature() { return components.get(Feature.class); }
    public Tile    tile()    { return components.get(Tile.class); }
    public Mobile  mobile()  { return components.get(Mobile.class); }
    public Point   point()   { return components.get(Point.class); }

    /**
     * Gets whether the entity is at the given cell or not.
     * @param cell The cell
     * @return true or false
     */
    public boolean isAt(Cell cell) {
        return Objects.equals(cell(), cell);
    }

    public TerrainType terrainType() {
        return get(Feature.class)
            .map(f -> f.terrainType())
            .orElse(TerrainType.NONE);
    }

    //-------------------------------------------------------------------------
    // Component Setters

    /**
     * Puts a component into the entity, replacing any previous component
     * of the same type.
     * @param component The new component value
     * @param <T> The component type.
     */
    public <T> Entity put(T component) {
        components.put(component);
        return this;
    }

    public Entity cell(int row, int col) {
        put(new Cell(row, col));
        return this;
    }

    public Entity tile(Image img) {
        put(new Tile(img));
        return this;
    }

    public Entity mobile(String name) {
        put(new Mobile(name));
        return this;
    }

    public Entity feature(TerrainType type) {
        put(new Feature(type));
        return this;
    }

    public Entity point(String name) {
        put(new Point(name));
        return this;
    }

    public Entity sign(String text) {
        put(new Sign(text));
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
