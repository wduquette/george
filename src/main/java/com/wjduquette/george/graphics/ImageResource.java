package com.wjduquette.george.graphics;

import javafx.scene.image.Image;

/**
 * An image resource, with an internal name, a file name, and the actual
 * image.
 * @param name The image's internal name
 * @param resource The image's resource file name
 * @param image The actual image.
 */
public record ImageResource(String name, String resource, Image image) { }
