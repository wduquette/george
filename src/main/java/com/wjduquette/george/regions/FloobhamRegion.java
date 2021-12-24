package com.wjduquette.george.regions;

import com.wjduquette.george.App;
import com.wjduquette.george.ecs.Chest;
import com.wjduquette.george.ecs.Entity;
import com.wjduquette.george.model.DataDrivenRegion;
import com.wjduquette.george.tmx.TiledMapReader;

public class FloobhamRegion extends DataDrivenRegion {
    //-------------------------------------------------------------------------
    // Constructor

    public FloobhamRegion(App app, Class<?> cls, String relPath) {
        super(app, cls, relPath);
    }

    //-------------------------------------------------------------------------
    // Customizations

    protected final boolean handleObject(
        String key,
        TiledMapReader.MapObject obj)
    {
        switch (obj.name) {
            case "town_chest1":
            case "town_chest2": {
                var chest = makeChest(key).cell(object2cell(obj));
                var vial = app.items().make("vial.healing")
                    .owner(chest.id());
                entities.add(vial);
                return true;
            }
            case "town_chest3": {
                var chest = makeChest(key).cell(object2cell(obj));
                var scroll = app.items().make("scroll.mapping")
                    .owner(chest.id());
                entities.add(scroll);
                return true;
            }
            default: return false;
        }
    }

}
