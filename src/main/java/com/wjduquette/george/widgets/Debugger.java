package com.wjduquette.george.widgets;

import com.wjduquette.george.App;
import com.wjduquette.george.ecs.Entity;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Debugger extends StackPane {
    //-------------------------------------------------------------------------
    // Instance Variables

    private final TabPane tabPane;

    private TableView<EntityProxy> entitiesView;
    private ObservableList<EntityProxy> entityList;
    private FilteredList<EntityProxy> filteredEntityList;
    private ContextMenu entityContextMenu;

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

        // Tabs
        tabPane = new TabPane();
        makeEntitiesTab();
        getChildren().add(tabPane);

        // NEXT, pop up the window.
        Scene scene = new Scene(this, 800, 600);
        stage = new Stage();
        stage.setTitle("George's Debugger");
        stage.setScene(scene);
        stage.setOnCloseRequest(evt -> onClose());
//        stage.initOwner(viewer.getScene().getWindow());
    }

    private void makeEntitiesTab() {
        Tab entitiesTab = new Tab();
        entitiesTab.setText("Entities");
        tabPane.getTabs().add(entitiesTab);

        // Toolbar
        ToolBar toolbar = new ToolBar();

        TextField entityFilter = new TextField();
        entityFilter.setPrefColumnCount(20);
        entityFilter.textProperty().addListener((p, o, n) ->
            filteredEntityList.setPredicate(ep -> doEntityFilter(ep, n)));
        toolbar.getItems().add(new Label("Filter"));
        toolbar.getItems().add(entityFilter);

        Button refresh = new Button("Refresh");
        refresh.setOnAction(evt -> refresh());

        toolbar.getItems().add(refresh);

        // EntitiesView
        entityList = FXCollections.observableArrayList();
        entitiesView = new TableView<>();
        entitiesView.setStyle("-fx-font-family: Menlo;");
        filteredEntityList = new FilteredList<>(entityList, ep -> doEntityFilter(ep, null));
        entitiesView.setItems(filteredEntityList);

        TableColumn<EntityProxy,String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        idColumn.setPrefWidth(80);

        TableColumn<EntityProxy,String> textColumn = new TableColumn<>("Detail");
        textColumn.setCellValueFactory(new PropertyValueFactory<>("text"));
        textColumn.setPrefWidth(2000);
        entitiesView.getColumns().add(idColumn);
        entitiesView.getColumns().add(textColumn);

        // entityContextMenu
        entityContextMenu = new ContextMenu();
        entityContextMenu.getItems().add(label("Dummy"));
        entityContextMenu.setOnShowing(evt -> populateEntityContextMenu());
        entitiesView.setContextMenu(entityContextMenu);

        // BorderPane
        BorderPane content = new BorderPane();
        content.setTop(toolbar);
        content.setCenter(entitiesView);

        entitiesTab.setContent(content);
    }

    private boolean doEntityFilter(EntityProxy proxy, String filterString) {
        if (filterString == null || filterString.isEmpty()) {
            return true;
        } else {
            return proxy.getText().contains(filterString);
        }
    }

    private void populateEntityContextMenu() {
        EntityProxy proxy = entitiesView.getSelectionModel().getSelectedItem();

        entityContextMenu.getItems().clear();

        if (proxy == null) {
            entityContextMenu.getItems().addAll(
                label("No selected entity")
            );
            return;
        }

        var entity = app.getCurrentRegion().get(Long.parseLong(proxy.id));

        entityContextMenu.getItems().addAll(
            label(proxy.getId()),
            separator(),
            menuItem("Go To Cell",
                with(entity.cell(), () -> app.doMagicMove(entity.cell())))
        );
    }

    private MenuItem label(String label) {
        return menuItem(label, null);
    }

    private MenuItem menuItem(String label, Runnable action) {
        MenuItem item = new MenuItem(label);
        item.setMnemonicParsing(false);
        item.setDisable(action == null);

        if (action != null) {
            item.setOnAction(evt -> action.run());
        }

        return item;
    }

    private MenuItem separator() {
        return new SeparatorMenuItem();
    }

    private <T> T with(Object o, T value) {
        return o != null ? value : null;
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

        var proxy = entitiesView.getSelectionModel().getSelectedItem();
        entityList.clear();
        for (long id : region.getEntities().ids()) {
            var newProxy = new EntityProxy(region.get(id));
            entityList.add(newProxy);
            if (proxy != null && proxy.id.equals(newProxy.id)) {
                entitiesView.getSelectionModel().select(newProxy);
            }
        }
    }

    //-------------------------------------------------------------------------
    // Helper Classes

    public static class EntityProxy {
        private final String id;
        private final String text;

        EntityProxy(Entity entity) {
            this.id = String.format("%04d", entity.id());
            this.text = entity.componentString().replaceAll("\\s+", " ");
        }

        public String getId() { return id; }
        public String getText() { return text; }
    }
}
