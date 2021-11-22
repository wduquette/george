package com.wjduquette.george;

import com.wjduquette.george.graphics.ImageUtils;
import com.wjduquette.george.tiles.Buttons;
import com.wjduquette.george.tiles.Effects;
import com.wjduquette.george.widgets.CanvasPane;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

public class App extends Application {
    private final CanvasPane canvas = new CanvasPane();
    private List<Image> tiles = null;

    @Override
    public void start(Stage stage) {
        // FIRST, read tiles
        tiles = List.of(
            Effects.BULLET,
            Effects.TARGET,
            Effects.FIREBALL,
            Effects.JAWS,
            Effects.FIST,
            Effects.SWOOSH,
            Effects.CLAW,
            Effects.SLIME,
            Effects.ZZZ
        );
        System.out.println("Got tiles: " + tiles.size());

        // NEXT, configure the GUI
        canvas.setOnResize(this::fillCanvas);

        Scene scene = new Scene(canvas, 320, 240);
        stage.setTitle("George's Saga!");
        stage.setScene(scene);
        stage.show();
    }

    private void fillCanvas() {
        GraphicsContext gc = canvas.gc();
        canvas.clear();

        for (int i = 0; i < tiles.size(); i++) {
            int x = 40 * (i % 8);
            int y = 40 * (i / 8);

            gc.drawImage(tiles.get(i), x, y);
        }

    }


    public static void main(String[] args) {
        launch();
    }
}
