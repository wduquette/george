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
    public Entity remove(Class<? extends Component> cls) {
        components.remove(cls);
        return this;
    }

    /**
     * Removes the component, if defined.
     * @param component The component
     * @throws IllegalArgumentException if this component doesn't belong to
     * the entity.
     */
    public Entity remove(Component component) {
        // TODO: consider defining TypeMap.values(), and using that.
        var old = components.get(component.getClass());
        if (!old.equals(component)) {
            throw new IllegalArgumentException(
                "Attempt to remove un-owned component: " + component);
        }
        components.remove(component.getClass());
        return this;
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

    /**
     * True if the entity has the given component class.
     * @param cls The desired component class.
     * @return true or falseThe component
     */
    public <T extends Component> boolean has(Class<T> cls) {
        return components.get(cls) != null;
    }

    //-------------------------------------------------------------------------
    // Component Access Methods

    //
    // Preliminary -- Not sure what the access pattern will be.
    //

    // Armor
    public Entity  tagAsArmor()  { return put(new Armor()); }
    public Armor   armor()       { return components.get(Armor.class); }
    public boolean isArmor()     { return has(Armor.class); }

    // Weapon
    public Entity  tagAsWeapon() { return put(new Weapon()); }
    public Weapon  weapon()      { return components.get(Weapon.class); }
    public boolean isWeapon()    { return has(Weapon.class); }

    //
    // Type and TypeInfo components.  These components tag an entity as having
    // a particular type, and may include some static metadata (e.g., a
    // game info key).
    //

    // Exit
    public Entity tagAsExit(String region, String point) {
        return put(new Exit(region, point));
    }
    public boolean isExit() { return has(Exit.class); }
    public Exit exit() { return get(Exit.class); }

    // Feature
    public Entity tagAsFeature() { return put(new Feature()); }
    public boolean isFeature() { return has(Feature.class); }

    // Item
    public Entity tagAsItem(String key, Items.Type type) {
        return put(new Item(key, type));
    }
    public boolean isItem() { return has(Item.class); }
    public Item item() { return get(Item.class); }

    // ItemStack
    public Entity tagAsItemStack() { return put(new ItemStack()); }
    public boolean isItemStack() { return has(ItemStack.class); }

    // Mannikin
    public Entity tagAsMannikin(String key) { return put(new Mannikin(key)); }
    public boolean isMannikin() { return has(Mannikin.class); }
    public Mannikin mannikin() { return get(Mannikin.class); }

    // Mobile
    public Entity tagAsMobile(String key) { return put(new Mobile(key)); }
    public boolean isMobile() { return has(Mobile.class); }
    public Mobile mobile() { return get(Mobile.class); }

    // Narrative
    public Entity tagAsNarrative(String key) { return put(new Narrative(key)); }
    public boolean isNarrative() { return has(Narrative.class); }
    public Narrative narrative() { return get(Narrative.class); }

    // Player
    public Entity tagAsPlayer(String name) {
        return put(new Player(name)).label(name);
    }
    public boolean isPlayer() { return has(Player.class); }
    public Player player() { return get(Player.class); }

    // Point
    public Entity tagAsPoint(String name) { return put(new Point(name)); }
    public boolean isPoint() { return has(Point.class); }
    public Point point() { return get(Point.class); }

    // Sign
    public Entity tagAsSign(String text) { return put(new Sign(text)); }
    public boolean isSign() { return has(Sign.class); }
    public Sign sign() { return get(Sign.class); }


    //=========================================================================
    // Record Types - Records containing dynamic data associated with
    // the entity.
    // TODO: Possibly these should either be Structures or decomposed.

    //-------------------------------------------------------------------------
    // Chests

    /**
     * Adds the chest, and updates the entity's label and sprite according to
     * the chest's state.
     * @param chest The chest component
     * @return The entity
     */
    public Entity tagAsChest(Chest chest) {
        return put(chest)
            .put(chest.label())
            .put(chest.sprite());
    }

    public boolean isChest() { return has(Chest.class); }
    public Chest chest() { return get(Chest.class); }

    /**
     * Sets the entity's chest's state to DoorState.OPEN, updating relevant
     * components.
     * @return The entity.
     */
    public Entity openChest() {
        chest().open();
        return tagAsChest(chest());  // Resets label and sprite
    }

    /**
     * Sets the entity's chest's state to DoorState.CLOSED, updating relevant
     * @return The entity.
     */
    public Entity closeChest() {
        chest().close();
        return tagAsChest(chest());  // Resets label and sprite
    }

    //-------------------------------------------------------------------------
    // Doors

    /**
     * Adds the door and updates the entity's label, sprite, and terrain
     * according to the door's state.
     * @param door The door
     * @return The entity
     */
    public Entity tagAsDoor(Door door) {
        return put(door)
            .put(door.label())
            .put(door.sprite())
            .put(door.terrain());
    }

    public boolean isDoor() { return has(Door.class); }
    public Door door() { return get(Door.class); }

    /**
     * Sets the entity's door's state to DoorState.OPEN, updating relevant
     * components.
     * @return The entity.
     */
    public Entity openDoor() { return tagAsDoor(door().open()); }

    /**
     * Sets the entity's door's state to DoorState.CLOSED, updating relevant
     * components.
     * @return The entity.
     */
    public Entity closeDoor() { return tagAsDoor(door().close()); }

    //-------------------------------------------------------------------------
    // Tripwires

    public Entity tagAsTripwire(Trigger trigger, Step step) {
        return put(new Tripwire(trigger, step));
    }
    public boolean isTripwire() { return has(Tripwire.class); }
    public Tripwire tripwire() { return get(Tripwire.class); }

    //========================================================================
    // Entity Parts
    //
    // These components add information to one or more entity types.

    //-------------------------------------------------------------------------
    // Structures
    //
    // A structure is a component containing a complex mutable Java data
    // structures.  Usually they are created with the entity and edited in
    // place.

    // Miscellaneous
    public Equipment  equipment()  { return get(Equipment.class); }
    public Health     health()     { return get(Health.class); }
    public Inventory  inventory()  { return get(Inventory.class); }

    // Plan
    public Plan plan() { return get(Plan.class); }
    public Entity removePlan() { return remove(Plan.class); }
    public Entity newPlan() { return put(new Plan()); }

    //-------------------------------------------------------------------------
    // Fields - Single values associated with types

    // Label
    public String label() { return get(Label.class).text(); }
    public Entity label(String text) { return put(new Label(text)); }

    // Sprite
    public Entity sprite(String name) { return put(new Sprite(name)); }
    public Entity sprite(ImageInfo info) { return put(new Sprite(info.name())); }
    public String sprite() { return get(Sprite.class).name(); }

    public Loc        loc()        { return components.get(Loc.class); }
    public Terrain    terrain()    { return components.get(Terrain.class); }

    public Entity terrain(TerrainType type) { return put(new Terrain(type)); }

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
