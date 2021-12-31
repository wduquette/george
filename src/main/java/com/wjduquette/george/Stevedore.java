package com.wjduquette.george;

import com.wjduquette.george.ecs.Entity;
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
        // TODO: Need log.
        return false;
    }
}
