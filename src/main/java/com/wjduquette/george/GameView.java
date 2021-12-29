package com.wjduquette.george;

import com.wjduquette.george.ecs.*;
import com.wjduquette.george.model.*;
import com.wjduquette.george.widgets.GamePane;
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
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.*;

public class GameView extends GamePane {
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

    private int rowOffset = 0;
    private int colOffset = 0;

    private int rowMin = 0;
    private int rowMax = 0;
    private int colMin = 0;
    private int colMax = 0;

    // The region currently being displayed
    private Region region = null;

    // Which selectable buttons are selected.
    private final Set<Button> selected = new HashSet<>();

    //-------------------------------------------------------------------------
    // Constructor

    public GameView(App app) {
        super(app);

        // Configure the pane
        setBackground(new Background(
            new BackgroundFill(Color.BLACK, null, null)));

        // Configure the buttons
        selected.add(Button.POINTER);
    }

    //-------------------------------------------------------------------------
    // GamePane API

    protected void onMouseClick(MouseEvent evt) {
        Point2D mouse = toPoint(evt);

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
    protected void onKeyPress(KeyEvent evt) {
        if (evt.getCode() == KeyCode.I) {
            App.println("GameView: " + evt);
            fireInputEvent(new UserInput.ShowInventory());
        } else if (evt.getCode() == KeyCode.F1) {
            fireInputEvent(new UserInput.ShowDebugger());
        }
    }

    //-------------------------------------------------------------------------
    // Public Methods

    public void setRegion(Region map) {
        this.region = map;

        Platform.runLater(this::repaint);
    }

    public Region getRegion() {
        return region;
    }

    protected void onRepaint() {
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
                canvas().drawImage(tile.image(), rc2xy(r, c));

                // TODO: for now, mark a cell "seen" if it has appeared in
                // the rendered area.
                region.markSeen(r,c);
            }
        }

        // NEXT, render the features
        for (Entity feature : region.query(Feature.class).toList()) {
            canvas().drawImage(toImage(feature), entity2xy(feature));
        }

        // NEXT, render the mobiles on top
        for (Entity mobile : region.query(Mobile.class).toList()) {
            canvas().drawImage(toImage(mobile), entity2xy(mobile));
        }

        // NEXT, render other visual effects that have their own tiles.
        for (Entity effect : region.query(VisualEffect.class, Sprite.class).toList()) {
            canvas().drawImage(toImage(effect), entity2xy(effect));
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
        var w = getWidth();
        var h = getHeight();
        var bw = sprites().width();
        var bh = sprites().height();
        var border = 4;

        var nbuttons = Button.values().length;
        var barw = nbuttons*(bw + border) + border;
        var barh = bh + 2*border;

        var bary = h - (barh + BAR_MARGIN);
        var barx = (w - barw)/2.0;

        gc().setFill(Color.BLACK);
        gc().fillRect(barx, bary, barw, barh);

        for (Button btn : Button.values()) {
            var i = btn.ordinal();
            var bx = barx + border + i*(bw + border);
            var by = bary + border;

            var fill = selected.contains(btn) ? Color.LIGHTGRAY : Color.GRAY;

            gc().setFill(fill);
            gc().fillRect(bx, by, bw, bh);
            gc().drawImage(sprites().get(btn.sprite()), bx, by);

            var box = new BoundingBox(bx, by, bw, bh);
            addTarget(box, () -> buttonClick(btn));
        }
    }

    //-------------------------------------------------------------------------
    // LogMessage Display

    private static final double LOG_MIN_Y = 20;
    private static final double LOG_X = 20;
    private static final double LOG_HEIGHT = 25;


    private void drawLogMessage(int index, String message) {
        double x = LOG_X;
        double y = getHeight() - (index*LOG_HEIGHT + LOG_MIN_Y);

        gc().setStroke(Color.BLACK);
        gc().setLineWidth(1);
        gc().setFill(Color.WHITE);
        gc().setFont(Font.font("Helvetica", LOG_HEIGHT));
        gc().strokeText(message, x, y);
        gc().fillText(message, x, y);
    }

    //-------------------------------------------------------------------------
    // Status Box display

    private void drawStatusBox(int index, Entity player) {
        double oborder = 2;
        double iborder = 1;
        double border = oborder + iborder;
        double hName = 12;
        double gap = 5;
        Image sprite = toImage(player);
        double boxHeight = sprite.getHeight() + 2 * border + hName;
        double boxWidth = sprite.getWidth() + 2 * border;

        double yTop = 10 + index * (boxHeight + gap);
        double xLeft = 10;

        var box = new BoundingBox(xLeft, yTop, boxWidth, boxHeight);
        var input = new UserInput.StatusBox(player.id());
        addTarget(box, () -> fireInputEvent(input));

        gc().setFill(Color.BLACK);
        gc().fillRect(xLeft, yTop, boxWidth, boxHeight);

        gc().setFill(Color.WHITE);
        gc().fillRect(
            xLeft + oborder, yTop + oborder,
            boxWidth - 2 * oborder, boxHeight - 2 * oborder);

        gc().setFill(Color.CYAN);
        gc().fillRect(
            xLeft + border, yTop + border,
            boxWidth - 2 * border, boxHeight - 2 * border);

        gc().drawImage(sprite, xLeft + border, yTop + border);
    }


    //-------------------------------------------------------------------------
    // Utilities

    // Compute the row and column offsets so that the given cell is in the
    // middle of the view pane
    private void computeBounds(Cell cell) {
        double heightInTiles = getHeight() / region.getTileHeight();
        double widthInTiles = getWidth() / region.getTileWidth();
        rowOffset = cell.row() - (int) heightInTiles / 2;
        colOffset = cell.col() - (int) widthInTiles / 2;

        rowMax = Math.min(region.getHeight(), rowOffset + (int) heightInTiles + 1);
        colMax = Math.min(region.getHeight(), colOffset + (int) widthInTiles + 1);

        rowMin = Math.max(0, rowOffset);
        colMin = Math.max(0, colOffset);
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
