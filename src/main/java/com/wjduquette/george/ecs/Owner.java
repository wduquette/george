package com.wjduquette.george.ecs;

/**
 * This component captures the owner of an Item.  The owner can be a
 * mobile, a chest, etc.
 */
public record Owner(long ownerId) implements Component {
    @Override public String toString() { return "(Owner " + ownerId + ")"; }
}
