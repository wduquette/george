package com.wjduquette.george;

import com.wjduquette.george.ecs.Entity;
import com.wjduquette.george.ecs.ItemStack;
import com.wjduquette.george.model.Role;
import com.wjduquette.george.model.ItemSlot;
import com.wjduquette.george.model.Region;
import com.wjduquette.george.widgets.Action;
import com.wjduquette.george.widgets.SlotBox;

import java.util.Optional;

import static com.wjduquette.george.util.Combinator.*;

/**
 * The Stevedore system is responsible for moving items between inventories.
 */
public class Stevedore {
    public static final int MAPPING_RADIUS = 20;

    private Stevedore() {} // Not instantiable

    /**
     * Gets the SlotBox for the given item slot.
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
            box.actions().addAll(listOf(
                when(item.item().type().isUsable(),
                    new Action("Use", () -> useItem(region, owner, slot.index()))),
                when(item.item().type().isEquippable(),
                    new Action("Equip", () -> equipItem(region, owner, slot.index()))),
                new Action("Drop", () -> dropItem(region, owner, slot.index()))
            ));
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
        var role = slot.role();
        var item = owner.equipment().get(role).orElse(null);
        var count = item != null ? 1 : 0;
        var box = new SlotBox(slot, count, item);

        if (count > 0) {
            box.actions().addAll(listOf(
                new Action("Unequip", () -> takeOff(region, owner, role))
            ));
        }

        return box;
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
            stack = region.makeItemStack().cell(owner.cell());
            region.entities().add(stack);
        }

        if (stack.inventory().add(item) != -1) {
            region.log("Dropped " + item.label().text());
            return true;
        } else {
            owner.inventory().add(item);
            region.log("There's no room here.");
            return false;
        }
    }

    public static boolean takeOff(Region region, Entity owner, Role role) {
        var item = owner.equipment().remove(role).orElseThrow();

        if (owner.inventory().add(item) != -1) {
            region.log("Unequipped " + item.label().text());
            return true;
        } else {
            region.log("Inventory is full.");
            owner.equipment().wear(role, item);
            return false;
        }
    }

    /**
     * Use the given item, if possible in this context.  Returns false if the
     * item could not currently be used.
     * @param region The region
     * @param owner The owner
     * @param index The index of the inventory slot
     * @return true or false
     */
    public static boolean useItem(Region region, Entity owner, int index) {
        // FIRST, get the item to use
        var item = owner.inventory().take(index).orElseThrow();
        var used = true;

        // NEXT, attempt to use it.
        switch (item.item().type()) {
            case SCROLL_OF_MAPPING -> {
                region.markSeen(owner.cell(), MAPPING_RADIUS);
                region.log("You know more about the vicinity.");
            }
            case VIAL_OF_HEALING -> {
                region.log(owner.label().text() + " is at full health.");
                used = false;
            }
            default -> {
                region.log("TODO: " + item.item().type());
                used = false;
            }
        }

        // NEXT, if we couldn't use it then put it back.
        if (!used) {
            owner.inventory().add(item);
        }

        return used;
    }

    /**
     * Equips the given item, if possible in this context.  Returns false if the
     * item could not currently be equipped.
     * @param region The region
     * @param owner The owner
     * @param index The index of the inventory slot
     * @return true or false
     */
    public static boolean equipItem(Region region, Entity owner, int index) {
        // FIRST, get the item to equip
        var item = owner.inventory().take(index).orElseThrow();

        // NEXT, get the equipment slot it should go in.
        var role = Role.ofItemType(item.item().type()).orElseThrow();

        // NEXT, get whatever is in the slot now.
        var oldItem = owner.equipment().remove(role);
        owner.equipment().wear(role, item);
        oldItem.ifPresent(i -> {
            owner.inventory().add(i);
            region.log("Unequipped " + i.label().text());
        });
        region.log("Equipped " + item.label().text());

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
