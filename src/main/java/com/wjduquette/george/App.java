package com.wjduquette.george;

import com.wjduquette.george.model.Cell;
import com.wjduquette.george.model.Player;
import com.wjduquette.george.model.RegionMap;
import com.wjduquette.george.ecs.*;
import com.wjduquette.george.widgets.CellClickEvent;
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

        Player george = new Player("George");

        overworld.getEntities().make().mobile("george")
            .put(george)
            .put(origin)
            .tile(TileSets.MOBILES.get("mobile.george").orElseThrow());


        // Dump the entities table
        overworld.getEntities().dump();

        MapViewer viewer = new MapViewer();
        viewer.addEventHandler(CellClickEvent.CELL_CLICK, evt -> {
            System.out.println("user clicked on cell: " + evt.getCell());
        });
        viewer.setMap(overworld);

        // NEXT, configure the GUI
        Scene scene = new Scene(viewer, 800, 600);
        stage.setTitle("George's Saga!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
