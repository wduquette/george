package com.wjduquette.george.regions;

import com.wjduquette.george.App;
import com.wjduquette.george.model.*;
import com.wjduquette.george.tmx.TiledMapReader;

public class BuglandRegion extends DataDrivenRegion {
    //-------------------------------------------------------------------------
    // Constructor

    public BuglandRegion(App app, Class<?> cls, String relPath) {
        super(app, cls, relPath);
    }

    //-------------------------------------------------------------------------
    // Customizations

    protected final boolean handleObject(
        String key,
        TiledMapReader.MapObject obj)
    {
        if (obj.name.equals("chest.supplies")) {
            var chest = makeChest(key).loc(object2cell(obj));
            chest.inventory().add(app.items().make("vial.healing"));
            chest.inventory().setGold(5);
            entities.add(chest);

            return true;
        } else if (obj.name.equals("chest.treasure")) {
            var chest = makeChest(key).loc(object2cell(obj));
            chest.inventory().add(app.items().make("weapon.staple_gun"));
            chest.inventory().setGold(30);
            entities.add(chest);

            return true;
        }
        return false;
    }
}
