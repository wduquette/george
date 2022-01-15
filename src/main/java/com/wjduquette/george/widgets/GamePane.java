package com.wjduquette.george.widgets;

import com.wjduquette.george.App;
import com.wjduquette.george.ecs.Entity;
import com.wjduquette.george.ecs.Sprite;
import com.wjduquette.george.ecs.LiveImage;
import com.wjduquette.george.graphics.ImageUtils;
import com.wjduquette.george.graphics.SpriteSet;
import com.wjduquette.george.model.ItemSlot;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * A canvas pane extended with tools related to the game and its data.
 * This is used for the main game view and a variety of other panels.
 */
public abstract class GamePane extends StackPane {
    /** The title font. */
    public static final Font TITLE_FONT = Font.font("Helvetica", 24);

    /** The normal text font. */
    public static final Font NORMAL_FONT = Font.font("Helvetica", 16);

    /** The small text font. */
    public static final Font SMALL_FONT = Font.font("Helvetica", 12);

    /** The leading we use with the TITLE_FONT. */
    public static final double TITLE_LEADING = 30;

    /** The leading we use with the NORMAL_FONT. */
    public static final double NORMAL_LEADING = 20;


    //-------------------------------------------------------------------------
    // Instance Variables

    // The application
    private final App app;

    // The canvas pane, for content.
    private final CanvasPane canvas;

    // The list of child widgets the subclass has defined.
    private final List<Node> widgets = new ArrayList<>();

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
    protected void target(ClickTarget target) {
        targets.add(target);
    }

    /**
     * Adds a click target to the pane.
     * @param bounds The bounding box
     * @param action The action to take on click
     */
    protected void target(Bounds bounds, Runnable action) {
        targets.add(new ClickTarget(bounds, action));
    }

    /**
     * Adds the Text shape to the canvas at the given location.  It will be
     * cleaned up prior to the next repaint.  This is the easiest way to
     * add a text target.
     * @param widget The text shape
     * @param x The X position in pixels
     * @param y The Y position in pixels
     */
    protected void place(Text widget, double x, double y) {
        widget.setX(x);
        widget.setY(y);
        widgets.add(widget);
        canvas.getChildren().add(widget);
    }

    /**
     * Adds the Text shape to the canvas at the given location.  It will be
     * cleaned up prior to the next repaint.  This is the easiest way to
     * add a text target.
     * @param widget The text shape
     * @param x The X position in pixels
     * @param y The Y position in pixels
     * @param action An action to take when the widget is clicked.
     */
    protected void place(Text widget, double x, double y, Runnable action) {
        widget.setOnMouseClicked(evt -> action.run());
        place(widget, x, y);
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
     * Gets the entity's sprite image.  The image will come from the entity's
     * Visual, if any, and from its Sprite component otherwise.
     * @param entity The entity
     * @return The image
     */
    protected Image toImage(Entity entity) {
        return entity.has(LiveImage.class)
            ? entity.liveImage().image()
            : sprites().get(entity.sprite());
    }

    //-------------------------------------------------------------------------
    // Drawing API

    protected void drawImage(Image image, double x, double y) {
        gc().drawImage(image, x, y);
    }

    protected void drawImage(Image image, Point2D point) {
        gc().drawImage(image, point.getX(), point.getY());
    }

    /**
     * Draws the entity as shown in dialogs, etc.  The entity and its
     * background terrain are drawn double-sized at the given location,
     * with a white border.  The upper left corner of the entity's sprite
     * is at x,y.
     * @param entity The entity
     * @param x The left x coordinate, in pixels
     * @param y The top y coordinate, in pixels
     */
    protected void drawFramedEntity(Entity entity, double x, double y, int factor) {
        var terrain = entity.loc() != null
            ? app().getCurrentRegion().getTerrain(entity.loc()).image()
            : null;

        drawFramedSprites(toImage(entity), terrain, x, y, factor);
    }

    /**
     * Draws the pair of images at the given factor size, surrounded by a
     * white border.  If the background image is null, uses a cyan background.
     * @param fgImage The foreground image
     * @param bgImage The background image, or null
     * @param x The left x coordinate, in pixels
     * @param y The top y coordinate, in pixels
     */
    protected void drawFramedSprites(
        Image fgImage,
        Image bgImage,
        double x,
        double y,
        int factor)
    {
        var border = 2;
        var ix = x + border;
        var iy = y + border;
        var iw = factor * fgImage.getWidth();
        var ih = factor * fgImage.getHeight();
        var w = iw + 2*border;
        var h = ih + 2*border;

        fill(Color.WHITE, x, y, w, h);

        if (bgImage != null) {
            drawImage(ImageUtils.embiggen(bgImage, factor), ix, iy);
        } else {
            fill(Color.CYAN, ix, iy, iw, ih);
        }

        drawImage(ImageUtils.embiggen(fgImage, factor), ix, iy);
    }

    protected void fill(Paint paint, double x, double y, double width, double height) {
        gc().setFill(paint);
        gc().fillRect(x, y, width, height);
    }

    protected void fill(Paint paint, Bounds box) {
        gc().setFill(paint);
        gc().fillRect(box.getMinX(), box.getMinY(), box.getWidth(), box.getHeight());
    }

    protected void fillTextBlock(String block, double x, double y, double spacing) {
        // TODO: Move fill text block here.
        canvas.fillTextBlock(block, x, y, spacing);
    }

    /**
     * Draws an array of slot boxes.  cols*rows must equal the length of the
     * slots list.
     * @param x The left x coordinate of the array
     * @param y The top y coordinate of the array
     * @param cols The number of columns
     * @param selectedSlot The selected slot
     * @param boxes The slots to draw.
     * @param onSelect Handler to call when the box is selected.
     */
    protected void drawSlots(
        double x,
        double y,
        int cols,
        List<SlotBox> boxes,
        ItemSlot selectedSlot,
        Consumer<SlotBox> onSelect
    ) {
        var border = 2;
        var sw = sprites().width() + border;
        var sh = sprites().height() + border;
        var rows = boxes.size()/cols;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                var index = r*cols + c;
                var sx = x + c*sw;
                var sy = y + r*sh;
                var box = boxes.get(index);
                var bounds = new BoundingBox(sx, sy, sw + border, sh + border);

                // Draw frame
                var bg = box.slot().equals(selectedSlot) ? Color.LIGHTGRAY : Color.DIMGRAY;
                fill(Color.WHITE, bounds);
                fill(bg, sx + border, sy + border, sw - border, sh - border);

                if (box.item() != null) {
                    drawImage(sprites().get(box.item().sprite()),
                        sx + border, sy + border);
                }

                if (box.count() > 1) {
                    var count = Integer.toString(box.count());
                    var tw = Gui.pixelWidth(SMALL_FONT, count);
                    var tx = sx + sw - 4 - tw;
                    var ty = sy + sh - 4;

                    gc().setFill(Color.WHITE);
                    gc().setFont(SMALL_FONT);
                    gc().setTextBaseline(VPos.BOTTOM);
                    gc().fillText(count, tx, ty);
                }

                target(bounds, () -> onSelect.accept(box));
            }
        }
    }

    //-------------------------------------------------------------------------
    // Public API

    public void repaint() {
        // FIRST, clear the old content
        canvas.clear();
        canvas.getChildren().removeAll(widgets);
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
