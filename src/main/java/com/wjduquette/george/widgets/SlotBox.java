package com.wjduquette.george.widgets;

import com.wjduquette.george.ecs.Entity;

import java.util.ArrayList;
import java.util.List;

/**
 * A representation of an inventory or equipment slot.
 */
public class SlotBox {
    //-------------------------------------------------------------------------
    // Instance Variables

    private final Entity owner;
    private final int index;
    private final String contentSprite;
    private final List<Action> actions = new ArrayList<>();

    //-------------------------------------------------------------------------
    // Constructor

    public SlotBox(Entity owner, int index, String contentSprite) {
        this.owner = owner;
        this.index = index;
        this.contentSprite = contentSprite;
    }

    //-------------------------------------------------------------------------
    // API

    public Entity       owner()         { return owner; }
    public int          index()         { return index; }
    public String       contentSprite() { return contentSprite; }
    public List<Action> actions()       { return actions; }
}
