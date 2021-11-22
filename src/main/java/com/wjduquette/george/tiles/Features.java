package com.wjduquette.george.tiles;

import javafx.scene.image.Image;

import java.io.InputStream;

public class Features {
    public static final Image BLANK = file("Features_000");
    public static final Image CLOSED_DOOR = file("Features_001");
    public static final Image OPEN_DOOR = file("Features_002");
    public static final Image STAIRS_DOWN = file("Features_003");
    public static final Image STAIRS_UP = file("Features_004");
    public static final Image CHEST = file("Features_005");
    public static final Image OPEN_CHEST = file("Features_006");
    public static final Image LADDER_DOWN = file("Features_007");
    public static final Image LADDER_UP = file("Features_008");
    public static final Image PORTAL_ACTIVE = file("Features_009");
    public static final Image PORTAL_INACTIVE = file("Features_010");
    public static final Image SIGN = file("Features_011");
    public static final Image SPIKES = file("Features_012");
    public static final Image PEDESTAL = file("Features_013");
    public static final Image SAVE_PEDESTAL = file("Features_014");
    public static final Image BROKEN_ORB = file("Features_015");
    public static final Image YELLOW_ORB = file("Features_016");
    public static final Image BLUE_ORB = file("Features_017");
    public static final Image GREEN_ORB = file("Features_018");
    public static final Image RED_ORB = file("Features_019");
    public static final Image TALL_PEDESTAL = file("Features_020");
    public static final Image HEART_PEDESTAL = file("Features_021");

    // 022 - 023, undefined

    private static Image file(String filename) {
        InputStream istream =
            Features.class.getResourceAsStream(filename + ".png");
        return new Image(istream);
    }
}
