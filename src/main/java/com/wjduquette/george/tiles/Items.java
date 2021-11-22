package com.wjduquette.george.tiles;

import javafx.scene.image.Image;

import java.io.InputStream;

public class Items {
    public static final Image SANDALS = file("Items_000");
    public static final Image SHOES = file("Items_001");
    public static final Image BOOTS = file("Items_002");
    public static final Image CHAIN_BOOTS = file("Items_003");
    public static final Image PLATE_BOOTS = file("Items_004");
    public static final Image LORD_BOOTS = file("Items_005");
    public static final Image LEATHER_ARMOR = file("Items_006");
    public static final Image CHAIN_ARMOR = file("Items_007");
    public static final Image PLATE_ARMOR = file("Items_008");
    public static final Image LORD_ARMOR = file("Items_009");
    public static final Image LEATHER_HELMET = file("Items_010");
    public static final Image POT_HELMET = file("Items_011");
    public static final Image CHAIN_HELMET = file("Items_012");
    public static final Image PLATE_HELMET = file("Items_013");
    public static final Image LORD_HELMET = file("Items_014");
    public static final Image DAGGER = file("Items_015");
    public static final Image SHORT_SWORD = file("Items_016");
    public static final Image LONG_SWORD = file("Items_017");
    public static final Image PIZZA_PAN = file("Items_018");
    public static final Image BACKPACK = file("Items_019");
    public static final Image WOODEN_SHIELD = file("Items_020");
    public static final Image LEATHER_SHIELD = file("Items_021");
    public static final Image PLATE_SHIELD = file("Items_022");
    public static final Image LORD_SHIELD = file("Items_023");
    public static final Image BOW = file("Items_024");
    public static final Image FANCY_BOW = file("Items_025");
    public static final Image STAPLE_GUN = file("Items_026");
    public static final Image HAT = file("Items_027");
    public static final Image OVERALLS = file("Items_028");
    public static final Image PITCHFORK = file("Items_029");
    public static final Image MONK_HABIT = file("Items_030");
    public static final Image HOLY_BOOK = file("Items_031");
    public static final Image STAFF = file("Items_032");
    public static final Image HOLY_ORB = file("Items_033");
    public static final Image GOLD_KEY = file("Items_034");
    public static final Image TROPHY = file("Items_035");
    public static final Image WORD_SCROLL = file("Items_036");
    public static final Image MAP_SCROLL = file("Items_037");
    public static final Image RED_VIAL = file("Items_038");
    public static final Image RED_FLASK = file("Items_039");
    public static final Image BLUE_VIAL = file("Items_040");
    public static final Image BLUE_FLASK = file("Items_041");
    public static final Image GREEN_VIAL = file("Items_042");
    public static final Image GREEN_FLASK = file("Items_043");
    public static final Image GOLD_VIAL = file("Items_044");
    public static final Image GOLD_FLASK = file("Items_045");
    public static final Image PURPLE_VIAL = file("Items_046");
    public static final Image PURPLE_FLASK = file("Items_047");
    public static final Image SMALL_WRENCH = file("Items_048");
    public static final Image SKULL = file("Items_049");
    public static final Image GOLD_COIN = file("Items_050");
    public static final Image HANDKERCHIEF = file("Items_051");
    public static final Image SPITTOON = file("Items_052");

    // 053 - 055, undefined

    private static Image file(String filename) {
        InputStream istream =
            Items.class.getResourceAsStream(filename + ".png");
        return new Image(istream);
    }
}
