package com.wjduquette.george;

import com.wjduquette.george.ecs.Entity;
import com.wjduquette.george.ecs.ItemStack;
import com.wjduquette.george.model.Region;

import java.util.Optional;

/**
 * The Stevedore system is responsible for moving items between inventories.
 */
public class Stevedore {
    private Stevedore() {} // Not instantiable

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
