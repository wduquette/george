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

    // From mobile_animal
    public static final Image GRAY_SNAKE    = file("mobile_animal_000", "gray_snake");
    public static final Image ORANGE_SNAKE  = file("mobile_animal_001", "orange_snake");
    public static final Image RED_SNAKE     = file("mobile_animal_002", "red_snake");
    public static final Image GREEN_SNAKE   = file("mobile_animal_003", "green_snake");
    public static final Image BLUE_SNAKE    = file("mobile_animal_004", "blue_snake");
    public static final Image PURPLE_SNAKE  = file("mobile_animal_005", "purple_snake");
    public static final Image XMAS_SNAKE    = file("mobile_animal_006", "xmas_snake");
    public static final Image SPOTTED_SNAKE = file("mobile_animal_007", "spotted_snake");
    public static final Image BAT           = file("mobile_animal_008", "bat");
    public static final Image BLACK_BAT     = file("mobile_animal_009", "black_bat");
    public static final Image FIRE_BAT      = file("mobile_animal_010", "fire_bat");
    public static final Image ICE_BAT       = file("mobile_animal_011", "ice_bat");
    public static final Image GREEN_BAT     = file("mobile_animal_012", "green_bat");
    public static final Image PURPLE_BAT    = file("mobile_animal_013", "purple_bat");
    public static final Image ELECTRIC_BAT  = file("mobile_animal_014", "electric_bat");
    public static final Image JET_BAT       = file("mobile_animal_015", "jet_bat");
    public static final Image RAT           = file("mobile_animal_016", "rat");
    public static final Image GIANT_RAT     = file("mobile_animal_017", "giant_rat");
    public static final Image NEWT          = file("mobile_animal_018", "newt");
    // Unused: 019-023

    // From mobile_demon
    public static final Image IMP         = file("mobile_demon_000", "imp");
    public static final Image IMP_WIZARD  = file("mobile_demon_001", "imp_wizard");
    public static final Image IMP_FIGHTER = file("mobile_demon_002", "imp_fighter");
    public static final Image IMP_LORD    = file("mobile_demon_003", "imp_lord");
    public static final Image FIRE_DEMON  = file("mobile_demon_004", "fire_demon");
    public static final Image CTHULHU     = file("mobile_demon_005", "cthulhu");
    // Unused: 006-007

    // From mobile_fairy
    public static final Image BLUE_FAIRY   = file("mobile_fairy_000", "blue_fairy");
    public static final Image ORANGE_FAIRY = file("mobile_fairy_001", "orange_fairy");
    public static final Image GREEN_FAIRY  = file("mobile_fairy_002", "green_fairy");
    // Unused: 003-007

    // From mobile_humanoid
    public static final Image KOBOLD          = file("mobile_humanoid_000", "kobold");
    public static final Image GOBLIN          = file("mobile_humanoid_001", "goblin");
    public static final Image ORC             = file("mobile_humanoid_002", "orc");
    public static final Image OGRE            = file("mobile_humanoid_003", "ogre");
    public static final Image TROLL           = file("mobile_humanoid_004", "troll");
    public static final Image FIRE_CULTIST    = file("mobile_humanoid_005", "fire_cultist");
    public static final Image FIRE_PRIEST     = file("mobile_humanoid_006", "fire_priest");
    public static final Image CTHULHU_CULTIST = file("mobile_humanoid_007", "cthulhu_cultist");
    public static final Image CTHULHU_PRIEST  = file("mobile_humanoid_008", "cthulhu_priest");
    // Unused: 009-015

    // From mobile_insect
    public static final Image ROACH =     file("mobile_insect_000", "roach");
    public static final Image LADY_BUG =  file("mobile_insect_001", "lady_bug");
    public static final Image MANLY_BUG = file("mobile_insect_002", "manly_bug");
    public static final Image GOLD_BUG =  file("mobile_insect_003", "gold_bug");
    public static final Image SPIDER =    file("mobile_insect_004", "spider");
    // Unused 005-015

    // From mobile_robot
    public static final Image PEASANT_ROBOT = file("mobile_robot_000", "peasant_robot");
    public static final Image ROBOT         = file("mobile_robot_001", "robot");
    public static final Image WAR_ROBOT     = file("mobile_robot_002", "war_robot");
    // Unused: 003-007

    // From mobile_slime
    public static final Image WHITE_SLIME  = file("mobile_slime_000", "white_slime");
    public static final Image YELLOW_SLIME = file("mobile_slime_001", "yellow_slime");
    public static final Image BLUE_SLIME   = file("mobile_slime_002", "blue_slime");
    public static final Image RED_SLIME    = file("mobile_slime_003", "red_slime");
    public static final Image ORANGE_SLIME = file("mobile_slime_004", "orange_slime");
    public static final Image PURPLE_SLIME = file("mobile_slime_005", "purple_slime");
    public static final Image GREEN_SLIME  = file("mobile_slime_006", "green_slime");
    public static final Image METAL_SLIME  = file("mobile_slime_007", "metal_slime");

    // From mobile_undead
    public static final Image ZOMBIE   = file("mobile_undead_000", "zombie");
    public static final Image SKELETON = file("mobile_undead_001", "skeleton");
    public static final Image REAPER   = file("mobile_undead_002", "reaper");

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
