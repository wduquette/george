package com.wjduquette.george.ecs;

import com.wjduquette.george.graphics.ImageInfo;
import com.wjduquette.george.model.Cell;
import com.wjduquette.george.model.TerrainType;
import com.wjduquette.george.util.TypeMap;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * An Entity is an entity in a World.  It contains multiple components of
 * various types; the components determine what kind of entity it is.
 */
public class Entity {
    //-------------------------------------------------------------------------
    // Static API

    // The counter used to assign IDs
    private static long nextId = 1;

    /**
     * Gets the "next ID".  This is for use when saving the game state.
     * @return the "next ID".
     */
    public static long getNextId() {
        return nextId;
    }

    /**
     * Sets the "next ID".  This is for use when loading the game state from
     * disk.
     * @param value The ID for the next created entity
     */
    public static void setNextId(long value) {
        nextId = value;
    }

    /**
     * A comparator for entities, comparing by entity ID.
     * @param a The first entity
     * @param b The second entity
     * @return -1, 0, or 1.
     */
    public static int oldestFirst(Entity a, Entity b) {
        return Long.compare(a.id(), b.id());
    }

    /**
     * A comparator for entities, comparing by entity ID.
     * @param a The first entity
     * @param b The second entity
     * @return -1, 0, or 1.
     */
    public static int newestFirst(Entity a, Entity b) {
        return Long.compare(b.id(), a.id());
    }

    //-------------------------------------------------------------------------
    // Instance Variables

    // The Entity's ID
    private final long id;

    // The TypeMap containing the components
    private final TypeMap components = new TypeMap();

    //-------------------------------------------------------------------------
    // Constructor

    /**
     * Creates an entity and assigns it the next entity ID.  This constructor
     * is for use during normal execution.
     */
    public Entity() {
        this(Entity.nextId++);
    }

    /**
     * Creates a new entity with no components and the given ID.  This
     * constructor is for use when loading entities from disk; the loader code
     * should be sure to call {@code Entity.setNextId()} before creating
     * entities normally.
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

    //-------------------------------------------------------------------------
    // Generic Component Operations


    /**
     * Puts a component into the entity, replacing any previous component
     * of the same type.
     * @param component The new component value
     * @param <T> The component type.
     * @return The entity itself, for fluency.
     */
    public <T extends Component> Entity put(T component) {
        components.put(component);
        return this;
    }

    /**
     * Remove the component of the given class, if any.
     * @param cls The class
     */
    public void remove(Class<? extends Component> cls) {
        components.remove(cls);
    }

    /**
     * Remove the component, if defined.
     * @param component The component
     * @throws IllegalArgumentException if this component doesn't belong to
     * the entity.
     */
    public void remove(Component component) {
        // TODO: consider defining TypeMap.values(), and using that.
        var old = components.get(component.getClass());
        if (!old.equals(component)) {
            throw new IllegalArgumentException(
                "Attempt to remove un-owned component: " + component);
        }
        components.remove(component.getClass());
    }

    /**
     * Get the component of the given class, requiring that it must exist.
     * @param cls The desired component class.
     * @param <T> The class
     * @return The component
     */
    public <T extends Component> T get(Class<T> cls) {
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
    public <T extends Component> Optional<T> find(Class<T> cls) {
        return Optional.ofNullable(components.get(cls));
    }

    //-------------------------------------------------------------------------
    // Location Component Methods

    /**
     * Returns the entity's cell, or null if it doesn't have one.
     * @return The cell
     */
    public Loc loc() { return components.get(Loc.class); }

    public Cell cell() {
        return components.get(Loc.class).cell();
    }

    public Entity cell(Cell cell) {
        put(Loc.of(cell));
        return this;
    }

    /**
     * Component constructor: sets the entity's cell given a row and column,
     * clearing any visual offset.
     * @param row The row index
     * @param col The column index
     * @return The entity
     */
    public Entity cell(int row, int col) {
        put(Loc.of(new Cell(row, col)));
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
    // Sprite Component Methods

    /**
     * Sets the entity's label
     * @param text The label text
     * @return The entity
     */
    public Entity label(String text) {
        put(new Label(text));
        return this;
    }

    /**
     * Gets the entity's label
     * @return The label
     */
    public Label label() { return components.get(Label.class); }

    /**
     * Gets the entity's Sprite, or null if none.
     * @return The sprite, or null.
     */
    public Sprite sprite() { return components.get(Sprite.class); }

    /**
     * Sets the entity's Sprite given an image and its name
     * @param name The sprite name
     * @return The entity
     */
    public Entity sprite(String name) {
        put(new Sprite(name));
        return this;
    }

    /**
     * Sets the entity's Sprite.
     * @param info The image info
     * @return The entity
     */
    public Entity sprite(ImageInfo info) {
        put(new Sprite(info.name()));
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
     * Tags the entity as a Feature entity.
     * @return The entity
     */
    public Entity tagAsFeature() { return put(new Feature()); }

    /**
     * Sets the entity's terrain type.
     * @param type The type
     * @return The entity
     */
    public Entity terrain(TerrainType type) {
        put(new Terrain(type));
        return this;
    }

    /**
     * Gets the entity's terrain type, as read from its Terrain component,
     * or NONE if it has no terrain type.
     * @return The TerrainType.
     */
    public TerrainType terrainType() {
        return find(Terrain.class)
            .map(Terrain::terrainType)
            .orElse(TerrainType.NONE);
    }

    /**
     * Gets the feature's Door component, or null if none.
     * @return The door.
     */
    public Door door() { return components.get(Door.class); }

    /**
     * Adds the door and updates the entity according to the door's state.
     * @param door The door
     * @return The entity
     */
    public Entity door(Door door) {
        return put(door)
            .put(door.label())
            .put(door.sprite())
            .put(door.terrain());
    }

    /**
     * Sets the entity's door component to DoorState.OPEN.
     * @return The entity.
     */
    public Entity openDoor() {
        door(door().open());
        return this;
    }

    /**
     * Sets the entity's door component to DoorState.CLOSED.
     * @return The entity.
     */
    public Entity closeDoor() {
        door(door().close());
        return this;
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

    /**
     * Gets the entity's Mannikin component, or null if none.
     * @return The component or null.
     */
    public Mannikin mannikin() { return components.get(Mannikin.class); }

    /**
     * Gets the entity's Exit data, or null if none.
     * @return The component, or null.
     */
    public Exit exit() { return components.get(Exit.class); }

    /**
     * Sets an Exit's region and point
     * @param region The region name
     * @param point The name of the entry point in that region
     * @return The entity
     */
    public Entity exit(String region, String point) {
        put(new Exit(region, point));
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
     * Component constructor: sets the Mobile given its key.
     * @param key The name
     * @return the entity
     */
    public Entity mobile(String key) {
        put(new Mobile(key));
        return this;
    }

    /**
     * Gets the entity's plan component, or null if none.
     * @return The component, or null.
     */
    public Plan plan() { return components.get(Plan.class); }

    /**
     * Tags the entity as a Player mobile
     * @return The entity
     */
    public Entity tagAsPlayer() { return put(new Player()); }

    //-------------------------------------------------------------------------
    // Log Messages

    /**
     * Gets a log message's LogMessage component.
     * @return The component
     */
    public LogMessage logMessage() {
        return components.get(LogMessage.class);
    }

    //-------------------------------------------------------------------------
    // Player Methods

    public Player player() { return components.get(Player.class); }

    //-------------------------------------------------------------------------
    // Object Methods

    @Override
    public String toString() {
        return "(Entity " + componentString() + ")";
    }

    public String componentString() {
        StringBuilder buff = new StringBuilder();
        for (Class<?> cls : components.keySet()) {
            buff.append("\n ")
                .append(components.get(cls).toString());
        }

        return buff.toString();
    }
}
