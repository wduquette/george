package com.wjduquette.george;

import com.wjduquette.george.widgets.CanvasPane;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class App extends Application {
    private final CanvasPane canvas = new CanvasPane();

    @Override
    public void start(Stage stage) {
        canvas.setOnResize(this::fillCanvas);

        Scene scene = new Scene(canvas, 320, 240);
        stage.setTitle("George's Saga!");
        stage.setScene(scene);
        stage.show();
    }

    private void fillCanvas() {
        GraphicsContext gc = canvas.gc();

        double w = canvas.getWidth();
        double h = canvas.getHeight();
        gc.setLineWidth(5.0);
        gc.setStroke(Color.RED);
        gc.setFill(Color.GREEN);

        gc.fillRect(0, 0, w, h);
        gc.strokeRect(0, 0, w, h);
    }


    public static void main(String[] args) {
        launch();
    }
}