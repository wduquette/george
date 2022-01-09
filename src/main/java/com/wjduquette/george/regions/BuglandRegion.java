package com.wjduquette.george.regions;

import com.wjduquette.george.App;
import com.wjduquette.george.ecs.Entity;
import com.wjduquette.george.model.*;
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
        return false;
    }
}
