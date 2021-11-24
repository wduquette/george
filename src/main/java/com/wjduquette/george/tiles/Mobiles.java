package com.wjduquette.george.tiles;

import com.wjduquette.george.graphics.ImageResource;
import javafx.scene.image.Image;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Mobiles {
    private static final Map<String, ImageResource> tiles = new LinkedHashMap<>();
    public static List<ImageResource> getTiles() {
        return tiles.values().stream().collect(Collectors.toList());
    }
    public static ImageResource getInfo(String name) {
        return tiles.get(name);
    }

    public static Image getImage(String name) {
        var info = tiles.get(name);
        return info != null ? info.image() : null;
    }

    // From mobile_human.png
    public static final Image GEORGE        = file("mobile_human_000", "george");
    public static final Image KNIGHT        = file("mobile_human_001", "knight");
    public static final Image FRIAR         = file("mobile_human_002", "friar");
    public static final Image WIZARD1       = file("mobile_human_003", "wizard1");
    public static final Image THIEF         = file("mobile_human_004", "thief");
    public static final Image DESK_WIZARD   = file("mobile_human_005", "desk_wizard");
    public static final Image FILLMORE      = file("mobile_human_006", "fillmore");
    public static final Image GOLD_FILLMORE = file("mobile_human_007", "gold_fillmore");
    public static final Image DARK_WIZARD   = file("mobile_human_008", "dark_wizard");
    public static final Image PEASANT1      = file("mobile_human_009", "peasant1");
    public static final Image PEASANT2      = file("mobile_human_010", "peasant2");
    public static final Image PEASANT3      = file("mobile_human_011", "peasant3");
    public static final Image PEASANT4      = file("mobile_human_012", "peasant4");
    public static final Image PEASANT5      = file("mobile_human_013", "peasant5");
    public static final Image PRINCESS      = file("mobile_human_014", "princess");
    public static final Image WIZARD2       = file("mobile_human_015", "wizard2");


    private static Image file(String filename, String name) {
        String resource = filename + ".png";
        try (InputStream istream = Mobiles.class.getResourceAsStream(resource)) {
            Image img = new Image(istream);
            tiles.put(name, new ImageResource(name, resource, img));
            return img;
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to load resource: " + filename);
        }
    }
}
