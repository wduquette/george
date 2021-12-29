package com.wjduquette.george.widgets;

import com.wjduquette.george.App;
import com.wjduquette.george.ecs.Player;
import com.wjduquette.george.model.Cell;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

/**
 * MapPanel displays the known portions of the current region's map.
 */
public class MapPanel extends GamePane implements Panel {
    private final static double INSET = 50;
    private final static double FRAME = 5;

    private Runnable onClose = null;

    public MapPanel(App app) {
        super(app);
        setPadding(new Insets(INSET));
    }

    @Override public Node asNode() { return this; }
    @Override public void setOnClose(Runnable func) { this.onClose = func; }

    protected void onMouseClick(MouseEvent evt) {
        if (onClose != null) {
            onClose.run();
        }
    }

    protected void onRepaint() {
        var region = app().getCurrentRegion();
        var w = getWidth() - 2*INSET;
        var h = getHeight() - 2*INSET;

        // NEXT, how big should we draw the map?  How many pixels per cell?
        var xSize = mapCellSize(w, region.getWidth());
        var ySize = mapCellSize(h, region.getHeight());
        var cellSize = Math.min(xSize, ySize);

        var mapWidth = region.getWidth() * cellSize;
        var mapHeight = region.getHeight() * cellSize;
        var xLeft = FRAME + (w - mapWidth)/2.0;
        var yTop = FRAME + (h - mapHeight)/2.0;

        // Fill the background
        gc().setFill(Color.BLACK);
        gc().fillRect(xLeft - FRAME, yTop - FRAME,
            mapWidth + 2*FRAME, mapHeight + 2*FRAME);

        for (int r = 0; r < region.getHeight(); r++) {
            for (int c = 0; c < region.getWidth(); c++) {
                var cell = new Cell(r,c);
                var x = xLeft + c * cellSize;
                var y = yTop + r * cellSize;

                Color color = Color.WHITE;

                if (region.isSeen(r, c)) {
                    color = switch (region.getTerrainType(cell)) {
                        case NONE -> Color.BLACK;
                        case UNKNOWN -> Color.BLACK;
                        case WATER -> Color.BLUE;
                        case FLOOR -> Color.SANDYBROWN;
                        default -> Color.color(0.2, 0.2, 0.2);
                    };
                }

                gc().setFill(color);
                gc().fillRect(x, y, cellSize, cellSize);
            }
        }

        // NEXT, draw the player/leader a little bigger than its cell.
        var player = region.query(Player.class).findFirst().orElseThrow();

        var x = xLeft + player.cell().col() * cellSize;
        var y = yTop + player.cell().row() * cellSize;

        gc().setFill(Color.CYAN);
        gc().fillRect(x - 1, y - 1, cellSize + 2, cellSize + 2);

    }

    private double mapCellSize(double numPixels, double numCells) {
        numPixels = numPixels - 2*FRAME;

        if (numCells >= numPixels) {
            return 1;
        }

        var size = 1;
        while (size*numCells < numPixels) {
            size++;
        }

        return size - 1;
    }
}
