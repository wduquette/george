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
import javafx.scene.layout.StackPane;
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

    // The hull widget
    private final StackPane hull = new StackPane();

    // The main GUI pane
    private GameView viewer;

    // The log pane
    private LogPane logPane;

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

    // The Party!
    private Entity george;

    //-------------------------------------------------------------------------
    // Main Program

    @Override
    public void start(Stage stage) {
        // FIRST, Set up global resources
        populateRegionFactories();
        items = new Items(getClass(), "assets/items.keydata");

        // NEXT, Create the player(s)
        george = makeGeorge();

        // TEMP

//        region = getRegion("test");
//        region = getRegion("floobham");
        region = getRegion("overworld");

        // Put George in the region
        Cell origin = region.point("origin").orElse(new Cell(10, 10));
        region.entities().add(george.cell(origin));

        // NEXT, initialize the GUI
        viewer = new GameView(this);
        viewer.addEventHandler(UserInputEvent.USER_INPUT, this::onUserInput);
        viewer.setRegion(region);

        logPane = new LogPane(this);

        hull.getChildren().add(viewer);
        hull.getChildren().add(logPane);

        // NEXT, configure the GUI
        Scene scene = new Scene(hull, 800, 600);
        stage.setMinWidth(800);
        stage.setMinHeight(600);
        stage.setTitle("George's Saga!");
        stage.setScene(scene);
        stage.show();
        stage.setOnCloseRequest(evt -> System.exit(0));

        Platform.runLater(looper::run);
    }

    // Creates George as of the beginning of the game.
    private Entity makeGeorge() {
        Player player = new Player("George");
        player.setHitPoints(10, 10);
        var inv = new Inventory(Player.INVENTORY_SIZE);
        inv.add(items().make("vial.healing"));
        inv.add(items().make("vial.healing"));
        inv.add(items().make("scroll.mapping"));
        inv.add(items().make("weapon.small_wrench"));

        return new Entity()
            .player(player)
            .mobile("george") // Key
            .put(inv)
            .sprite(Sprites.ALL.getInfo("mobile.george"));
    }

    //-------------------------------------------------------------------------
    // Click Handling

    // Handle cells clicks
    private void onUserInput(UserInputEvent event) {
        switch (event.getInput()) {
            case UserInput.ShowDebugger $ -> showDebugger();
            case UserInput.ShowInventory $ -> showInventory();
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

    //-------------------------------------------------------------------------
    // Movers

    /**
     * Returns the party leader.
     * @return The leader
     */
    public Entity leader() {
        return george;
    }

    //-------------------------------------------------------------------------
    // Interrupts and Panels

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

        region.entities().remove(player.id());
        newRegion.entities().add(player);

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

            showPanel(new FeaturePanel(this, entity, text));
        } else if (entity.mannikin() != null) {
            region.findDialog(entity.id()).ifPresent(dlg ->
                showPanel(new DialogPanel(this, dlg)));
        }
    }

    /**
     * Shows the inventory panel for the current leader.
     */
    public void showInventory() {
        showPanel(new InventoryPanel(this, leader()));
    }

    /**
     * Displays the map panel, showing that part of the map the player has
     * seen.
     */
    public void showMap() {
        showPanel(new MapPanel(this));
    }

    private void showPanel(Panel panel) {
        looper.stop();
        panel.setOnClose(() -> {
            hull.getChildren().remove(panel.asNode());
            viewer.repaint();
            looper.run();
        });
        hull.getChildren().add(1, panel.asNode());
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

    public void log(String message) {
        logPane.log(message);
    }

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


    //-------------------------------------------------------------------------
    // Main

    public static void main(String[] args) {
        launch();
    }
}
