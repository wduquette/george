package com.wjduquette.george;

import com.wjduquette.george.model.Cell;
import com.wjduquette.george.model.Interrupt;
import com.wjduquette.george.model.Player;
import com.wjduquette.george.model.Region;
import com.wjduquette.george.ecs.*;
import com.wjduquette.george.util.Looper;
import com.wjduquette.george.widgets.UserInput;
import com.wjduquette.george.widgets.UserInputEvent;
import com.wjduquette.george.widgets.GameView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Stack;

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

    // The map we're currently wandering about on.
    private Region region = null;

    // The most recent user input.
    private UserInput userInput = null;

    // The Interrupt stack
    private Stack<Interrupt> interrupts = new Stack<>();

    //-------------------------------------------------------------------------
    // Main Program

    @Override
    public void start(Stage stage) {
        region = new Region(getClass(),
            "assets/regions/overworld/overworld.region");

        Cell origin = region.query(Point.class)
            .filter(e -> e.point().name().equals("origin"))
            .map(Entity::cell)
            .findFirst()
            .orElse(new Cell(10, 10));

        Player george = new Player("George");

        region.getEntities().make().mobile("george")
            .put(george)
            .cell(origin)
            .tile(TileSets.MOBILES.get("mobile.george"));

        // Dump the entities table
//        region.getEntities().dump();

        viewer.addEventHandler(UserInputEvent.USER_INPUT, this::onUserInput);
        viewer.setRegion(region);

        // NEXT, configure the GUI
        Scene scene = new Scene(viewer, 800, 600);
        stage.setTitle("George's Saga!");
        stage.setScene(scene);
        stage.show();

        Platform.runLater(() -> looper.run());
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
        // FIRST, handle any interrupts.
        if (!interrupts.isEmpty()) {
            handleInterrupts(userInput);
            userInput = null;
            return;
        }

        // Do planning, based on current input.
        if (userInput != null) {
            var interrupt = Planner.doPlanning(userInput, region);
            userInput = null;

            if (interrupt.isPresent()) {
                interrupts.add(interrupt.get());
                return;
            }
        }

        // Animate any visual effects
        Animator.doAnimate(region);

        // Execute any plans
        Executor.doMovement(region).ifPresent(i -> interrupts.add(i));

        // FINALLY, repaint.
        viewer.repaint();
    }

    private void handleInterrupts(UserInput input) {
        switch (interrupts.pop()) {
            case Interrupt.DisplaySign sign -> {
                var signName = region.get(sign.signId()).sign().name();
                System.out.println("The sign reads: " +
                    region.getString(signName));
            }
        }
    }
}
