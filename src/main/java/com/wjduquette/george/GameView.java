package com.wjduquette.george;

import com.wjduquette.george.ecs.*;
import com.wjduquette.george.graphics.ImageUtils;
import com.wjduquette.george.graphics.SpriteSet;
import com.wjduquette.george.model.*;
import com.wjduquette.george.util.RandomPlus;
import com.wjduquette.george.widgets.CanvasPane;
import com.wjduquette.george.widgets.UserInput;
import com.wjduquette.george.widgets.UserInputEvent;
import javafx.application.Platform;
import javafx.geometry.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.*;

public class GameView extends StackPane {
    //-------------------------------------------------------------------------
    // Statics

    private enum Button {
        MODE("button.normal"),
        POINTER("button.pointer"),
        MAGNIFIER("button.magnifier"),
        SCROLL("button.scroll"),
        MAP("button.map");

        private String sprite;

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

    // TODO: Should be defined on App as a global resource.
    private final RandomPlus random = new RandomPlus();

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
            var input = new UserInput.CellClick(cell);
            fireEvent(new UserInputEvent(input));
        } else if (selected.contains(Button.MAGNIFIER)) {
            region.log(region.describe(cell));
        }
    }

    // Convert keypresses into user input
    private void onKeyPressed(KeyEvent evt) {
        if (evt.getCode() == KeyCode.F1) {
            fireEvent(new UserInputEvent(new UserInput.ShowDebugger()));
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
            canvas.requestFocus(); // Only needed for keystrokes
        });
    }

    /**
     * Describes a feature, based on what it is.  Supported features include
     * Signs and Mannikins.
     * @param id The feature entity's ID
     */
    public void describeFeature(long id) {
        repaint();
        var entity = region.get(id);

        if (entity.sign() != null) {
            var signName = entity.sign().name();
            var text = region.getString(signName);

            displayTextBlock(entity, text);
        } else if (entity.mannikin() != null) {
            var name = entity.mannikin().name();
            StringBuilder buff = new StringBuilder();
            buff.append(region.getString(name + ".name")).append("\n\n");
            buff.append(region.getString(name + ".description")).append("\n\n");

            List<String> greetings = region.strings().keyList().stream()
                .filter(key -> key.startsWith(name + ".greeting"))
                .map(key -> region.strings().get(key).orElseThrow())
                .toList();

            buff.append(random.pickFrom(greetings));

            displayTextBlock(entity, buff.toString());
        }
    }

    public void displayTextBlock(Entity entity, String text) {
        repaint();
        var xLeft = 50.0;
        var yTop = 50.0;
        var width = canvas.getWidth() - 100;
        var height= canvas.getHeight() - 100;

        // The background
        canvas.gc().setFill(Color.DARKBLUE);
        canvas.gc().fillRect(xLeft, yTop, width, height);

        // The image
        var ix = xLeft + 20;
        var iy = yTop + 40;
        var terrain = region.getTerrain(entity.cell());
        canvas.gc().drawImage(ImageUtils.embiggen(terrain.image(), 2), ix, iy);
        canvas.gc().drawImage(ImageUtils.embiggen(img(entity.sprite()), 2), ix, iy);

        // The "click to continue"
        canvas.gc().setFill(Color.WHITE);
        canvas.gc().setFont(Font.font("Helvetica", 14));
        canvas.gc().fillText("Click to continue...",
            xLeft + 20,
            yTop + height - 20);

        // The game text
        Text block = new Text(text);
        block.setFont(Font.font("Helvetica", 18));
        block.setWrappingWidth(width - 80);
        block.setFill(Color.WHITE);
        StackPane.setAlignment(block, Pos.TOP_LEFT);
        StackPane.setMargin(block, new Insets(
            yTop + 40,            // Top
            xLeft + width - 20,   // Right
            yTop + height - 40,   // Bottom
            xLeft + 130));        // Left

        getChildren().setAll(canvas, block);
    }

    public void repaint() {
        canvas.clear();
        targets.clear();
        getChildren().setAll(canvas);

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
            case POINTER:
                selected.remove(Button.MAGNIFIER);
                selected.add(Button.POINTER);
                break;
            case MAGNIFIER:
                selected.remove(Button.POINTER);
                selected.add(Button.MAGNIFIER);
                break;
            default:
                region.log("TODO: " + btn);
                break;
        }
    }

}
