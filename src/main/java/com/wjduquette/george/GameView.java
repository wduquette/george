package com.wjduquette.george;

import com.wjduquette.george.ecs.*;
import com.wjduquette.george.graphics.SpriteSet;
import com.wjduquette.george.model.*;
import com.wjduquette.george.widgets.CanvasPane;
import com.wjduquette.george.widgets.UserInput;
import com.wjduquette.george.widgets.UserInputEvent;
import javafx.application.Platform;
import javafx.geometry.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.*;

public class GameView extends StackPane {
    //-------------------------------------------------------------------------
    // Statics

    private enum Button {
        MODE("button.normal"),
        POINTER("button.pointer"),
        MAGNIFIER("button.magnifier"),
        INVENTORY("button.backpack"),
        SCROLL("button.scroll"),
        MAP("button.map");

        private final String sprite;

        Button(String sprite) {
            this.sprite = sprite;
        }

        public String sprite() {
            return sprite;
        }
    }

    //-------------------------------------------------------------------------
    // Instance Variables

    // The canvas on which the map is drawn.
    private final CanvasPane canvas;

    private int rowOffset = 0;
    private int colOffset = 0;

    private int rowMin = 0;
    private int rowMax = 0;
    private int colMin = 0;
    private int colMax = 0;

    // The region currently being displayed
    private Region region = null;

    // The sprite set to use for rendering.  (This is used for entities only;
    // the base terrain is drawn using the tiles from the region's
    // TerrainTileSet.)
    private SpriteSet sprites = null;

    // The currently rendered click targets
    private final List<ClickTarget> targets = new ArrayList<>();

    private final Set<Button> selected = new HashSet<>();

    //-------------------------------------------------------------------------
    // Constructor

    public GameView() {
        // Configure the Canvas
        canvas = new CanvasPane();
        getChildren().add(canvas);
        canvas.setBackground(new Background(
            new BackgroundFill(Color.BLACK, null, null)));
        canvas.setOnResize(this::repaint);
        canvas.setOnMouseClicked(this::onMouseClick);
        canvas.setOnKeyPressed(this::onKeyPressed);

        // Configure the buttons
        selected.add(Button.POINTER);
    }

    //-------------------------------------------------------------------------
    // Event Handling

    // Convert mouse clicks into user input
    private void onMouseClick(MouseEvent evt) {
        Point2D mouse = canvas.ofMouse(evt);

        // FIRST, did they click a specific target?
        for (ClickTarget target : targets) {
            if (target.bounds().contains(mouse)) {
                target.action.run();
                return;
            }
        }

        // NEXT, did they click a cell?
        Cell cell = xy2rc(mouse);

        if (!region.contains(cell)) {
            return;
        }

        if (selected.contains(Button.POINTER)) {
            if (evt.getButton().equals(MouseButton.PRIMARY)) {
                fireInputEvent(new UserInput.MoveTo(cell));
            } else if (evt.getButton().equals(MouseButton.SECONDARY)) {
                fireInputEvent(new UserInput.InteractWith(cell));
            }
        } else if (selected.contains(Button.MAGNIFIER)) {
            region.log(region.describe(cell));
        }
    }

    // Convert keypresses into user input
    private void onKeyPressed(KeyEvent evt) {
        if (evt.getCode() == KeyCode.I) {
            App.println("GameView: " + evt);
            fireInputEvent(new UserInput.ShowInventory());
        } else if (evt.getCode() == KeyCode.F1) {
            fireInputEvent(new UserInput.ShowDebugger());
        }
    }

    //-------------------------------------------------------------------------
    // Public Methods

    /**
     * Gets the sprite set in use by the renderer.
     * @return The set.
     */
    public SpriteSet getSprites() {
        return sprites;
    }

    /**
     * Sets the sprite set to use for rendering.
     * @param set The set
     */
    public void setSprites(SpriteSet set) {
        this.sprites = set;
    }

    public void setRegion(Region map) {
        this.region = map;

        Platform.runLater(() -> {
            repaint();
        });
    }

    public Region getRegion() {
        return region;
    }

    public void repaint() {
        canvas.clear();
        targets.clear();

        // Don't recompute bounds if the player is executing a plan.
        // TODO: Not sure if this is want I want.  At the very least, I need
        // recompute if the player is outside the current bounds.
        Entity player = region.query(Player.class).findFirst().orElseThrow();

        if (player.find(Plan.class).isEmpty()) {
            computeBounds(player.cell());
        }

        // FIRST, render the terrain
        for (int r = rowMin; r < rowMax; r++) {
            for (int c = colMin; c < colMax; c++) {
                TerrainTile tile = region.getTerrain(r, c);
                canvas.drawImage(tile.image(), rc2xy(r, c));

                // TODO: for now, mark a cell "seen" if it has appeared in
                // the rendered area.
                region.markSeen(r,c);
            }
        }

        // NEXT, render the features
        for (Entity feature : region.query(Feature.class).toList()) {
            canvas.drawImage(img(feature.sprite()), entity2xy(feature));
        }

        // NEXT, render the mobiles on top
        for (Entity mobile : region.query(Mobile.class).toList()) {
            canvas.drawImage(img(mobile.sprite()), entity2xy(mobile));
        }

        // NEXT, render other visual effects that have their own tiles.
        for (Entity effect : region.query(VisualEffect.class, Sprite.class).toList()) {
            canvas.drawImage(img(effect.sprite()), entity2xy(effect));
        }

        // NEXT, render the controls.
        drawStatusBox(0, player);
        drawButtonBar();

        // NEXT, render log messages
        List<Entity> messages = region.query(LogMessage.class)
            .sorted(Entity::newestFirst)
            .toList();
        for (int i = 0; i < messages.size(); i++) {
            drawLogMessage(i, messages.get(i).logMessage().message());
        }

        // NEXT, request the keyboard focus
        canvas.requestFocus();
    }

    //-------------------------------------------------------------------------
    // Button Bar Display

    private static final double BAR_MARGIN = 10;

    private void drawButtonBar() {
        var w = canvas.getWidth();
        var h = canvas.getHeight();
        var bw = sprites.width();
        var bh = sprites.height();
        var border = 4;

        var nbuttons = Button.values().length;
        var barw = nbuttons*(bw + border) + border;
        var barh = bh + 2*border;

        var bary = h - (barh + BAR_MARGIN);
        var barx = (w - barw)/2.0;

        canvas.gc().setFill(Color.BLACK);
        canvas.gc().fillRect(barx, bary, barw, barh);

        for (Button btn : Button.values()) {
            var i = btn.ordinal();
            var bx = barx + border + i*(bw + border);
            var by = bary + border;

            var fill = selected.contains(btn) ? Color.LIGHTGRAY : Color.GRAY;

            canvas.gc().setFill(fill);
            canvas.gc().fillRect(bx, by, bw, bh);
            canvas.gc().drawImage(sprites.get(btn.sprite()), bx, by);

            var box = new BoundingBox(bx, by, bw, bh);
            targets.add(new ClickTarget(box, () -> buttonClick(btn)));
        }
    }

    //-------------------------------------------------------------------------
    // LogMessage Display

    private static final double LOG_MIN_Y = 20;
    private static final double LOG_X = 20;
    private static final double LOG_HEIGHT = 25;


    private void drawLogMessage(int index, String message) {
        double x = LOG_X;
        double y = canvas.getHeight() - (index*LOG_HEIGHT + LOG_MIN_Y);

        canvas.gc().setStroke(Color.BLACK);
        canvas.gc().setLineWidth(1);
        canvas.gc().setFill(Color.WHITE);
        canvas.gc().setFont(Font.font("Helvetica", LOG_HEIGHT));
        canvas.gc().strokeText(message, x, y);
        canvas.gc().fillText(message, x, y);
    }

    //-------------------------------------------------------------------------
    // Status Box display

    private void drawStatusBox(int index, Entity player) {
        double oborder = 2;
        double iborder = 1;
        double border = oborder + iborder;
        double hName = 12;
        double gap = 5;
        Image sprite = img(player.sprite());
        double boxHeight = sprite.getHeight() + 2 * border + hName;
        double boxWidth = sprite.getWidth() + 2 * border;

        double yTop = 10 + index * (boxHeight + gap);
        double xLeft = 10;

        var box = new BoundingBox(xLeft, yTop, boxWidth, boxHeight);
        var input = new UserInput.StatusBox(player.id());
        targets.add(new ClickTarget(box, () -> fireInputEvent(input)));

        canvas.gc().setFill(Color.BLACK);
        canvas.gc().fillRect(xLeft, yTop, boxWidth, boxHeight);

        canvas.gc().setFill(Color.WHITE);
        canvas.gc().fillRect(
            xLeft + oborder, yTop + oborder,
            boxWidth - 2 * oborder, boxHeight - 2 * oborder);

        canvas.gc().setFill(Color.CYAN);
        canvas.gc().fillRect(
            xLeft + border, yTop + border,
            boxWidth - 2 * border, boxHeight - 2 * border);

        canvas.gc().drawImage(sprite, xLeft + border, yTop + border);
    }


    //-------------------------------------------------------------------------
    // Utilities

    private void drawRoute(List<Cell> route) {
        canvas.gc().setStroke(Color.WHITE);
        canvas.gc().setLineWidth(3);

        for (int i = 1; i < route.size(); i++) {
            Point2D a = cell2centerxy(route.get(i - 1));
            Point2D b = cell2centerxy(route.get(i));
            canvas.gc().strokeLine(a.getX(), a.getY(), b.getX(), b.getY());
        }
    }

    // Compute the row and column offsets so that the given cell is in the
    // middle of the view pane
    private void computeBounds(Cell cell) {
        double heightInTiles = canvas.getHeight() / region.getTileHeight();
        double widthInTiles = canvas.getWidth() / region.getTileWidth();
        rowOffset = cell.row() - (int) heightInTiles / 2;
        colOffset = cell.col() - (int) widthInTiles / 2;

        rowMax = Math.min(region.getHeight(), rowOffset + (int) heightInTiles + 1);
        colMax = Math.min(region.getHeight(), colOffset + (int) widthInTiles + 1);

        rowMin = Math.max(0, rowOffset);
        colMin = Math.max(0, colOffset);
    }

    private Image img(Sprite sprite) {
        return sprites.get(sprite.name());
    }

    // Gets the pixel coordinates at which to draw the entity's tile.
    private Point2D entity2xy(Entity entity) {
        return loc2xy(entity.loc());
    }

    // Convert cell coordinates to the pixel coordinates at which it will
    // be drawn, taking the cell's row and column offset into account.  If
    // the offsets are 0,0, the result will be the pixel coordinates of
    // the upper-left corner of the cell
    private Point2D cell2xy(Cell cell) {
        return rc2xy(cell.row(), cell.col());
    }

    private Point2D loc2xy(Loc loc) {
        return rc2xy(
            loc.cell().row() + loc.rowOffset(),
            loc.cell().col() + loc.colOffset());
    }

    // Convert cell coordinates to the pixel coordinates of the center of the
    // cell.
    private Point2D cell2centerxy(Cell cell) {
        Point2D p = rc2xy(cell.row(), cell.col());
        return new Point2D(
            p.getX() + (double) region.getTileWidth()/2,
            p.getY() + (double) region.getTileHeight()/2);
    }

    private Point2D rc2xy(double row, double col) {
        double x = (col - colOffset) * region.getTileWidth();
        double y = (row - rowOffset) * region.getTileHeight();
        return new Point2D(x,y);
    }

    // Converts a point in pixel coordinates to a logical cell.
    private Cell xy2rc(Point2D pt) {
        int c = (int)(pt.getX() / region.getTileWidth()) + colOffset;
        int r = (int)(pt.getY() / region.getTileHeight()) + rowOffset;

        return new Cell(r,c);
    }

    //-------------------------------------------------------------------------
    // ClickTarget: canned bounds on which the user can click, plus actions.

    // If the user clicks in the bounds, the user input is sent.
    private record ClickTarget(Bounds bounds, Runnable action) {}

    private void fireInputEvent(UserInput input) {
        fireEvent(new UserInputEvent(input));
    }

    private void buttonClick(Button btn) {
        switch (btn) {
            case POINTER -> {
                selected.remove(Button.MAGNIFIER);
                selected.add(Button.POINTER);
            }
            case MAGNIFIER -> {
                selected.remove(Button.POINTER);
                selected.add(Button.MAGNIFIER);
            }
            case INVENTORY -> fireInputEvent(new UserInput.ShowInventory());
            case MAP -> fireInputEvent(new UserInput.ShowMap());
            default -> region.log("TODO: " + btn);
        }
    }
}
