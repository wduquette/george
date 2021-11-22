package com.wjduquette.george.tiles;

import javafx.scene.image.Image;

import java.io.InputStream;

public class Buttons {
    // 000, undefined

    public static final Image BACKPACK = file("Buttons_001");
    public static final Image COMBAT = file("Buttons_002");
    public static final Image NORMAL = file("Buttons_003");
    public static final Image SCROLL = file("Buttons_004");
    public static final Image MAP = file("Buttons_005");
    public static final Image MAGNIFIER = file("Buttons_006");
    public static final Image LOAD = file("Buttons_007");
    public static final Image SAVE = file("Buttons_008");

    // 009 - 015, undefined

    private static Image file(String filename) {
        InputStream istream =
            Buttons.class.getResourceAsStream(filename + ".png");
        return new Image(istream);
    }
}
