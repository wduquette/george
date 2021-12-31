package com.wjduquette.george.model;

/**
 * The kinds of item slot in inventories and equipment. Used in the
 * GUI.
 */
public sealed interface ItemSlot {
    /**
     * A slot in an entity's inventory
     * @param id The entity ID
     * @param index The slot index
     */
    record Inventory(long id, int index) implements ItemSlot {}

    /**
     * A slot in the party's shared baggage.
     * @param index The slot index
     */
    record Party(int index) implements ItemSlot {}

    /**
     * A slot in an entity's equipment.
     * @param id The entity ID
     * @param role The kind of equipment.
     */
    record Equipment(long id, Equip role) implements ItemSlot {}
}
