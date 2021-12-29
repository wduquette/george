package com.wjduquette.george.widgets;

import com.wjduquette.george.App;
import com.wjduquette.george.ecs.Entity;
import com.wjduquette.george.ecs.Sprite;
import com.wjduquette.george.graphics.SpriteSet;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.ArrayList;
import java.util.List;

/**
 * A canvas pane extended with tools related to the game and its data.
 * This is used for the main game view and a variety of other panels.
 */
public abstract class GamePane extends StackPane {
    //-------------------------------------------------------------------------
    // Instance Variables

    // The application
    private final App app;

    // The canvas pane, for content.
    private final CanvasPane canvas;

    // The list of things the user can click on.
    private final List<ClickTarget> targets = new ArrayList<>();

    //-------------------------------------------------------------------------
    // Constructor

    /**
     * Creates the new pane.
     * @param app The application object.
     */
    public GamePane(App app) {
        this.app = app;
        this.canvas = new CanvasPane();

        canvas.setOnResize(this::repaint);
        canvas.setOnMouseClicked(this::handleMouseClick);
        canvas.setOnKeyPressed(this::onKeyPress);
        getChildren().add(canvas);
    }

    //-------------------------------------------------------------------------
    // Event Handlers

    // Handles all mouse clicks.  Click targets are handled directly; other
    // clicks are passed on to the subclass.
    private void handleMouseClick(MouseEvent evt) {
        Point2D mouse = canvas.toPoint(evt);

        // FIRST, did they click a specific target?
        for (ClickTarget target : targets) {
            if (target.bounds().contains(mouse)) {
                target.action.run();
                return;
            }
        }

        // NEXT, give the subclass a chance.
        onMouseClick(evt);
    }

    //-------------------------------------------------------------------------
    // Protected API

    // Components
    protected App             app()     { return app; }
    protected CanvasPane      canvas()  { return canvas; }
    protected GraphicsContext gc()      { return canvas.gc(); }
    protected SpriteSet       sprites() { return app.sprites(); }

    /**
     * Adds a click target to the pane.
     * @param target The target
     */
    protected void addTarget(ClickTarget target) {
        targets.add(target);
    }

    /**
     * Adds a click target to the pane.
     * @param bounds The bounding box
     * @param action The action to take on click
     */
    protected void addTarget(Bounds bounds, Runnable action) {
        targets.add(new ClickTarget(bounds, action));
    }

    /**
     * Forget all defined click targets.
     */
    protected void clearTargets() {
        targets.clear();
    }

    /**
     * Get a sprite image given its name.
     * @param name The sprite name
     * @return The image
     */
    protected Image toImage(String name) {
        return sprites().get(name);
    }

    /**
     * Get a sprite image given a Sprite component.
     * @param sprite The Sprite component
     * @return The image
     */
    protected Image toImage(Sprite sprite) {
        return sprites().get(sprite.name());
    }

    /**
     * Get a sprite image given an entity, from its Sprite component.
     * @param entity The entity
     * @return The image
     */
    protected Image toImage(Entity entity) {
        return sprites().get(entity.sprite().name());
    }

    //-------------------------------------------------------------------------
    // Drawing API

    protected void drawImage(Image image, double x, double y) {
        gc().drawImage(image, x, y);
    }

    protected void drawImage(Image image, Point2D point) {
        gc().drawImage(image, point.getX(), point.getY());
    }

    protected void fill(Paint paint, double x, double y, double width, double height) {
        gc().setFill(paint);
        gc().fillRect(x, y, width, height);
    }

    protected void fill(Paint paint, Bounds box) {
        gc().setFill(paint);
        gc().fillRect(box.getMinX(), box.getMinY(), box.getWidth(), box.getHeight());
    }

    //-------------------------------------------------------------------------
    // Framework Methods

    /**
     * Subclasses must override to paint content content.
     */
    abstract protected void onRepaint();

    /**
     * Subclasses may override to receive mouse clicks (that were not
     * handled by defined click targets).
     * @param evt The event
     */
    protected void onMouseClick(MouseEvent evt) {}

    /**
     * Subclasses may override to receive key events.
     * @param evt The event.
     */
    protected void onKeyPress(KeyEvent evt) {}

    //-------------------------------------------------------------------------
    // Public API

    public void repaint() {
        // FIRST, clear the old content
        canvas.clear();
        targets.clear();

        // NEXT, repaint the content
        onRepaint();

        // NEXT, request the keyboard focus.
        canvas.requestFocus();
    }

    public Point2D toPoint(MouseEvent evt) {
        return canvas.toPoint(evt);
    }

    //-------------------------------------------------------------------------
    // Helper Classes

    /**
     * If the user clicks in the bounds of this target, the action is called.
     * @param bounds A bounding box
     * @param action The action to do.
     */
    public record ClickTarget(Bounds bounds, Runnable action) {}
}
