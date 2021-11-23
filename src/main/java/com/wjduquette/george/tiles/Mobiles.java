package com.wjduquette.george.tiles;

import com.wjduquette.george.graphics.Sprite;
import javafx.scene.image.Image;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Mobiles {
    private static final Map<String, Sprite> sprites = new LinkedHashMap<>();
    public static List<Sprite> getSprites() {
        return sprites.values().stream().collect(Collectors.toList());
    }
    public static Sprite getSprite(String name) {
        return sprites.get(name);
    }

    // From mobile_human.png
    public static final Image GEORGE =
        sprite("george", "mobile_human_000");
    public static final Image KNIGHT = sprite("knight", "mobile_human_001");
    public static final Image FRIAR = sprite("friar", "mobile_human_002");
    public static final Image WIZARD1 = sprite("wizard1", "mobile_human_003");
    public static final Image THIEF = sprite("thief", "mobile_human_004");
    public static final Image DESK_WIZARD = sprite("desk_wizard", "mobile_human_005");
    public static final Image FILLMORE = sprite("fillmore", "mobile_human_006");
    public static final Image GOLD_FILLMORE = sprite("gold_fillmore", "mobile_human_007");
    public static final Image DARK_WIZARD = sprite("dark_wizard", "mobile_human_008");
    public static final Image PEASANT1 = sprite("peasant1", "mobile_human_009");
    public static final Image PEASANT2 = sprite("peasant2", "mobile_human_010");
    public static final Image PEASANT3 = sprite("peasant3", "mobile_human_011");
    public static final Image PEASANT4 = sprite("peasant4", "mobile_human_012");
    public static final Image PEASANT5 = sprite("peasant5", "mobile_human_013");
    public static final Image PRINCESS = sprite("princess", "mobile_human_014");
    public static final Image WIZARD2 = sprite("wizard2", "mobile_human_015");


    private static Image sprite(String name, String filename) {
        String resource = filename + ".png";
        try (InputStream istream = Mobiles.class.getResourceAsStream(resource)) {
            Image img = new Image(istream);
            sprites.put(name, new Sprite(name, resource, img));
            return img;
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to load resource: " + filename);
        }
    }
}
