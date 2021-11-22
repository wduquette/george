package com.wjduquette.george.tiles;

import javafx.scene.image.Image;

import java.io.InputStream;

public class Effects {
    /** Bullet (arbitrary ranged missile) */
    public static final Image BULLET = file("Effects_001");

    /** Target (arbitrary ranged hit) */
    public static final Image TARGET = file("Effects_002");

    /** Fireball (ranged magic) */
    public static final Image FIREBALL = file("Effects_003");

    /** Jaws (biting attack) */
    public static final Image JAWS = file("Effects_004");

    /** Fist (Unarmed attack) */
    public static final Image FIST = file("Effects_005");

    /** Swoosh (Miss!) */
    public static final Image SWOOSH = file("Effects_006");

    /** Claw */
    public static final Image CLAW = file("Effects_007");

    /** Slime */
    public static final Image SLIME = file("Effects_008");

    /** Zzzz's */
    public static final Image ZZZ = file("Effects_009");

    // 010 - 015, undefined

    private static Image file(String filename) {
        InputStream istream =
            Effects.class.getResourceAsStream(filename + ".png");
        return new Image(istream);
    }
}
