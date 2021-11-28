package com.wjduquette.george;

import com.wjduquette.george.model.RegionMap;
import com.wjduquette.george.widgets.CanvasPane;
import com.wjduquette.george.ecs.*;
import com.wjduquette.george.widgets.MapViewer;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.stage.Stage;

public class App extends Application {
    private final CanvasPane canvas = new CanvasPane();

    @Override
    public void start(Stage stage) {
        RegionMap overworld = new RegionMap(getClass(),
            "assets/regions/overworld/overworld.region");

        Cell origin = overworld.query(Point.class)
            .filter(e -> e.point().name().equals("origin"))
            .map(Entity::cell)
            .findFirst()
            .orElse(new Cell(10,10));

        overworld.getEntities().make().putMobile()
            .putCell(origin)
            .putTile(TileSets.MOBILES.get("mobile.george").orElseThrow());

        MapViewer viewer = new MapViewer();
        viewer.setMap(overworld);

        // NEXT, configure the GUI
        Scene scene = new Scene(viewer, 440, 440);
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
