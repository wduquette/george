package com.wjduquette.george;

import com.wjduquette.george.ecs.Entity;
import com.wjduquette.george.ecs.ItemStack;
import com.wjduquette.george.model.Region;

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
}
