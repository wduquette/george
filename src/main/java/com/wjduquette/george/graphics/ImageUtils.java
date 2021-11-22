package com.wjduquette.george.graphics;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;

import java.util.ArrayList;
import java.util.List;

/**
 * Tools for working with PNG image files.
 */
public class ImageUtils {
    private ImageUtils() {} // Not instantiable

    /**
     * Breaks a PNG image into a set of size*size tiles
     * @param sourceImage The source image.
     * @param size The tile size in pixels
     * @return A list of the loaded pixels.
     */
    public static List<Image> getTiles(Image sourceImage, int size) {
        List<Image> list = new ArrayList<>();

        System.out.println("sourceImage = " + sourceImage.getWidth() + "x" +
            sourceImage.getHeight());

        PixelReader reader = sourceImage.getPixelReader();

        for (int j = 0; j < sourceImage.getHeight(); j += size) {
            for (int i = 0; i < sourceImage.getWidth(); i += size) {
                WritableImage tile =
                    new WritableImage(reader, i, j, size, size);
                list.add(tile);
            }
        }

        return list;
    }
}
