package com.wjduquette.george.graphics;

import javafx.scene.image.Image;

/**
 * Information about an image used in the game.
 */
public interface ImageInfo {
    /**
     * Gets the actual image.
     * @return The image
     */
    Image image();

    /**
     * Gets the name by which the image is known internally.
     * @return The name
     */
    String name();

    /**
     * Gets the image's height.
     * @return The height in pixels
     */
    default double height() { return image().getHeight(); }

    /**
     * Gets the image's width
     * @return The width in pixels
     */
    default double width() { return image().getWidth(); }
}
