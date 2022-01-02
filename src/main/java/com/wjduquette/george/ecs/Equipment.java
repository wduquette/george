package com.wjduquette.george.ecs;

import com.wjduquette.george.model.Equip;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Equipment implements Component {
    //-------------------------------------------------------------------------
    // Instance Variables

    // The map from equipment place to Item entity.  Items added must have
    // item().type() == equip.itemType()
    private final Map<Equip,Entity> map = new HashMap<>();

    //-------------------------------------------------------------------------
    // Constructor

    public Equipment() {
        // Nothing to do at the moment.
    }

    //-------------------------------------------------------------------------
    // API

    /**
     * Equips the item in the given place; the item must be compatible
     * with the place.
     * @param place The place
     * @param item The item
     */
    public void wear(Equip place, Entity item) {
        if (item.item().type() != place.itemType()) {
            throw new IllegalArgumentException(
                "Item cannot go in this equipment slot!");
        }
        map.put(place, item);
    }

    /**
     * Take and return the item from the given place.
     * @param place The place
     * @return The item
     */
    public Optional<Entity> remove(Equip place) {
        var item = map.get(place);
        map.put(place, null);
        return Optional.ofNullable(item);
    }

    /**
     * Get the item that's equipped in the given place.
     * @param place The place
     * @return The item
     */
    public Optional<Entity> get(Equip place) {
        return Optional.ofNullable(map.get(place));
    }
}
