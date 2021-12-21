package com.wjduquette.george.widgets;

import com.wjduquette.george.App;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Debugger extends StackPane {
    //-------------------------------------------------------------------------
    // Instance Variables

    // The application
    private final App app;

    // The stage containing this scene.
    private final Stage stage;

    // The client's on-close handler
    private Runnable onClose = null;

    private Label counterLabel = new Label();
    int counter = 0;

    //-------------------------------------------------------------------------
    // Constructor

    /**
     * Creates the debugger for the application.
     * @param app The application
     */
    public Debugger(App app, GameView viewer) {
        this.app = app;

        counterLabel.setPrefHeight(300);
        counterLabel.setPrefWidth(300);
        getChildren().add(counterLabel);

        Scene scene = new Scene(this, 800, 600);
        stage = new Stage();
        stage.setTitle("George's Debugger");
        stage.setScene(scene);
        stage.setOnCloseRequest(evt -> onClose());
        stage.initOwner(viewer.getScene().getWindow());
    }

    //-------------------------------------------------------------------------
    // Event Handlers

    private void onClose() {
        // TODO Notify owner.
        stage.hide();

        if (onClose != null) {
            onClose.run();
        }
    }

    //-------------------------------------------------------------------------
    // Public API

    public void show() {
        stage.show();
    }

    public void setOnClose(Runnable onClose) {
        this.onClose = onClose;
    }

    public void refresh() {
        ++counter;
        counterLabel.setText("Refresh #" + counter);
    }
}
