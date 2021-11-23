package com.wjduquette.george.graphics;

import javafx.scene.image.Image;

/**
 * A 40x40 image tile, as read from a resource file.
 */
public record Sprite(String name, String resource, Image image) {
}
