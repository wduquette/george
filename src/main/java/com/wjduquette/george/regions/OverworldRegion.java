package com.wjduquette.george.regions;

import com.wjduquette.george.App;
import com.wjduquette.george.model.DataDrivenRegion;
import com.wjduquette.george.tmx.TiledMapReader;

public class OverworldRegion extends DataDrivenRegion {
    //-------------------------------------------------------------------------
    // Constructor

    public OverworldRegion(App app, Class<?> cls, String relPath) {
        super(app, cls, relPath);
    }

    //-------------------------------------------------------------------------
    // Customizations

    protected final boolean handleObject(
        String key,
        TiledMapReader.MapObject obj)
    {
        switch (obj.name) {
            case "tutor":
                var tutor = makeMannikin(key).cell(object2cell(obj));
                entities.add(tutor);
                // TODO: Add a tripwire.
                return true;
            default: return false;
        }
    }
}
