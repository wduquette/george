package com.wjduquette.george.ecs;

import com.wjduquette.george.graphics.ImageInfo;
import com.wjduquette.george.model.*;
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
    private final TypeMap components;

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
        components = new TypeMap();
    }

    /**
     * Creates a shallow copy of the given entity, assigning a new ID.
     * This should only be used for entities that are effectively immutable.
     * @param other The other entity
     */
    public Entity(Entity other) {
        this.id = Entity.nextId++;
        this.components = other.components;
    }

    //-------------------------------------------------------------------------
    // General API

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
     * Puts a component into the entity, replacing any previous component
     * of the same class.
     * @param component The new component value
     * @param <T> The component type.
     * @return The entity itself, for fluency.
     */
    public <T extends Component> Entity put(T component) {
        components.put(component);
        return this;
    }

    /**
     * Removes the component of the given class, if any.
     * @param cls The class
     */
    public void remove(Class<? extends Component> cls) {
        components.remove(cls);
    }

    /**
     * Removes the component, if defined.
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
        return components.get(cls);
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
    // Component Retrieval Methods
    //
    // There is one for each defined component class. Each returns the
    // component, throwing an error if it doesn't exist.

    public Chest      chest()      { return components.get(Chest.class); }
    public Door       door()       { return components.get(Door.class); }
    public Exit       exit()       { return components.get(Exit.class); }
    public Feature    feature()    { return components.get(Feature.class); }
    public HandWeapon handWeapon() { return components.get(HandWeapon.class); }
    public Item       item()       { return components.get(Item.class); }
    public ItemStack  itemStack()  { return components.get(ItemStack.class); }
    public Inventory  inventory()  { return components.get(Inventory.class); }
    public Label      label()      { return components.get(Label.class); }
    public Loc        loc()        { return components.get(Loc.class); }
    public Mannikin   mannikin()   { return components.get(Mannikin.class); }
    public Mobile     mobile()     { return components.get(Mobile.class); }
    public Plan       plan()       { return components.get(Plan.class); }
    public Player     player()     { return components.get(Player.class); }
    public Point      point()      { return components.get(Point.class); }
    public Sign       sign()       { return components.get(Sign.class); }
    public Sprite     sprite()     { return components.get(Sprite.class); }
    public Terrain    terrain()    { return components.get(Terrain.class); }
    public Tripwire   tripwire()   { return components.get(Tripwire.class); }

    // Other simple queries

    /**
     * Gets the entity's cell location (taken from its Loc component).
     * @return The cell.
     */
    public Cell cell() { return components.get(Loc.class).cell(); }

    /**
     * Returns true if the entity is at the given cell, and false otherwise.
     * @param cell The cell
     * @return true or false
     */
    public boolean isAt(Cell cell) { return Objects.equals(cell(), cell); }

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
     * Returns true if a transition is in progress, and false otherwise.
     * I.e., it returns true if the next step in the plan has
     * {@code isTransition() }.
     * @return true or false
     */
    public boolean isTransitionInProgress() {
        if (plan() == null) {
            return false;
        }
        var step = plan().peekFirst();

        return step != null && step.isTransition();
    }

    //-------------------------------------------------------------------------
    // Component Setters
    //
    // tagAs* methods add the tag component.
    // Others just add the component given the arguments.

    public Entity tagAsFeature() { return put(new Feature()); }
    public Entity tagAsHandWeapon() { return put(new HandWeapon()); }
    public Entity tagAsItemStack() { return put(new ItemStack()); }
    public Entity player(Player player) { return put(player).label(player.name()); }
    public Entity exit(String region, String point) { return put(new Exit(region, point)); }
    public Entity item(String key, Items.Type type) { return put(new Item(key, type)); }
    public Entity label(String text) { return put(new Label(text)); }
    public Entity mannikin(String key) { return put(new Mannikin(key)); }
    public Entity mobile(String key) { return put(new Mobile(key)); }
    public Entity point(String name) { return put(new Point(name)); }
    public Entity sign(String text) { put(new Sign(text)); return this; }
    public Entity sprite(String name) { return put(new Sprite(name)); }
    public Entity sprite(ImageInfo info) { return put(new Sprite(info.name())); }
    public Entity terrain(TerrainType type) { return put(new Terrain(type)); }
    public Entity tripwire(Trigger trigger, Step step) { return put(new Tripwire(trigger, step)); }

    /**
     * Sets the entity's cell location, clearing any visual offsets.
     * @param cell The cell
     * @return The entity
     */
    public Entity cell(Cell cell) { return put(Loc.of(cell)); }

    /**
     * Sets the entity's cell location given a row and column, clearing any
     * visual offset.
     * @param row The row index
     * @param col The column index
     * @return The entity
     */
    public Entity cell(int row, int col) { return put(Loc.of(new Cell(row, col))); }

    /**
     * Adds the chest, and updates the entity's label and sprite according to
     * the chest's state.
     * @param chest The chest component
     * @return The entity
     */
    public Entity chest(Chest chest) {
        return put(chest)
            .put(chest.label())
            .put(chest.sprite());
    }

    /**
     * Adds the door and updates the entity's label, sprite, and terrain
     * according to the door's state.
     * @param door The door
     * @return The entity
     */
    public Entity door(Door door) {
        return put(door)
            .put(door.label())
            .put(door.sprite())
            .put(door.terrain());
    }


    //-------------------------------------------------------------------------
    // Entity Operations

    /**
     * Sets the entity's chest's state to DoorState.OPEN, updating relevant
     * components.
     * @return The entity.
     */
    public Entity openChest() {
        chest().open();
        return chest(chest());  // Resets label and sprite
    }

    /**
     * Sets the entity's chest's state to DoorState.CLOSED, updating relevant
     * @return The entity.
     */
    public Entity closeChest() {
        chest().close();
        return chest(chest());  // Resets label and sprite
    }

    /**
     * Sets the entity's door's state to DoorState.OPEN, updating relevant
     * components
     * @return The entity.
     */
    public Entity openDoor() { return door(door().open()); }

    /**
     * Sets the entity's door's state to DoorState.CLOSED, updating relevant
     * @return The entity.
     */
    public Entity closeDoor() { return door(door().close()); }


    //-------------------------------------------------------------------------
    // Object Methods

    @Override
    public String toString() {
        return "(Entity " + id + " " + componentString() + ")";
    }

    public String componentString() {
        StringBuilder buff = new StringBuilder();
        for (Class<?> cls : components.keySet()) {
            buff.append(" ")
                .append(components.get(cls).toString());
        }

        return buff.toString();
    }
}
