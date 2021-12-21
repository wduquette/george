package com.wjduquette.george.widgets;

import com.wjduquette.george.App;
import com.wjduquette.george.ecs.Entity;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Callback;

public class Debugger extends BorderPane {
    //-------------------------------------------------------------------------
    // Instance Variables

    private TabPane tabPane;
    private Tab entitiesTab;
    private TableView<EntityProxy> entitiesView;

    // The application
    private final App app;

    // The stage containing this scene.
    private final Stage stage;

    // The client's on-close handler
    private Runnable onClose = null;

    //-------------------------------------------------------------------------
    // Constructor

    /**
     * Creates the debugger for the application.
     * @param app The application
     */
    public Debugger(App app, GameView viewer) {
        this.app = app;

        // FIRST, set up the GUI.

        // Toolbar
        ToolBar toolbar = new ToolBar();
        Button refresh = new Button("Refresh");
        refresh.setOnAction(evt -> refresh());
        toolbar.getItems().add(refresh);

        setTop(toolbar);

        // Tabs
        tabPane = new TabPane();
        makeEntitiesTab();
        setCenter(tabPane);

        // NEXT, pop up the window.
        Scene scene = new Scene(this, 800, 600);
        stage = new Stage();
        stage.setTitle("George's Debugger");
        stage.setScene(scene);
        stage.setOnCloseRequest(evt -> onClose());
        stage.initOwner(viewer.getScene().getWindow());
    }

    private void makeEntitiesTab() {
        entitiesTab = new Tab();
        entitiesTab.setText("Entities");
        tabPane.getTabs().add(entitiesTab);

        entitiesView = new TableView<>();
        entitiesView.setStyle("-fx-font-family: Menlo;");

        TableColumn<EntityProxy,String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        idColumn.setPrefWidth(80);

        TableColumn<EntityProxy,String> textColumn = new TableColumn<>("Detail");
        textColumn.setCellValueFactory(new PropertyValueFactory<>("text"));
        textColumn.setPrefWidth(600);
        entitiesView.getColumns().addAll(idColumn, textColumn);

        entitiesTab.setContent(entitiesView);
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
        refresh();
        stage.show();
    }

    public void setOnClose(Runnable onClose) {
        this.onClose = onClose;
    }

    public void refresh() {
        var region = app.getCurrentRegion();
        stage.setTitle("George's Debugger: " + region.prefix());

        entitiesView.getItems().clear();
        for (long id : region.getEntities().ids()) {
            entitiesView.getItems().add(new EntityProxy(region.get(id)));
        }
    }

    //-------------------------------------------------------------------------
    // Helper Classes

    public static class EntityProxy {
        private final String id;
        private final String text;

        EntityProxy(Entity entity) {
            this.id = String.format("%04d", entity.id());
            this.text = entity.toString().replaceAll("\\s+", " ");
        }

        public String getId() { return id.toString(); }
        public String getText() { return text; }
    }
}
