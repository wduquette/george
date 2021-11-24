package com.wjduquette.george;

import com.wjduquette.george.graphics.ImageUtils;
import com.wjduquette.george.tiles.Mobiles;
import com.wjduquette.george.tiles.Terrains;
import com.wjduquette.george.widgets.CanvasPane;
import com.wjduquette.george.world.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
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
                Entity e = world.make();
                e.put(new Cell(r,c));

                if (Math.abs(r) > 2 || Math.abs(c) > 2) {
                    e.put(new Terrain(Terrains.GRASS));
                } else {
                    e.put(new Terrain(Terrains.TILE_FLOOR));
                }
            }
        }

        Entity player = world.make();
        player.put(new Cell(0,0));
        player.put(new Mobile(Mobiles.GEORGE));

        // NEXT, configure the GUI
        canvas.setOnResize(this::render);

        Scene scene = new Scene(canvas, 440, 440);
        stage.setTitle("George's Saga!");
        stage.setScene(scene);
        stage.show();
    }

    private void render() {
        GraphicsContext gc = canvas.gc();
        canvas.clear();

        for (Entity ground : world.query(Cell.class, Terrain.class).toList()) {
            Cell cell = ground.cell();
            Terrain terrain = ground.terrain();
            // TODO: need to allow for scrolling, other visual controls.
            int x = (5 + cell.col())*40;
            int y = (5 + cell.row())*40;
            gc.drawImage(terrain.tile(), x, y);
        }

        for (Entity mobile : world.query(Cell.class, Mobile.class).toList()) {
            Cell cell = mobile.cell();
            Mobile mob = mobile.mobile();
            // TODO: need to allow for scrolling, other visual controls.
            int x = (5 + cell.col())*40;
            int y = (5 + cell.row())*40;
            gc.drawImage(mob.image(), x, y);
        }
    }


    public static void main(String[] args) {
        launch();
    }
}
