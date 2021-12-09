package com.wjduquette.george.ecs;

import com.wjduquette.george.model.Cell;
import com.wjduquette.george.model.Player;
import com.wjduquette.george.model.Step;
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
     * Gets the Entity's ID.
     * @return The ID
     */
    public long id() {
        return id;
    }

    /**
     * Determines whether the entity has any components.
     * @return true or false
     */
    public boolean isEmpty() {
        return components.isEmpty();
    }

    /**
     * Returns true if the entity has all the components in the set.
     * @param componentSet A set of component classes
     * @return true or false
     */
    public boolean hasAll(Set<Class<?>> componentSet) {
        return components.keySet().containsAll(componentSet);
    }

    /**
     * Returns true if the entity has the given component.
     * @param component The component class
     * @return true or false
     */
    public boolean has(Class<?> component) {
        return components.get(component) != null;
    }

    //-------------------------------------------------------------------------
    // Generic Component Operations


    /**
     * Puts a component into the entity, replacing any previous component
     * of the same type.
     * @param component The new component value
     * @param <T> The component type.
     * @return The entity itself, for fluency.
     */
    public <T> Entity put(T component) {
        components.put(component);
        return this;
    }

    /**
     * Remove the component of the given class, if any.
     * @param cls The class
     */
    public void remove(Class<?> cls) {
        components.remove(cls);
    }

    /**
     * Get the component of the given class, requiring that it must exist.
     * @param cls The desired component class.
     * @param <T> The class
     * @return The component
     */
    public <T> T get(Class<T> cls) {
        T comp = components.get(cls);
        if (comp == null) {
            throw new IllegalArgumentException("Missing component: " + cls);
        }
        return comp;
    }

    /**
     * Finds the component of the given class if it exists, returning Optional.
     * @param cls The desired component class.
     * @param <T> The class
     * @return The component
     */
    public <T> Optional<T> find(Class<T> cls) {
        return Optional.ofNullable(components.get(cls));
    }

    //-------------------------------------------------------------------------
    // Cell Component Methods

    /**
     * Returns the entity's cell, or null if it doesn't have one.
     * @return The cell
     */
    public Cell cell() { return components.get(Cell.class); }

    /**
     * Component constructor: sets the entity's cell given a row and column
     * @param row The row index
     * @param col The column index
     * @return The entity
     */
    public Entity cell(int row, int col) {
        put(Cell.of(row, col));
        return this;
    }

    /**
     * Returns true if the entity is at the given cell, and false otherwise.
     * @param cell The cell
     * @return true or false
     */
    public boolean isAt(Cell cell) {
        return Objects.equals(cell(), cell);
    }

    //-------------------------------------------------------------------------
    // Tile Component Methods

    /**
     * Gets the entity's Tile, or null if none.
     * @return The tile, or null.
     */
    public Tile tile() { return components.get(Tile.class); }

    /**
     * Sets the entity's Tile given an image.
     * @param img The image
     * @return The entity
     */
    public Entity tile(Image img) {
        put(new Tile(img));
        return this;
    }

    //-------------------------------------------------------------------------
    // Map Feature component

    /**
     * Gets the entity's terrain feature, or null if none.
     * @return The feature, or null.
     */
    public Feature feature() { return components.get(Feature.class); }

    /**
     * Sets a feature given a terrain type.
     * @param type The type
     * @return The entity
     */
    public Entity feature(TerrainType type) {
        put(new Feature(type));
        return this;
    }

    /**
     * Gets the entity's terrain type, as read from its Feature component,
     * or NONE if it has no terrain type.
     * @return The TerrainType.
     */
    public TerrainType terrainType() {
        return find(Feature.class)
            .map(Feature::terrainType)
            .orElse(TerrainType.NONE);
    }

    //-------------------------------------------------------------------------
    // Points of Interest

    /**
     * Gets the entity's point of interest data, or null if none.
     * @return The component, or null.
     */
    public Point point() { return components.get(Point.class); }

    /**
     * Sets the entity's Point by name.
     * @param name The name
     * @return The entity
     */
    public Entity point(String name) {
        put(new Point(name));
        return this;
    }

    //-------------------------------------------------------------------------
    // Trigger Components

    // TODO Should probably be a more general Trigger entity.

    /**
     * Gets the entity's Sign data, or null if none.
     * @return The component, or null.
     */
    public Sign sign() { return components.get(Sign.class); }

    /**
     * Sets a Sign's text
     * @param text The text
     * @return The entity
     */
    public Entity sign(String text) {
        put(new Sign(text));
        return this;
    }

    //-------------------------------------------------------------------------
    // Mobile Entity Methods

    /**
     * Gets the entity's mobile component, or null if none.
     * @return The component, or null.
     */
    public Mobile mobile() { return components.get(Mobile.class); }

    /**
     * Component constructor: sets the Mobile given its name.
     * @param name The name
     * @return the entity
     */
    public Entity mobile(String name) {
        put(new Mobile(name));
        return this;
    }

    /**
     * Gets the entity's plan component, or null if none.
     * @return The component, or null.
     */
    public Plan plan() { return components.get(Plan.class); }

    /**
     * Gives the entity a new empty Plan
     * @return The entity
     */
    @Deprecated
    public Entity newPlan() {
        put(new Plan());
        return this;
    }

    //-------------------------------------------------------------------------
    // Player Methods

    public Player player() { return components.get(Player.class); }

    //-------------------------------------------------------------------------
    // Object Methods

    @Override
    public String toString() {
        StringBuilder buff = new StringBuilder();
        buff.append("(entity ").append(id);
        for (Class<?> cls : components.keySet()) {
            buff.append("\n ")
                .append(components.get(cls).toString());
        }
        buff.append(")");

        return buff.toString();
    }
}
