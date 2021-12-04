package com.wjduquette.george.widgets;

import com.wjduquette.george.ecs.*;
import com.wjduquette.george.model.*;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import java.util.List;

public class MapViewer extends StackPane {
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

    // The map currently being displayed
    private RegionMap map = null;

    // After a mouse click, the target cell.
    private Cell target = null;

    //-------------------------------------------------------------------------
    // Constructor

    public MapViewer() {
        canvas = new CanvasPane();
        getChildren().add(canvas);
        canvas.setOnResize(this::repaint);
        canvas.setOnMouseClicked(this::onMouseClick);
        canvas.setOnKeyPressed(this::onKeyPressed);

        canvas.setBackground(new Background(
            new BackgroundFill(Color.BLACK, null, null)));
    }

    //-------------------------------------------------------------------------
    // Event Handling

    private void onKeyPressed(KeyEvent evt) {
        int rDelta = 0;
        int cDelta = 0;
        System.out.println("key: " + evt.getCode());

        switch (evt.getCode()) {
            case UP:
            case KP_UP:
                rDelta = -1;
                break;
            case RIGHT:
            case KP_RIGHT:
                cDelta = 1;
                break;
            case DOWN:
            case KP_DOWN:
                rDelta = 1;
                break;
            case LEFT:
            case KP_LEFT:
                cDelta = -1;
                break;
            default:
                return;
        }

        // TEMP
        Entity player = map.query(Mobile.class).findFirst().get();
        Cell cell = player.cell().adjust(rDelta, cDelta);
        TerrainType terrain = map.getTerrainType(cell);

        if (terrain.isWalkable()) {
            player.put(cell);
        } else {
            System.out.println("Bonk!");
        }
        repaint();
    }

    private void onMouseClick(MouseEvent evt) {
        Point2D mouse = canvas.ofMouse(evt);
        Cell cell = xy2rc(mouse);

        // TEMP: So I can render the route
        target = cell;
        repaint();

        if (map.contains(cell)) {
            CellClickEvent.generate(cell, evt);
        }
    }

    //-------------------------------------------------------------------------
    // Public Methods

    public void setMap(RegionMap map) {
        this.map = map;

        Platform.runLater(() -> {
            repaint();
            canvas.requestFocus(); // Only needed for keystrokes
        });
    }

    public void repaint() {
        canvas.clear();

        // TEMP
        Entity player = map.query(Player.class).findFirst().get();
        computeBounds(player.cell());

        // FIRST, render the terrain
        for (int r = rowMin; r < rowMax; r++) {
            for (int c = colMin; c < colMax; c++) {
                TerrainTile tile = map.getTerrain(r, c);
                canvas.drawImage(tile.image(), rc2xy(r, c));
            }
        }

        // NEXT, render the features
        for (Entity feature : map.query(Feature.class).toList()) {
            canvas.drawImage(feature.tile().image(), cell2xy(feature.cell()));
        }

        // NEXT, if there's a target compute the route.
        if (target != null) {
            List<Cell> route = RegionMap.findRoute(this::isWalkable,
                player.cell(), target);

            if  (!route.isEmpty()) {
                route.add(0, player.cell());
                drawRoute(route);
            }
        }

        // NEXT, render the mobiles on top
        for (Entity mobile : map.query(Mobile.class).toList()) {
            canvas.drawImage(mobile.tile().image(), cell2xy(mobile.cell()));
        }

        // NEXT, render player status boxes
        drawStatusBox(0, player);
    }

    // Probably belongs in RegionMap
    public boolean isWalkable(Cell cell) {
        return map.getTerrainType(cell).isWalkable();
    }

    private void drawStatusBox(int index, Entity player) {
        double oborder = 2;
        double iborder = 1;
        double border = oborder + iborder;
        double hName = 12;
        double gap = 5;
        double boxHeight = player.tile().height() + 2*border + hName;
        double boxWidth = player.tile().width() + 2*border;

        double yTop = 10 + index*(boxHeight + gap);
        double xLeft = 10;

        canvas.gc().setFill(Color.BLACK);
        canvas.gc().fillRect(xLeft, yTop, boxWidth, boxHeight);

        canvas.gc().setFill(Color.WHITE);
        canvas.gc().fillRect(
            xLeft + oborder, yTop + oborder,
            boxWidth - 2*oborder, boxHeight - 2*oborder);

        canvas.gc().setFill(Color.CYAN);
        canvas.gc().fillRect(
            xLeft + border, yTop + border,
            boxWidth - 2*border, boxHeight - 2*border);

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
        double heightInTiles = canvas.getHeight()/map.getTileHeight();
        double widthInTiles = canvas.getWidth()/map.getTileWidth();
        rowOffset = cell.row() - (int)heightInTiles/2;
        colOffset = cell.col() - (int)widthInTiles/2;

        rowMax = Math.min(map.getHeight(), rowOffset + (int)heightInTiles + 1);
        colMax = Math.min(map.getHeight(), colOffset + (int)widthInTiles + 1);

        rowMin = Math.max(0, rowOffset);
        colMin = Math.max(0, colOffset);
    }

    // Convert cell coordinates to the pixel coordinates of the upper-left
    // corner of the cell
    private Point2D cell2xy(Cell cell) {
        return rc2xy(cell.row(), cell.col());
    }

    // Convert cell coordinates to the pixel coordinates of the center of the
    // cell.
    private Point2D cell2centerxy(Cell cell) {
        Point2D p = rc2xy(cell.row(), cell.col());
        return new Point2D(
            p.getX() + (double)map.getTileWidth()/2,
            p.getY() + (double)map.getTileHeight()/2);
    }

    private Point2D rc2xy(int row, int col) {
        int x = (col - colOffset) * map.getTileWidth();
        int y = (row - rowOffset) * map.getTileHeight();
        return new Point2D(x,y);
    }

    private Cell xy2rc(Point2D pt) {
        int c = (int)(pt.getX() / map.getTileWidth()) + colOffset;
        int r = (int)(pt.getY() / map.getTileHeight()) + rowOffset;

        return new Cell(r,c);
    }
}
