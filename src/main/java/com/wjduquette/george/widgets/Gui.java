package com.wjduquette.george.widgets;


import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class Gui {
    private Gui() {} // Not instantiable

    /**
     * Gets the width in pixels of the text string in the given font.
     * @param font The font
     * @param text The text string.
     * @return the width in pixels;
     */
    public static double pixelWidth(Font font, String text) {
        var shape = new Text(text);
        shape.setFont(font);
        return shape.getLayoutBounds().getWidth();
    }
}
