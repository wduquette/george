package com.wjduquette.george.widgets;

import com.wjduquette.george.ecs.Entity;
import com.wjduquette.george.model.ItemSlot;

import java.util.ArrayList;
import java.util.List;

/**
 * A representation of an inventory or equipment slot.
 */
public class SlotBox {
    //-------------------------------------------------------------------------
    // Instance Variables

    private final ItemSlot slot;
    private final int count;
    private final Entity item;
    private final List<Action> actions = new ArrayList<>();

    //-------------------------------------------------------------------------
    // Constructor

    /**
     * Creates a slot box for the identified slot
     * @param slot The slot
     * @param count The item count in the slot
     * @param item The item entity, or null
     */
    public SlotBox(ItemSlot slot, int count, Entity item) {
        this.slot = slot;
        this.count = count;
        this.item = item;
    }

    //-------------------------------------------------------------------------
    // API

    public ItemSlot     slot()          { return slot; }
    public int          count()         { return count; }
    public Entity       item()          { return item; }
    public List<Action> actions()       { return actions; }
}
