package com.wjduquette.george.widgets;

import com.wjduquette.george.App;
import com.wjduquette.george.GameView;
import com.wjduquette.george.ecs.Entity;
import com.wjduquette.george.ecs.Exit;
import com.wjduquette.george.ecs.Player;
import com.wjduquette.george.ecs.Point;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.HashSet;
import java.util.Set;

import static com.wjduquette.george.util.Combinator.with;

public class Debugger extends StackPane {
    //-------------------------------------------------------------------------
    // Instance Variables

    private final TabPane tabPane;
    private final TextArea outputLog;

    // Entities Pane
    private TableView<EntityProxy> entitiesView;
    private ObservableList<EntityProxy> entityList;
    private FilteredList<EntityProxy> filteredEntityList;
    private MenuButton gotoMenu;
    private Label playerCellLabel;
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

        // TabPane and Tabs
        tabPane = new TabPane();
        makeEntitiesTab();

        // Output Log
        outputLog = new TextArea();
        outputLog.setEditable(false);
        outputLog.setFont(Font.font("Menlo", 14));

        // main split
        SplitPane mainSplit = new SplitPane();
        mainSplit.setOrientation(Orientation.VERTICAL);
        mainSplit.getItems().add(tabPane);
        mainSplit.getItems().add(outputLog);
        mainSplit.setDividerPosition(0, 0.9);

        getChildren().add(mainSplit);

        // NEXT, pop up the window.
        Scene scene = new Scene(this, 800, 600);
        stage = new Stage();
        stage.setTitle("George's Debugger");
        stage.setScene(scene);
        stage.setOnCloseRequest(evt -> onClose());
    }

    //-------------------------------------------------------------------------
    // Entities Tab

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

        gotoMenu = new MenuButton("Go To");
        gotoMenu.getItems().add(label("dummy"));
        gotoMenu.setOnShowing(evt -> populateGotoMenu());
        toolbar.getItems().add(gotoMenu);

        Button refresh = new Button("Refresh");
        refresh.setOnAction(evt -> refresh());
        toolbar.getItems().add(refresh);

        Pane spacer1 = new Pane();
        HBox.setHgrow(spacer1, Priority.ALWAYS);
        toolbar.getItems().add(spacer1);

        playerCellLabel = new Label("(--,--)");
        playerCellLabel.setFont(Font.font("Menlo"));
        toolbar.getItems().add(playerCellLabel);

        // EntitiesView
        entityList = FXCollections.observableArrayList();
        entitiesView = new TableView<>();
        entitiesView.setStyle("-fx-font-family: Menlo;");
        filteredEntityList = new FilteredList<>(entityList, ep -> doEntityFilter(ep, null));
        entitiesView.setItems(filteredEntityList);

        TableColumn<EntityProxy,String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("idText"));
        idColumn.setPrefWidth(80);

        TableColumn<EntityProxy,String> labelColumn = new TableColumn<>("Label");
        labelColumn.setCellValueFactory(new PropertyValueFactory<>("label"));
        labelColumn.setPrefWidth(120);

        TableColumn<EntityProxy,String> placeColumn = new TableColumn<>("Place");
        placeColumn.setCellValueFactory(new PropertyValueFactory<>("place"));
        placeColumn.setPrefWidth(120);

        TableColumn<EntityProxy,String> textColumn = new TableColumn<>("Detail");
        textColumn.setCellValueFactory(new PropertyValueFactory<>("text"));
        textColumn.setPrefWidth(2000);
        entitiesView.getColumns().add(idColumn);
        entitiesView.getColumns().add(labelColumn);
        entitiesView.getColumns().add(placeColumn);
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

    private void populateGotoMenu() {
        gotoMenu.getItems().clear();
        Set<String> labels = new HashSet<>();

        for (Entity e : app.getCurrentRegion().query(Point.class).toList()) {
            var label = "here:" + e.point().name();

            if (!labels.contains(label)) {
                gotoMenu.getItems().add(
                    menuItem(label, () -> app.doMagicMove(e.loc())));
            }
            labels.add(label);
        }

        for (Entity e : app.getCurrentRegion().query(Exit.class).toList()) {
            var label = e.exit().region() + ":" + e.exit().point();
            if (!labels.contains(label)) {
                gotoMenu.getItems().add(
                    menuItem(label, () -> app.doMagicTransfer(e.exit())));
            }
            labels.add(label);
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

        var entity = proxy.entity;

        entityContextMenu.getItems().addAll(
            label(proxy.getIdText()),
            separator(),
            menuItem("Go To Cell",
                with(entity.loc(), () -> app.doMagicMove(entity.loc())))
        );
    }

    //-------------------------------------------------------------------------
    // Menu Helpers

    private MenuItem label(String label) {
        MenuItem item = new MenuItem(label);
        item.setMnemonicParsing(false);
        return item;
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

        // Display George's location
        var player = region.query(Player.class).findFirst().orElseThrow();
        playerCellLabel.setText(player.loc().displayString());

        // Populate the entities table
        var selectedEntity = entitiesView.getSelectionModel().getSelectedItem();
        entityList.clear();

        for (long id : region.entities().ids()) {
            var entity = region.get(id);
            var newProxy = new EntityProxy(entity);
            entityList.add(newProxy);
            if (selectedEntity != null &&
                selectedEntity.entity.id() == newProxy.entity.id()) {
                entitiesView.getSelectionModel().select(newProxy);
            }
        }
    }

    /**
     * Add a log message to the text area text.
     * @param text The message.
     */
    public void println(String text) {
        var newText = outputLog.getText() + text + "\n";
        outputLog.setText(newText);
        outputLog.setScrollTop(Double.MAX_VALUE);
    }

    //-------------------------------------------------------------------------
    // Helper Classes

    public static class EntityProxy {
        private final Entity entity;

        EntityProxy(Entity entity) {
            this.entity = entity;
        }

        public Long getId() {
            return entity.id();
        }

        public String getIdText() {
            return String.format("%04d", entity.id());
        }

        public String getLabel() {
            if (entity.has(com.wjduquette.george.ecs.Label.class) &&
                entity.label() != null)
            {
                return entity.label();
            } else {
                return "--";
            }
        }

        public String getPlace() {
            if (entity.loc() != null) {
                return entity.loc().displayString();
            } else {
                return "--";
            }
        }

        public String getText() {
            return entity.componentString().replaceAll("\\s+", " ");
        }
    }
}
