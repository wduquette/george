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

    private final static double MARGIN = 50;
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
        GridPane grid = new GridPane();
        StackPane.setMargin(grid, new Insets(MARGIN));
        grid.setPadding(new Insets(INSET));
        grid.setBackground(new Background(new BackgroundFill(Color.DARKBLUE, null, null)));

        // NEXT, add the Player Character button bar
        var pcButtonBar = new HBox();
        GridPane.setConstraints(pcButtonBar, 0, 0);
        grid.getChildren().add(pcButtonBar);

        Button playerBtn = entityButton(player, () -> App.println(("Howdy!")));
        pcButtonBar.getChildren().add(playerBtn);

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

    private Button textButton(String text, Runnable runnable) {
        Button btn = new Button(text);

        btn.setFont(Font.font("Helvetica", 16));
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
}
