package com.wjduquette.george.graphics;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

import java.util.ArrayList;
import java.util.List;

/**
 * Tools for working with PNG image files.
 */
public class ImageUtils {
    private ImageUtils() {} // Not instantiable

    /**
     * Breaks a PNG image into a set of width*height tiles, reading from
     * right to left and top to bottom.
     * @param sourceImage The source image.
     * @param width The tile width in pixels
     * @param height The tile height in pixels
     * @return A list of the loaded tiles.
     */
    public static List<Image> getTiles(Image sourceImage, int width, int height) {
        List<Image> list = new ArrayList<>();

        PixelReader reader = sourceImage.getPixelReader();

        for (int j = 0; j < sourceImage.getHeight(); j += height) {
            for (int i = 0; i < sourceImage.getWidth(); i += width) {
                list.add(new WritableImage(reader, i, j, width, height));
            }
        }

        return list;
    }

    /**
     * Resizes the image to be factor times bigger, retaining pixelation.
     * @param source The source image
     * @param factor The factor, >= 1
     * @return The new image
     */
    public static Image embiggen(Image source, int factor) {
        PixelReader reader = source.getPixelReader();
        int w = (int)source.getWidth();
        int h = (int)source.getHeight();
        WritableImage result = new WritableImage(w*factor, h*factor);
        PixelWriter writer = result.getPixelWriter();

        for (int i = 0; i < w*factor; i++) {
            for (int j = 0; j < h*factor; j++) {
                int si = i / factor;
                int sj = j / factor;
                writer.setColor(i,j, reader.getColor(si,sj));
            }
        }

        return result;
    }
}
