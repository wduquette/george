package com.wjduquette.george;

import com.wjduquette.george.model.*;
import com.wjduquette.george.ecs.*;
import com.wjduquette.george.util.Looper;
import com.wjduquette.george.widgets.UserInput;
import com.wjduquette.george.widgets.UserInputEvent;
import com.wjduquette.george.widgets.GameView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.function.Supplier;

public class App extends Application {
    //-------------------------------------------------------------------------
    // Constants

    // How often the game loop executes
    private final int LOOP_MSECS = 50;

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

    //-------------------------------------------------------------------------
    // Main Program

    @Override
    public void start(Stage stage) {
        populateRegionFactories();

        // TEMP
//        region = getRegion("test");
        region = getRegion("floobham");
//        region = getRegion("overworld");
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

        Platform.runLater(looper::run);
    }

    public static void main(String[] args) {
        launch();
    }

    //-------------------------------------------------------------------------
    // Click Handling

    // Handle cells clicks
    private void onUserInput(UserInputEvent event) {
        // FIRST, save the target cell.  It will be assessed by the
        // Planner on the next iteration of the GameLoop.
        userInput = event.getInput();
    }

    //-------------------------------------------------------------------------
    //  The Game Loop

    private void gameLoop() {
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
        System.out.println("Go To region: " + info.region());
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
