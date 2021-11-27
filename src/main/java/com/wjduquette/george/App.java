package com.wjduquette.george;

import com.wjduquette.george.graphics.TerrainTileSet;
import com.wjduquette.george.graphics.TileSet;
import com.wjduquette.george.model.RegionMap;
import com.wjduquette.george.model.TerrainType;
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
    private TileSet mobiles;
    private RegionMap overworld;

    @Override
    public void start(Stage stage) {
        mobiles = new TileSet(getClass(), "assets/tilesets/mobiles.tileset");


//        // first, initialize the world.
//        for (int r = -5; r <= 5; r++) {
//            for (int c = -5; c <= 5; c++) {
//                String tile = Math.abs(r) > 2 || Math.abs(c) > 2
//                    ? "standard.grass" : "standard.tile_floor";
//                Image img = terrain.get(tile).orElseThrow().image();
//                world.make().putTerrain(TerrainType.FLOOR)
//                    .putCell(r,c)
//                    .putTile(img);
//            }
//        }
//
//        world.make().putMobile()
//            .putCell(0,0)
//            .putTile(mobiles.get("mobile.george").orElseThrow());

        overworld = new RegionMap(getClass(),
            "assets/regions/overworld/overworld.region");

        overworld.getEntities().make().putMobile()
            .putCell(10,10)
            .putTile(mobiles.get("mobile.george").orElseThrow());

        // NEXT, configure the GUI
        // TODO: revise to display the region.
        canvas.setOnResize(() -> renderRegion(overworld));

        Scene scene = new Scene(canvas, 440, 440);
        stage.setTitle("George's Saga!");
        stage.setScene(scene);
        stage.show();
    }

    private void println(String line) {
        System.out.println(line);
    }

    private void renderRegion(RegionMap map) {
        GraphicsContext gc = canvas.gc();
        canvas.clear();

        // FIRST, render the terrain
        for (Entity terrain : map.getEntities().query(Terrain.class).toList()) {
            canvas.drawImage(terrain.tile().image(), rc2xy(terrain.cell()));
        }

        // NEXT, render the features
        for (Entity feature : map.getEntities().query(Feature.class).toList()) {
            canvas.drawImage(feature.tile().image(), rc2xy(feature.cell()));
        }

        // NEXT, render the mobiles on top
        for (Entity mobile : map.getEntities().query(Mobile.class).toList()) {
            canvas.drawImage(mobile.tile().image(), rc2xy(mobile.cell()));
        }
    }

    // Convert cell coordinates to pixel coordinates.
    // Need to account for the cell offset.
    private Point2D rc2xy(Cell cell) {
        int x = cell.col()*40;
        int y = cell.row()*40;
        return new Point2D(x,y);
    }

    public static void main(String[] args) {
        launch();
    }
}
