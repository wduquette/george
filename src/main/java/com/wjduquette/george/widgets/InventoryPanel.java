package com.wjduquette.george.widgets;

import com.wjduquette.george.App;
import com.wjduquette.george.ecs.Entity;
import com.wjduquette.george.ecs.Player;
import com.wjduquette.george.model.Region;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;


/**
 * A panel for talking to NPCs.
 */
public class InventoryPanel extends StackPane implements Panel {
    //-------------------------------------------------------------------------
    // Constants

    private final static double MARGIN = 0;
    private final static double INSET = 30;

    //-------------------------------------------------------------------------
    // Instance Variables

    // The application
    private final App app;

    // The onClose handler
    private Runnable onClose = null;

    // The region and player
    private final Region region;
    private Entity player;

    //-------------------------------------------------------------------------
    // Constructor

    /**
     * Creates a panel to display player inventory.
     * @param app The application
     */
    public InventoryPanel(App app) {
        this.app = app;
        this.region = app.getCurrentRegion();

        // NEXT, get the player
        var player = region.query(Player.class).findFirst().orElseThrow();

        // FIRST, configure the panel itself

        // NEXT, manage the content
        GridPane grid = new GridPane();
        StackPane.setMargin(grid, new Insets(MARGIN));
        grid.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);
        grid.setPadding(new Insets(INSET));
        grid.setHgap(15);
        grid.setVgap(10);
        grid.setBackground(new Background(new BackgroundFill(Color.DARKBLUE, null, null)));

        // NEXT, add the Player Character button bar
        var pcButtonBar = new HBox();
        GridPane.setVgrow(pcButtonBar, Priority.ALWAYS);
        grid.add(pcButtonBar, 0, 0);

        Button playerBtn = entityButton(player, () -> {
            App.println("Howdy!");
        });
        pcButtonBar.getChildren().add(playerBtn);

        // NEXT, add the player's backpack area.
        GridPane backpack = new GridPane();
        GridPane.setVgrow(backpack, Priority.ALWAYS);
        GridPane.setHgrow(backpack, Priority.ALWAYS);
        backpack.setHgap(5);
        backpack.setVgap(5);
        grid.add(backpack, 1, 0);

        Label backpackTitle = label("Backpack");
        backpack.add(backpackTitle, 0, 0, 5, 1);

        for (int c = 0; c < 5; c++) {
            for (int r = 0; r < 3; r++) {
                var btn = emptyButton();
                backpack.add(btn, c, r + 1);
            }
        }

        // NEXT, add the back button
        Button backBtn = textButton("Back", this::onClose);
        GridPane.setConstraints(backBtn, 1, 1, 1, 1, HPos.RIGHT, VPos.BOTTOM);
        grid.getChildren().add(backBtn);

        getChildren().add(grid);

        // NEXT, popdown on Escape.
        grid.addEventFilter(KeyEvent.KEY_PRESSED, this::onKeyPress);

        // NEXT, get the keyboard focus for key commands.
        Platform.runLater(grid::requestFocus);
    }

    //-------------------------------------------------------------------------
    // Event Handling

    // Closes the panel.
    private void onClose() {
        onClose.run();
    }

    // Handle key-presses
    private void onKeyPress(KeyEvent evt) {
        // Close window on Escape
        if (evt.getCode() == KeyCode.ESCAPE) {
            onClose();
        }
    }



    //-------------------------------------------------------------------------
    // Panel API

    @Override public Node asNode() { return this; }
    @Override public void setOnClose(Runnable func) { this.onClose = func; }

    //-------------------------------------------------------------------------
    // Widget helpers

    private Label label(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("Helvetica", 16));
        label.setTextFill(Color.WHITE);
        return label;
    }

    private Button textButton(String text, Runnable runnable) {
        Button btn = new Button(text);

        btn.setFont(Font.font("Helvetica", 20));
        btn.setTextFill(Color.YELLOW);
        btn.setBackground(Background.EMPTY);
        btn.setOnAction(evt -> runnable.run());

        return btn;
    }

    private Button entityButton(Entity entity, Runnable runnable) {
        Button btn = new Button();
        var img = app.sprites().get(entity.sprite().name());
        btn.setGraphic(new ImageView(img));
        btn.setBackground(new Background(
            new BackgroundFill(Color.YELLOW, null, null),
            new BackgroundFill(Color.CYAN, null, new Insets(4))));
        btn.setOnAction(evt -> runnable.run());

        return btn;
    }

    private Button emptyButton() {
        Button btn = new Button();
        var label = new Label();
        label.setMinWidth(40);
        label.setMinHeight(40);

        btn.setGraphic(label);
        btn.setBackground(new Background(
            new BackgroundFill(Color.WHITE, null, null),
            new BackgroundFill(Color.DARKGRAY, null, new Insets(2))));
        return btn;
    }
}
