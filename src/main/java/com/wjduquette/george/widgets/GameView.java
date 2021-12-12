package com.wjduquette.george.widgets;

import com.wjduquette.george.ecs.*;
import com.wjduquette.george.model.*;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import java.util.List;

public class GameView extends StackPane {
    public final static int HEIGHT_IN_TILES = 20;
    public final static int WIDTH_IN_TILES = 25;

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

    //-------------------------------------------------------------------------
    // Constructor

    public GameView() {
        canvas = new CanvasPane();
        getChildren().add(canvas);
        canvas.setOnResize(this::repaint);
        canvas.setOnMouseClicked(this::onMouseClick);

        canvas.setBackground(new Background(
            new BackgroundFill(Color.BLACK, null, null)));
    }

    //-------------------------------------------------------------------------
    // Event Handling

    private void onMouseClick(MouseEvent evt) {
        Point2D mouse = canvas.ofMouse(evt);
        Cell cell = xy2rc(mouse);

        if (region.contains(cell)) {
            UserInputEvent.generate(new UserInput.CellClick(cell), evt);
        }
    }

    //-------------------------------------------------------------------------
    // Public Methods

    public void setRegion(Region map) {
        this.region = map;

        Platform.runLater(() -> {
            repaint();
            canvas.requestFocus(); // Only needed for keystrokes
        });
    }

    public void repaint() {
        canvas.clear();

        // TEMP
        Entity player = region.query(Player.class).findFirst().get();
        // Don't recompute bounds if the player is executing a plan.
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
            canvas.drawImage(feature.tile().image(), entity2xy(feature));
        }

        // NEXT, if there's a target compute the route.
        // TODO: TEMP
//        if (target != null) {
//            List<Cell> route = Region.findRoute(c -> map.isWalkable(c),
//                player.cell(), target);
//
//            if (!route.isEmpty()) {
//                route.add(0, player.cell());
//                drawRoute(route);
//            }
//        }

        // NEXT, render the mobiles on top
        for (Entity mobile : region.query(Mobile.class).toList()) {
            canvas.drawImage(mobile.tile().image(), entity2xy(mobile));
        }

        // NEXT, render other visual effects that have their own tiles.
        for (Entity effect : region.query(VisualEffect.class, Tile.class).toList()) {
            canvas.drawImage(effect.tile().image(), entity2xy(effect));
        }

        // NEXT, render player status boxes
        drawStatusBox(0, player);
    }


    private void drawStatusBox(int index, Entity player) {
        double oborder = 2;
        double iborder = 1;
        double border = oborder + iborder;
        double hName = 12;
        double gap = 5;
        double boxHeight = player.tile().height() + 2 * border + hName;
        double boxWidth = player.tile().width() + 2 * border;

        double yTop = 10 + index * (boxHeight + gap);
        double xLeft = 10;

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

        canvas.gc().drawImage(player.tile().image(),
            xLeft + border, yTop + border);
    }


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
}
