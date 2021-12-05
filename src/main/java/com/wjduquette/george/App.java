package com.wjduquette.george;

import com.wjduquette.george.model.Cell;
import com.wjduquette.george.model.Player;
import com.wjduquette.george.model.RegionMap;
import com.wjduquette.george.ecs.*;
import com.wjduquette.george.util.Looper;
import com.wjduquette.george.widgets.CellClickEvent;
import com.wjduquette.george.widgets.MapViewer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.List;

public class App extends Application {
    //-------------------------------------------------------------------------
    // Constants

    // How often the game loop executes
    private final int LOOP_MSECS = 100;

    //-------------------------------------------------------------------------
    // Instance Variables

    // The GUI component
    private final MapViewer viewer = new MapViewer();

    // The timer for the game loop
    private final Looper looper = new Looper(LOOP_MSECS, this::gameLoop);

    // The map we're currently wandering about on.
    private RegionMap region = null;

    //-------------------------------------------------------------------------
    // Main Program

    @Override
    public void start(Stage stage) {
        region = new RegionMap(getClass(),
            "assets/regions/overworld/overworld.region");

        Cell origin = region.query(Point.class)
            .filter(e -> e.point().name().equals("origin"))
            .map(Entity::cell)
            .findFirst()
            .orElse(new Cell(10,10));

        Player george = new Player("George");

        region.getEntities().make().mobile("george")
            .put(george)
            .put(origin)
            .tile(TileSets.MOBILES.get("mobile.george").orElseThrow());

        // Dump the entities table
        region.getEntities().dump();

        viewer.addEventHandler(CellClickEvent.CELL_CLICK, this::onCellClick);
        viewer.setMap(region);

        // NEXT, configure the GUI
        Scene scene = new Scene(viewer, 800, 600);
        stage.setTitle("George's Saga!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    //-------------------------------------------------------------------------
    // Click Handling

    // Handle cells clicks
    private void onCellClick(CellClickEvent event) {
        // Note: Eventually present, this will be the "Player Input" system.
        //
        // Note: At present we disallow clicks if any mobile is active; but
        // George is the only Mobile.  Eventually we will need to have a
        // Mover queue, and we will disallow clicks whenever a
        // non-player-controlled mobile isn't the "current mover".
        if (mobilesAreActive()) {
            return;
        }

        Entity player = region.query(Player.class).findFirst().get();
        List<Cell> route = RegionMap.findRoute(c -> region.isWalkable(c),
            player.cell(), event.getCell());

        if  (!route.isEmpty()) {
            player.mobile().doFirst(() -> followRoute(player, route));
            looper.run();
        }
    }

    //-------------------------------------------------------------------------
    //  The Game Loop

    private void gameLoop() {
        // FIRST, execute any goals
        List<Entity> activeMobiles = region.query(Mobile.class)
            .filter(e -> e.mobile().hasGoal())
            .toList();

        for (Entity mob : activeMobiles) {
            mob.mobile().doNext();
        }

        // NEXT, if there's no one with a goal, stop until we get some
        // user input.
        //
        // NOTE: When there are multiple movers, we will need to support
        // moving to the next mover.
        if (!mobilesAreActive()) {
            looper.stop();
        }

        // FINALLY, repaint.
        viewer.repaint();
    }

    // Are there any mobiles with active goals?
    private boolean mobilesAreActive() {
        return region.query(Mobile.class).anyMatch(e -> e.mobile().hasGoal());
    }

    //-------------------------------------------------------------------------
    // Goal functions for Mobiles

    private void followRoute(Entity entity, List<Cell> route) {
        Cell nextCell = route.remove(0);

        System.out.println("Stepping to " + nextCell);
        entity.put(nextCell);

        if (!route.isEmpty()) {
            entity.mobile().doFirst(() -> followRoute(entity, route));
        }
    }



}
