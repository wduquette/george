package com.wjduquette.george.model;

import com.wjduquette.george.App;
import com.wjduquette.george.ecs.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A base class for NPC dialogs.
 */
public abstract class AbstractDialog implements Dialog {
    //-------------------------------------------------------------------------
    // Instance Variables

    // The region
    private final Region region;

    // The entity
    private final Entity entity;

    // The Mannikin's info key
    private final String key;

    //-------------------------------------------------------------------------
    // Constructor

    /**
     * Creates a dialog for an entity.
     * @param region The region
     * @param entity The entity
     * @param key The entity's info key
     */
    public AbstractDialog(Region region, Entity entity, String key) {
        this.region = region;
        this.entity = entity;
        this.key = key;
    }

    //-------------------------------------------------------------------------
    // Protected API

    protected Region region() { return region; }
    protected Entity entity() { return entity; }
    protected String key() { return key; }

    //-------------------------------------------------------------------------
    // Dialog API

    /**
     * Gets the entity's name.  It defaults to "{key}.label".  Subclasses
     * can override.
     * @return The name
     */
    @Override
    public String getName() {
        return region.getInfo(key, "label");
    }

    /**
     * Gets the entity's description.  It defaults to
     * "{key}.description".  Subclasses can override.
     * @return The description
     */
    @Override
    public Optional<String> getDescription() {
        return Optional.of(region.getInfo(key, "description"));
    }

    /**
     * Gets the entity's foreground sprite name.  Subclasses can override.
     * @return The sprite name
     */
    @Override
    public String foregroundSprite() {
        return entity.sprite();
    }

    /**
     * Gets the entity's background terrain tile name, based on its location.
     * Subclasses can override.
     * @return The sprite name
     */
    @Override
    public String backgroundSprite() {
        return region.getTerrain(entity.cell()).name();
    }
}
