package com.wjduquette.george;

import com.wjduquette.george.graphics.TerrainTileSet;
import com.wjduquette.george.graphics.TileSet;
import com.wjduquette.george.widgets.CanvasPane;
import com.wjduquette.george.ecs.*;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class App extends Application {
    private final CanvasPane canvas = new CanvasPane();
    private final EntityTable world = new EntityTable();
    private TerrainTileSet terrain;
    private TileSet mobiles;
    private TileSet items;
    private TileSet buttons;
    private TileSet effects;
    private TileSet features;
    private TileSet slots;
    private TileSet markers;

    @Override
    public void start(Stage stage) {
        terrain = new TerrainTileSet(getClass(), "assets/tilesets/standard.terrain");
        mobiles = new TileSet(getClass(), "assets/tilesets/mobiles.tileset");
        items = new TileSet(getClass(), "assets/tilesets/Items.tileset");
        buttons = new TileSet(getClass(), "assets/tilesets/Buttons.tileset");
        effects = new TileSet(getClass(), "assets/tilesets/Effects.tileset");
        features = new TileSet(getClass(), "assets/tilesets/Features.tileset");
        slots = new TileSet(getClass(), "assets/tilesets/Slots.tileset");
        markers = new TileSet(getClass(), "assets/tilesets/markers.tileset");


        // FIRST, initialize the world.
        for (int r = -5; r <= 5; r++) {
            for (int c = -5; c <= 5; c++) {
                String tile = Math.abs(r) > 2 || Math.abs(c) > 2
                    ? "standard.grass" : "standard.tile_floor";
                Image img = terrain.get(tile).orElseThrow().image();
                world.make().terrain()
                    .cell(r,c)
                    .tile(img);
            }
        }

        world.make().mobile()
            .cell(0,0)
            .tile(mobiles.get("mobile.george").orElseThrow());

        // NEXT, configure the GUI
        canvas.setOnResize(() -> renderWorld(world));

        Scene scene = new Scene(canvas, 440, 440);
        stage.setTitle("George's Saga!");
        stage.setScene(scene);
        stage.show();
    }

    private void println(String line) {
        System.out.println(line);
    }

    private void renderWorld(EntityTable world) {
        GraphicsContext gc = canvas.gc();
        canvas.clear();

        // FIRST, render the terrain
        for (Entity terrain : world.query(Terrain.class, Cell.class, Tile.class).toList()) {
            canvas.drawImage(terrain.tile().image(), rc2xy(terrain.cell()));
        }

        // NEXT, render the mobiles on top
        for (Entity mobile : world.stream().filter(e -> e.has(Mobile.class)).toList()) {
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
