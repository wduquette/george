package com.wjduquette.george.tiles;

import javafx.scene.image.Image;

import java.io.InputStream;

public class Slots {
    public static final Image WEAPON_SLOT = file ("Slots_000");
    public static final Image BOW_SLOT = file ("Slots_001");
    public static final Image ARMOR_SLOT = file ("Slots_002");
    public static final Image SHIELD_SLOT = file ("Slots_003");
    public static final Image HELMET_SLOT = file ("Slots_004");
    public static final Image FOOTWEAR_SLOT = file ("Slots_005");
    public static final Image ITEM_SLOT = file ("Slots_006");

    // 007 undefined

    private static Image file(String filename) {
        InputStream istream =
            Slots.class.getResourceAsStream(filename + ".png");
        return new Image(istream);
    }
}
