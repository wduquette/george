package com.wjduquette.george;

import com.wjduquette.george.graphics.SpriteSet;
import com.wjduquette.george.model.*;
import com.wjduquette.george.ecs.*;
import com.wjduquette.george.regions.FloobhamRegion;
import com.wjduquette.george.regions.OverworldRegion;
import com.wjduquette.george.util.Looper;
import com.wjduquette.george.util.RandomPlus;
import com.wjduquette.george.widgets.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.*;
import java.util.function.Supplier;

public class App extends Application {
    //-------------------------------------------------------------------------
    // Constants

    /** RNG for the game. */
    public static final RandomPlus RANDOM = new RandomPlus();

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

    // The items lookup table
    private Items items;

    // A lookup table for regions by name
    private final Map<String,Region> regions = new HashMap<>();

    // The map we're currently wandering about on.
    private Region region = null;

    // The most recent user input.
    private UserInput userInput = null;

    // The Interrupt stack
    private final Stack<Interrupt> interrupts = new Stack<>();

    // The debugger, or null if not shown.
    private static Debugger debugger = null;

    // The game tick
    private long gameTick = 0;

    //-------------------------------------------------------------------------
    // Main Program

    @Override
    public void start(Stage stage) {
        // Set up global resources
        populateRegionFactories();
        items = new Items(getClass(), "assets/items.keydata");

        // TEMP
//        region = getRegion("test");
//        region = getRegion("floobham");
        region = getRegion("overworld");

        // TODO: add point-retrieval method
        Cell origin = region.query(Point.class)
            .filter(e -> e.point().name().equals("origin"))
            .map(Entity::cell)
            .findFirst()
            .orElse(new Cell(10, 10));

        Entity george = region.getEntities().make()
            .mobile("george") // Key
            .tagAsPlayer()
            .label("George")
            .cell(origin)
            .sprite(Sprites.ALL.getInfo("mobile.george"));

        viewer.setSprites(Sprites.ALL);
        viewer.addEventHandler(UserInputEvent.USER_INPUT, this::onUserInput);
        viewer.setRegion(region);

        // NEXT, configure the GUI
        Scene scene = new Scene(viewer, 800, 600);
        stage.setMinWidth(800);
        stage.setMinHeight(600);
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
        switch (event.getInput()) {
            case UserInput.ShowDebugger $ -> showDebugger();
            case UserInput.ShowMap $ -> showMap();
            default -> userInput = event.getInput();
        }
    }

    //-------------------------------------------------------------------------
    // Debugger API

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

    /**
     * Moves the party to the given cell in the current region.
     * @param cell The cell
     */
    public void doMagicMove(Cell cell) {
        // TODO: handler entire party, current movement capabilities.
        // TODO: Consider doing magic moves through planner.
        if (region.isWalkable(cell)) {
            region.query(Player.class)
                .findFirst()
                .ifPresent(p -> p.cell(cell));
        }
    }

    /**
     * Transfers the party to the region:point indicated by the given exit.
     * @param exit The exit.
     */
    public void doMagicTransfer(Exit exit) {
        gotoRegion(exit);
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
                Animator.doAnimate(gameTick, region);

                // Execute any plans.  (Can throw interrupt.)
                Executor.doMovement(region);

                // Monitor interactions and tripwires.  Could throw interrupt.
                Monitor.analyze(region);
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
            case Interrupt.GoToRegion info -> gotoRegion(info.exit());

            case Interrupt.Interact feature ->
                // At present, the only kind of interaction we support is
                // describing a feature.  So do that.
                showFeature(feature.id());
        }
    }

    // Transfer the party to the region:name indicated by the exit.
    private void gotoRegion(Exit exit) {
        System.out.println("Go To region: " + exit);
        var regionName = exit.region();
        var pointName = exit.point();

        // FIRST, find the new region
        if (!regionFactories.containsKey(regionName)) {
            System.out.println("Unknown region: " + regionName);
            return;
        }
        Region newRegion = getRegion(regionName);

        Optional<Entity> point = newRegion.query(Point.class)
            .filter(e -> e.point().name().equals(pointName))
            .findFirst();

        if (point.isEmpty()) {
            System.out.println("No such point in " + regionName + ": " + pointName);
            return;
        }

        // NEXT, clear all active plans.
        region.query(Plan.class).forEach(e -> e.remove(Plan.class));

        // NEXT, transfer the party and its belongings to the new region.
        // TODO: Handle multiple player characters properly
        // This will require special logic to position the follower(s).
        Entity player = region.query(Player.class).findFirst().orElseThrow();

        region.getEntities().remove(player.id());
        newRegion.getEntities().add(player);

        // Inventory
        var inventory = region.query(Owner.class)
            .filter(e -> e.owner().ownerId() == player.id())
            .toList();

        for (var item : inventory) {
            region.getEntities().remove(item.id());
            newRegion.getEntities().add(item);
        }

        // Position the player.
        player.cell(point.get().cell());

        // NEXT, Make the new region the current region.
        region = newRegion;
        viewer.setRegion(region);
    }

    /**
     * Describes a feature, based on what it is.  Supported features include
     * Signs and Mannikins.
     * @param id The feature entity's ID
     */
    public void showFeature(long id) {
        var entity = region.get(id);

        if (entity.sign() != null) {
            var key = entity.sign().key();
            var text = region.getInfo(key, "text");

            displayTextBlock(entity, text);
        } else if (entity.mannikin() != null) {
            var key = entity.mannikin().key();
            StringBuilder buff = new StringBuilder();
            buff.append(region.getInfo(key, "label")).append("\n\n");
            buff.append(region.getInfo(key, "description")).append("\n\n");

            List<String> greetings = region.info().values(key + ".greeting*");
            buff.append(RANDOM.pickFrom(greetings));

            displayTextBlock(entity, buff.toString());
        }
    }

    public void displayTextBlock(Entity entity, String text) {
        viewer.repaint();
        showPanel(new FeaturePanel(this, entity, text));
    }

    public void showMap() {
        viewer.repaint();
        showPanel(new MapPanel(this));
    }

    private void showPanel(Panel panel) {
        looper.stop();
        panel.setOnClose(() -> {
            viewer.getChildren().remove(panel);
            looper.run();
        });
        viewer.getChildren().add(panel.asNode());
    }

    //-------------------------------------------------------------------------
    // Region Definitions

    private void populateRegionFactories() {
        regionFactories.put("test",
            () -> new DataDrivenRegion(this, getClass(),
                "assets/regions/test/test.region")
        );
        regionFactories.put("overworld",
            () -> new OverworldRegion(this, getClass(),
                "assets/regions/overworld/overworld.region")
        );
        regionFactories.put("floobham",
            () -> new FloobhamRegion(this, getClass(),
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

    //-------------------------------------------------------------------------
    // Items

    /**
     * Gets the items table.
     * @return The table
     */
    public Items items() {
        return items;
    }

    public SpriteSet sprites() {
        return Sprites.ALL;
    }

    //-------------------------------------------------------------------------
    // Global Utilities

    /**
     * Prints to the current destination.
     * @param text The output text.
     */
    public static void println(String text) {
        if (debugger != null) {
            debugger.println(text);
        }

        System.out.println(text);
    }
}
