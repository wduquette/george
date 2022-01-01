package com.wjduquette.george;

import com.wjduquette.george.ecs.Entity;
import com.wjduquette.george.ecs.ItemStack;
import com.wjduquette.george.model.ItemSlot;
import com.wjduquette.george.model.Region;
import com.wjduquette.george.widgets.Action;
import com.wjduquette.george.widgets.SlotBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The Stevedore system is responsible for moving items between inventories.
 */
public class Stevedore {
    private Stevedore() {} // Not instantiable

    /**
     * Gets the SlotBox for the given item slot.
     * TODO: This doesn't work.
     * - The panel from which the actions are called needs to repaint after
     *   each action.
     * - The SlotBox doesn't need to have the actions; we can produce the
     *   actions just for the selected slot on repaint.
     * @param region The region
     * @param owner The owner
     * @param itemSlot The slot
     * @return The SlotBox
     */
    public static SlotBox getSlotBox(
        Region region,
        Entity owner,
        ItemSlot itemSlot)
    {
        return switch (itemSlot) {
            case ItemSlot.Inventory slot -> invSlotBox(region, owner, slot);
            case ItemSlot.Party slot -> partySlotBox(region, owner, slot);
            case ItemSlot.Equipment slot -> equipSlotBox(region, owner, slot);
        };
    }

    private static SlotBox invSlotBox(
        Region region,
        Entity owner,
        ItemSlot.Inventory slot
    ) {
        var count = owner.inventory().count(slot.index());
        var item = owner.inventory().peek(slot.index());
        var box = new SlotBox(slot, count, item);

        if (count > 0) {
            if (item.item().type().isUsable()) {
                box.actions().add(new Action("Use",
                    () -> useItem(region, owner, slot.index())));
            }
            box.actions().add(new Action("Drop",
                () -> dropItem(region, owner, slot.index())));
        }

        return box;
    }

    private static SlotBox partySlotBox(
        Region region,
        Entity owner,
        ItemSlot.Party slot)
    {
        // TODO
        return null;
    }

    private static SlotBox equipSlotBox(
        Region region,
        Entity owner,
        ItemSlot.Equipment slot)
    {
        // TODO
        return null;
    }

    /**
     * Attempts to drop one of the owner's items item on the ground.
     * Returns false if the drop could not be completed.
     * @param region The region
     * @param owner The owning player
     * @param index The slot index
     * @return true on success and false otherwise.
     */
    public static boolean dropItem(Region region, Entity owner, int index) {
        // FIRST, get the item to drop
        var item = owner.inventory().take(index).orElseThrow();

        // NEXT, find or create a stack.
        var stack = region.findAt(owner.cell(), ItemStack.class).orElse(null);

        // TODO: Need to check for overflow, find adjacent cell.
        if (stack == null) {
            // TODO: Need to find an adjacent cell with no stack
            stack = region.makeItemStack().cell(owner.cell());
            region.entities().add(stack);
        }

        stack.inventory().add(item);

        region.log("Dropped " + item.label().text());
        return true;
    }

    public static boolean useItem(Region region, Entity owner, int index) {
        // FIRST, get the item to use
        var item = owner.inventory().take(index).orElseThrow();

        region.log("Used: " + item.label().text());

        // TODO: switch on the item type to make stuff happen.
        // TODO: Return false if the item can't be used in this context.

        return true;
    }

    /**
     * The mobile attempts to pick up everything in the stack entity, which
     * may be any entity with an Inventory.
     * @param region The region
     * @param mobile The mobile
     * @param stack An entity with an Inventory.
     */
    public static void takeAll(Region region, Entity mobile, Entity stack) {
        var inv = stack.inventory();

        for (int i = 0; i < inv.size(); i++) {
            Optional<Entity> oitem;
            while ((oitem = inv.take(i)).isPresent()) {
                var item = oitem.get();

                if (mobile.inventory().add(item) != -1) {
                    // If it fits, notify the player
                    region.log("Picked up: " + item.label().text());
                } else {
                    // Otherwise, put it back in the inventory, and go on
                    // to the next item.
                    inv.add(item);
                    continue;
                }
            }
        }

        if (inv.isEmpty()) {
            // Remove the stack if it's an ItemStack.
            if (stack.itemStack() != null) {
                region.entities().remove(stack.id());
            }
        } else {
            region.log("You didn't have room for everything.");
        }
    }
}
