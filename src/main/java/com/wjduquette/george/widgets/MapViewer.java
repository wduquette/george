package com.wjduquette.george.widgets;

import com.wjduquette.george.ecs.*;
import com.wjduquette.george.model.RegionMap;
import com.wjduquette.george.model.TerrainTile;
import com.wjduquette.george.model.TerrainType;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;

public class MapViewer extends StackPane {
    public final static int HEIGHT_IN_TILES = 20;
    public final static int WIDTH_IN_TILES = 25;

    //-------------------------------------------------------------------------
    // Instance Variables

    // The canvas on which the map is drawn.
    private final CanvasPane canvas;

    private int rowOffset = 0;
    private int colOffset = 0;

    // The map currently being displayed
    private RegionMap map = null;

    //-------------------------------------------------------------------------
    // Constructor

    public MapViewer() {
        canvas = new CanvasPane();
        getChildren().add(canvas);
        canvas.setOnMouseClicked(me -> onMouseClick(me));
        canvas.setOnKeyPressed(evt -> onKeyPressed(evt));
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

        if (map.contains(cell)) {
            CellClickEvent.generate(cell, evt);
        }
    }

    //-------------------------------------------------------------------------
    // Public Methods

    public void setMap(RegionMap map) {
        this.map = map;

        double width = map.getTileWidth() * WIDTH_IN_TILES;
        double height = map.getTileHeight() * HEIGHT_IN_TILES;

        canvas.setMinWidth(width);
        canvas.setPrefWidth(width);
        canvas.setMaxWidth(width);
        canvas.setMinHeight(height);
        canvas.setPrefHeight(height);
        canvas.setMaxHeight(height);

        canvas.clear();
        Platform.runLater(() -> {
            repaint();
            canvas.requestFocus();
        });
    }

    public void repaint() {
        canvas.clear();
        Entity player = map.query(Mobile.class).findFirst().get();
        computeOffsets(player.cell());

        // FIRST, render the terrain
        // TODO Limit the area: we only need to draw what's in sight.
        for (int r = 0; r < map.getHeight(); r++) {
            for (int c = 0; c < map.getWidth(); c++) {
                TerrainTile tile = map.getTerrain(r, c);
                canvas.drawImage(tile.image(), rc2xy(r, c));
            }
        }

        // NEXT, render the features
        for (Entity feature : map.query(Feature.class).toList()) {
            canvas.drawImage(feature.tile().image(), cell2xy(feature.cell()));
        }

        // NEXT, render the mobiles on top
        for (Entity mobile : map.query(Mobile.class).toList()) {
            canvas.drawImage(mobile.tile().image(), cell2xy(mobile.cell()));
        }
    }

    // Compute the row and column offsets so that the given cell is in the
    // middle of the view pane
    private void computeOffsets(Cell cell) {
        rowOffset = cell.row() - HEIGHT_IN_TILES/2;
        colOffset = cell.col() - WIDTH_IN_TILES/2;
    }

    // Convert cell coordinates to pixel coordinates.
    private Point2D cell2xy(Cell cell) {
        return rc2xy(cell.row(), cell.col());
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
