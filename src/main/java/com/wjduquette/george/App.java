package com.wjduquette.george;

import com.wjduquette.george.model.*;
import com.wjduquette.george.ecs.*;
import com.wjduquette.george.util.Looper;
import com.wjduquette.george.widgets.Debugger;
import com.wjduquette.george.widgets.UserInput;
import com.wjduquette.george.widgets.UserInputEvent;
import com.wjduquette.george.widgets.GameView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Stack;
import java.util.function.Supplier;

public class App extends Application {
    //-------------------------------------------------------------------------
    // Constants

    // How often the game loop executes
    private static final int LOOP_MSECS = 50;
    private static final int DEBUGGER_REFRESH_TICKS = 10;

    //-------------------------------------------------------------------------
    // Instance Variables

    // The GUI component
    private final GameView viewer = new GameView();

    // The timer for the game loop
    private final Looper looper = new Looper(LOOP_MSECS, this::gameLoop);

    // A lookup table for region factories for region name
    private final Map<String, Supplier<Region>> regionFactories =
        new HashMap<>();

    // A lookup table for regions by name
    private final Map<String,Region> regions = new HashMap<>();

    // The map we're currently wandering about on.
    private Region region = null;

    // The most recent user input.
    private UserInput userInput = null;

    // The Interrupt stack
    private final Stack<Interrupt> interrupts = new Stack<>();

    // The debugger, or null if not shown.
    private Debugger debugger = null;

    // The game tick
    private long gameTick = 0;

    //-------------------------------------------------------------------------
    // Main Program

    @Override
    public void start(Stage stage) {
        populateRegionFactories();

        // TEMP
//        region = getRegion("test");
//        region = getRegion("floobham");
        region = getRegion("overworld");
        region.getEntities().dump();

        Cell origin = region.query(Point.class)
            .filter(e -> e.point().name().equals("origin"))
            .map(Entity::cell)
            .findFirst()
            .orElse(new Cell(10, 10));

        Player george = new Player("George");

        region.getEntities().make().mobile("george")
            .put(george)
            .cell(origin)
            .sprite(Sprites.ALL.getInfo("mobile.george"));

        viewer.setSprites(Sprites.ALL);
        viewer.addEventHandler(UserInputEvent.USER_INPUT, this::onUserInput);
        viewer.setRegion(region);

        // NEXT, configure the GUI
        Scene scene = new Scene(viewer, 800, 600);
        stage.setTitle("George's Saga!");
        stage.setScene(scene);
        stage.show();
        stage.setOnCloseRequest(evt -> System.exit(0));

        Platform.runLater(looper::run);
    }

    public static void main(String[] args) {
        launch();
    }

    //-------------------------------------------------------------------------
    // Click Handling

    // Handle cells clicks
    private void onUserInput(UserInputEvent event) {
        if (event.getInput() instanceof UserInput.ShowDebugger) {
            // Invoke the debugger
            showDebugger();
        } else {
            // Save the input.  It will be assessed by the
            // Planner on the next iteration of the GameLoop.
            userInput = event.getInput();
        }
    }

    private void showDebugger() {
        System.out.println("Show debugger");
        if (debugger == null) {
            debugger = new Debugger(this, viewer);
            debugger.setOnClose(() -> {
                debugger = null;
                System.out.println("Close debugger");
            });
        }
        debugger.show();
    }

    /**
     * Gets the current region.  This if for the use of the Debugger.
     * @return The region.
     */
    public Region getCurrentRegion() {
        return region;
    }

    //-------------------------------------------------------------------------
    //  The Game Loop

    private void gameLoop() {
        try {
            try {
                // FIRST, handle any interrupts.
                if (!interrupts.isEmpty()) {
                    handleInterrupts(userInput);
                    userInput = null;
                    return;
                }

                // Do planning, based on current input. (Can throw interrupt.)
                if (userInput != null) {
                    Planner.doPlanning(userInput, region);
                }

                // Animate any visual effects
                Animator.doAnimate(region);

                // Execute any plans.  (Can throw interrupt.)
                Executor.doMovement(region);
            } catch (InterruptException ex) {
                interrupts.add(ex.get());
            }

            // FINALLY, repaint.
            userInput = null;
            viewer.repaint();

            gameTick++;
            if (gameTick % DEBUGGER_REFRESH_TICKS == 0 && debugger != null) {
                debugger.refresh();
            }
        } catch (Exception ex) {
            looper.stop();
            throw ex;
        }
    }

    private void handleInterrupts(UserInput input) {
        switch (interrupts.pop()) {
            case Interrupt.WaitForInput wait -> {
                if (input == null) {
                    interrupts.push(wait);
                }
            }

            case Interrupt.DisplaySign sign -> {
                viewer.displaySign(sign.signId());

                // Wait for click.
                interrupts.add(new Interrupt.WaitForInput());
            }

            case Interrupt.GoToRegion info -> gotoRegion(info);
        }
    }

    private void gotoRegion(Interrupt.GoToRegion info) {
        System.out.println("Go To region: " + info.exit());
        var regionName = info.exit().region();
        var pointName = info.exit().point();

        // FIRST, find the new region
        if (!regionFactories.containsKey(regionName)) {
            System.out.println("Unknown region: " + regionName);
            return;
        }
        Region newRegion = getRegion(info.exit().region());

        Optional<Entity> point = newRegion.query(Point.class)
            .filter(e -> e.point().name().equals(pointName))
            .findFirst();

        if (point.isEmpty()) {
            System.out.println("No such point in " + regionName + ": " + pointName);
            return;
        }

        // NEXT, remove the party from the old region and clear all active
        // plans
        for (Entity player : region.query(Player.class).toList()) {
            region.getEntities().remove(player.id());
        }
        region.query(Plan.class).forEach(e -> e.remove(Plan.class));


        // NEXT, add the party to the new region
        region = newRegion;

        region.getEntities().make().mobile("george")
            .put(new Player("George"))
            .cell(point.get().cell())
            .sprite(Sprites.ALL.getInfo("mobile.george"));

        viewer.setRegion(region);
    }

    //-------------------------------------------------------------------------
    // Region Definitions

    private void populateRegionFactories() {
        regionFactories.put("test",
            () -> new Region(getClass(),
                "assets/regions/test/test.region")
        );
        regionFactories.put("overworld",
            () -> new Region(getClass(),
                "assets/regions/overworld/overworld.region")
        );
        regionFactories.put("floobham",
            () -> new Region(getClass(),
                "assets/regions/floobham/floobham.region")
        );
    }

    private Region getRegion(String name) {
        Region region = regions.get(name);

        if (region == null) {
            region = regionFactories.get(name).get();
            regions.put(name, region);
            Sprites.ALL.add(region.getTerrainTileSet());
        }

        return region;
    }
}
