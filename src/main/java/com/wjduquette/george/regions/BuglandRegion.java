package com.wjduquette.george.regions;

import com.wjduquette.george.App;
import com.wjduquette.george.ecs.Entity;
import com.wjduquette.george.model.AbstractDialog;
import com.wjduquette.george.model.DataDrivenRegion;
import com.wjduquette.george.model.Dialog;
import com.wjduquette.george.tmx.TiledMapReader;

import java.util.*;

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
//        switch (obj.name) {
//            case "fillip": {
//                var fillip = makeMannikin(key).cell(object2cell(obj));
//                entities.add(fillip);
//                return true;
//            }
//            case "town_chest1":
//            case "town_chest2": {
//                var chest = makeChest(key).cell(object2cell(obj));
//                chest.inventory().add(app.items().make("vial.healing"));
//                entities.add(chest);
//                return true;
//            }
//            case "town_chest3": {
//                var chest = makeChest(key).cell(object2cell(obj));
//                chest.inventory().add(app.items().make("scroll.mapping"));
//                entities.add(chest);
//                return true;
//            }
//            default: return false;
//        }
        return false;
    }
}
