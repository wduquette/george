package com.wjduquette.george.widgets;

import com.wjduquette.george.ecs.*;
import com.wjduquette.george.model.RegionMap;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
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
        canvas.setOnKeyPressed(evt -> {
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
            Cell cell = player.cell();
            player.putCell(cell.row() + rDelta, cell.col() + cDelta);
            repaint();
        });
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
        for (Entity terrain : map.query(Terrain.class).toList()) {
            canvas.drawImage(terrain.tile().image(), rc2xy(terrain.cell()));
        }

        // NEXT, render the features
        for (Entity feature : map.query(Feature.class).toList()) {
            canvas.drawImage(feature.tile().image(), rc2xy(feature.cell()));
        }

        // NEXT, render the mobiles on top
        for (Entity mobile : map.query(Mobile.class).toList()) {
            canvas.drawImage(mobile.tile().image(), rc2xy(mobile.cell()));
        }
    }

    // Compute the row and column offsets so that the given cell is in the
    // middle of the view pane
    private void computeOffsets(Cell cell) {
        rowOffset = cell.row() - HEIGHT_IN_TILES/2;
        colOffset = cell.col() - WIDTH_IN_TILES/2;
    }

    // Convert cell coordinates to pixel coordinates.
    private Point2D rc2xy(Cell cell) {
        int x = (cell.col() - colOffset) * map.getTileWidth();
        int y = (cell.row() - rowOffset) * map.getTileHeight();
        return new Point2D(x,y);
    }
}
