package com.wjduquette.george.ecs;

/**
 * A tag component for stacks of items on the ground.  It is created when
 * items are dropped, and deleted when all of the items are taken.
 */
public class ItemStack implements Component {
    public final static int INVENTORY_SIZE = 100;
    @Override public String toString() { return "(ItemStack)"; }
}
