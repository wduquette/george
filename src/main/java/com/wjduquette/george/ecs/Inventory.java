package com.wjduquette.george.ecs;

import com.wjduquette.george.model.Items.Type;

import java.util.Arrays;
import java.util.Optional;

/**
 * An inventory is an array of Item entities.  It has a maximum size, and
 * elements in the array can be empty.  Each element has a count, allowing for
 * stackable items.
 */
public class Inventory implements Component {
    //-------------------------------------------------------------------------
    // Types and Constants

    /** Content of any empty slot. */
    private static final Slot EMPTY = new Slot(null, 0);

    /**
     * A slot in the inventory.
     * @param entity The Item entity
     * @param count The number of items
     */
    private record Slot(Entity entity, int count) {
        /** Returns the type of the item in the slot. */
        public Type type() {
            return entity != null ? entity.item().type() : Type.NONE;
        }

        /** Returns a slot with an incremented count. */
        public Slot increment(int number) {
            if (equals(EMPTY)) {
                throw new IllegalArgumentException("Incrementing empty slot!");
            }
            if (!type().stacks()) {
                throw new IllegalArgumentException("Type does not stack!");
            }

            return new Slot(entity, count + number);
        }

        /**
         * Returns a slot with a decremented count, or empty if there are
         * no more items.
         * @param number The number of items to remove.
         * @return The new Slot value.
         */
        public Slot decrement(int number) {
            if (number > count) {
                throw new IllegalArgumentException(
                    "Too few items in slot: " + number);
            }

            if (number < count) {
                return new Slot(entity, count - number);
            } else {
                return EMPTY;
            }
        }
    }

    //-------------------------------------------------------------------------
    // Instance Variables

    // The slots.  The size is fixed.
    private final Slot[] slots;

    //-------------------------------------------------------------------------
    // Constructor

    /**
     * Creates an inventory with the given size.
     * @param size The inventory size
     */
    public Inventory(int size) {
        slots = new Slot[size];
        Arrays.fill(slots, EMPTY);
    }

    //-------------------------------------------------------------------------
    // Slot Manipulation

    /**
     * Removes everything from the inventory.
     */
    public final void clear() {
        Arrays.fill(slots, EMPTY);
    }

    public final int size() {
        return slots.length;
    }

    /**
     * Gets the slot at the index.
     * @param index The index
     * @return The item
     */
    protected Slot get(int index) { return slots[index]; }

    /**
     * Finds the index of a slot containing the type.
     * @param type The item type
     * @return The index, or -1 if not found
     */
    public int indexOf(Type type) {
        for (int i = 0; i < slots.length; i++) {
            if (slots[i].type() == type) { return i; }
        }
        return -1;
    }

    /**
     * Replaces the slot at the given index with the given slot.
     * @param index The index
     * @param slot The slot
     */
    protected void put(int index, Slot slot) {
        slots[index] = slot;
    }

    //-------------------------------------------------------------------------
    // Item Management

    /**
     * Gets the number of items in the inventory.
     * @return The number
     */
    public int count() {
        return Arrays.stream(slots)
            .map(slot -> slot.count)
            .reduce(0, Integer::sum);
    }

    /**
     * Gets the number of items in slot index.
     * @param index The index
     * @return The count.
     */
    public int count(int index) {
        return get(index).count();
    }

    /**
     * Peeks at the entity in the slot.
     * @param index The index
     * @return The entity, or null.
     */
    public Entity peek(int index) {
        return get(index).entity();
    }

    /**
     * Adds an Item entity, either stacking it with similar items or putting it
     * in the first open slot.  Returns the index of the slot the entity was
     * added to, or -1 if the entity couldn't be added
     * because the inventory is full.
     *
     * <p>Note: if the entity is stacked with others of its type, the
     * entity itself is gone.</p>
     * @param entity The entity to add.
     * @return The index of the slot.
     */
    public int add(Entity entity) {
        if (entity.item() == null) {
            throw new IllegalArgumentException("Adding non-Item to Inventory");
        }

        // FIRST, if the item is stackable, see if we already have a relevant
        // slot
        if (entity.item().stacks()) {
            var index = indexOf(entity.item().type());
            if (index != -1) {
                slots[index] = slots[index].increment(1);
                return index;
            }
        }

        // NEXT, look for the first empty slot.
        var index = indexOf(Type.NONE);

        if (index != -1) {
            slots[index] = new Slot(entity,1);
        }

        return index;
    }

    /**
     * Attempts to remove an entity of the given type from the inventory.
     * @param type The type
     * @return The entity, if found.
     */
    public Optional<Entity> take(Type type) {
        return take(indexOf(type));
    }

    /**
     * Attempts to remove an entity from the given slot.
     * @param index The slot index
     * @return The entity, if found.
     */
    public Optional<Entity> take(int index) {
        if (index == -1 || slots[index].count == 0) {
            return Optional.empty();
        } else if (slots[index].count == 1) {
            var entity = slots[index].entity;
            slots[index] = EMPTY;
            return Optional.of(entity);
        } else {
            // Make a shallow copy of the entity with its own index.
            var entity = new Entity(slots[index].entity);
            slots[index] = slots[index].decrement(1);
            return Optional.of(entity);
        }
    }

    /**
     * Gets whether the inventory is empty or not.
     * @return true or false
     */
    public boolean isEmpty() {
        for (var slot : slots) {
            if (slot != EMPTY) {
                return false;
            }
        }

        return true;
    }

    /**
     * Is slot index empty?
     * @param index The index
     * @return true or false
     */
    public boolean isEmpty(int index) {
        return get(index).count == 0;
    }

    @Override public String toString() {
        var buff = new StringBuilder();
        for (var i = 0; i < slots.length; i++) {
            if (slots[i] != EMPTY) {
                buff.append("[")
                    .append(String.format("%02d", i))
                    .append("] - ")
                    .append(slots[i].count)
                    .append(" ")
                    .append(slots[i].entity.label().text());
                buff.append("\n");
            }
        }
        return buff.toString();
    }
}
