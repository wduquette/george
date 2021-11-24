package com.wjduquette.george.tiles;

import javafx.scene.image.Image;

import java.io.InputStream;

/**
 * A class of terrain tiles.
 */
public class Terrains {
    public static final Image UNKNOWN = file("terrain_000");
    public static final Image TILE_FLOOR = file("terrain_001");
    public static final Image BLOCK_WALL = file("terrain_002");
    public static final Image COBBLE_FLOOR = file("terrain_003");
    public static final Image STONE_WALL = file("terrain_004");
    public static final Image WATER = file("terrain_005");
    public static final Image EARTH = file("terrain_006");
    public static final Image GRASS = file("terrain_007");
    public static final Image SAND = file("terrain_008");
    public static final Image GRAVEL = file("terrain_009");
    public static final Image MOUNTAIN = file("terrain_010");
    public static final Image CAVE = file("terrain_011");
    public static final Image WOOD_FLOOR_NS = file("terrain_012");
    public static final Image WOOD_FLOOR_EW = file("terrain_013");
    public static final Image HBRIDGE = file("terrain_014");
    public static final Image HBRIDGE_NORTH = file("terrain_015");
    public static final Image FOREST = file("terrain_016");
    public static final Image BRICK_SPIRAL = file("terrain_017");
    public static final Image BRICK_NS = file("terrain_018");
    public static final Image BRICK_EW = file("terrain_019");
    public static final Image VBRIDGE_WEST = file("terrain_020");
    public static final Image VBRIDGE_EAST = file("terrain_021");
    public static final Image VBRIDGE = file("terrain_022");
    public static final Image HBRIDGE_SOUTH = file("terrain_023");

    private static Image file(String filename) {
        InputStream istream =
            Terrains.class.getResourceAsStream(filename + ".png");
        return new Image(istream);
    }
}
