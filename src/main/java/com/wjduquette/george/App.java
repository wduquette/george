package com.wjduquette.george;

import com.wjduquette.george.graphics.ImageUtils;
import com.wjduquette.george.tiles.Mobiles;
import com.wjduquette.george.tiles.Terrains;
import com.wjduquette.george.widgets.CanvasPane;
import com.wjduquette.george.world.*;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class App extends Application {
    private final CanvasPane canvas = new CanvasPane();
    private final World world = new World();

    @Override
    public void start(Stage stage) {
        // FIRST, initialize the world.
        for (int r = -5; r <= 5; r++) {
            for (int c = -5; c <= 5; c++) {
                Image img;
                if (Math.abs(r) > 2 || Math.abs(c) > 2) {
                    img = Terrains.GRASS;
                } else {
                    img = Terrains.TILE_FLOOR;
                }
                world.make().terrain()
                    .cell(r,c)
                    .tile(img);
            }
        }

        world.make().mobile()
            .cell(0,0)
            .tile(Mobiles.GEORGE);

        // NEXT, configure the GUI
        canvas.setOnResize(() -> renderWorld(world));

        Scene scene = new Scene(canvas, 440, 440);
        stage.setTitle("George's Saga!");
        stage.setScene(scene);
        stage.show();
    }

    private void renderWorld(World world) {
        GraphicsContext gc = canvas.gc();
        canvas.clear();

        // FIRST, render the terrain
        for (Entity terrain : world.query(Terrain.class, Cell.class, Tile.class).toList()) {
            canvas.drawImage(terrain.tile().image(), rc2xy(terrain.cell()));
        }

        // NEXT, render the mobiles on top
        for (Entity mobile : world.query(Mobile.class, Cell.class, Tile.class).toList()) {
            canvas.drawImage(mobile.tile().image(), rc2xy(mobile.cell()));
        }
    }

    // Convert cell coordinates to pixel coordinates.
    // Need to account for the cell offset.
    private Point2D rc2xy(Cell cell) {
        int x = (5 + cell.col())*40;
        int y = (5 + cell.row())*40;
        return new Point2D(x,y);
    }

    public static void main(String[] args) {
        launch();
    }
}
