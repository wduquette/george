package com.wjduquette.george;

import com.wjduquette.george.model.RegionMap;
import com.wjduquette.george.widgets.CanvasPane;
import com.wjduquette.george.ecs.*;
import com.wjduquette.george.widgets.MapViewer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage stage) {
        RegionMap overworld = new RegionMap(getClass(),
            "assets/regions/overworld/overworld.region");

        Cell origin = overworld.query(Point.class)
            .filter(e -> e.point().name().equals("origin"))
            .map(Entity::cell)
            .findFirst()
            .orElse(new Cell(10,10));

        overworld.getEntities().make().mobile("george")
            .add(origin)
            .tile(TileSets.MOBILES.get("mobile.george").orElseThrow());


        // Dump the entities table
        overworld.getEntities().dump();

        MapViewer viewer = new MapViewer();
        viewer.setMap(overworld);

        // NEXT, configure the GUI
        Scene scene = new Scene(viewer, 440, 440);
        stage.setTitle("George's Saga!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
