package com.wjduquette.george.widgets;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

/**
 * A Pane containing a Canvas.  The Pane resizes the Canvas to fit
 * whenever it itself is resized.  Clients should redraw the canvas
 * in the {@code onResize} handler.
 */
public class CanvasPane extends Pane {
    //------------------------------------------------------------------------
    // Instance Variables

    private final Canvas canvas;
    private Runnable onResize = null;

    //------------------------------------------------------------------------
    // Constructor

    public CanvasPane() {
        // FIRST, create the components
        canvas = new Canvas();
        getChildren().add(canvas);
    }

    //-------------------------------------------------------------------------
    // Pane Behavior

    /**
     * Position and resize the canvas when the widget is resized.
     * Supports insets.
     */
    @Override
    protected void layoutChildren() {
        final int top = (int) snappedTopInset();
        final int right = (int) snappedRightInset();
        final int bottom = (int) snappedBottomInset();
        final int left = (int) snappedLeftInset();
        final int w = (int) getWidth() - left - right;
        final int h = (int) getHeight() - top - bottom;

        canvas.setLayoutX(left);
        canvas.setLayoutY(top);

        if (w != canvas.getWidth() || h != canvas.getHeight()) {
            canvas.setWidth(w);
            canvas.setHeight(h);
            if (onResize != null) {
                onResize.run();
            }
        }
    }

    //------------------------------------------------------------------------
    // Public Methods

    /**
     * Gets the pane's graphics context, for drawing.
     * @return The context
     */
    public GraphicsContext gc() {
        return canvas.getGraphicsContext2D();
    }

    /**
     * Gets the pane's onResize handler
     * @return The handler, or null if unset
     */
    public Runnable getOnResize() {
        return onResize;
    }

    /**
     * Sets the pane's onResize handler
     * @param handler The handler, or null to clear
     */
    public void setOnResize(Runnable handler) {
        this.onResize = handler;
    }

    /**
     * Clears the canvas, i.e., clearRect(0,0,w,h)
     */
    public void clear() {
        gc().clearRect(0, 0, getWidth(), getHeight());
    }

    /**
     * Draws an image on the canvas at the given location.
     * @param img The image
     * @param point The locatio
     */
    public void drawImage(Image img, Point2D point) {
        gc().drawImage(img, point.getX(), point.getY());
    }

    /**
     * Draws the text in the block using the given line spacing in pixels.  The
     * block is split into lines, and each line is drawn <i>spacing</i> pixels
     * below its predecessor.  Drawing uses the current text parameters.
     * @param text The text block
     * @param tx The x coordinate
     * @param ty The first y coordinate
     * @param spacing The spacing
     */
    public void fillTextBlock(
        String text,
        double tx,
        double ty,
        double spacing)
    {
        String[] lines = text.split("\n");

        for (var line : lines) {
            gc().fillText(line, tx, ty);
            ty += spacing;
        }
    }

    //-------------------------------------------------------------------------
    // Coordinate Conversion Helpers

    /**
     * Returns the bounds of the given node, converted to this pane's
     * coordinate system.  The node must be in the same scene.
     *
     * <p>The conversion converts the node's local bounds to scene bounds,
     * and then those scene bounds to this widget's local system.</p>
     * @param node The node
     * @return The converted bounds
     */
    public Bounds boundsOf(Node node) {
        Bounds sceneBounds = node.localToScene(node.getBoundsInLocal());
        return sceneToLocal(sceneBounds);
    }

    /**
     * Gets the mouse location in the local canvas coordinates.
     * @param evt The mouse event
     * @return The location
     */
    public Point2D ofMouse(MouseEvent evt) {
        return sceneToLocal(evt.getSceneX(), evt.getSceneY());
    }

}
