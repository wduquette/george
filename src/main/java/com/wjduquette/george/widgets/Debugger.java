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

public class Debugger extends StackPane {
    //-------------------------------------------------------------------------
    // Instance Variables

    private final SplitPane mainSplit;
    private final TabPane tabPane;
    private final TextArea outputLog;

    // Entities Pane
    private TableView<EntityProxy> entitiesView;
    private ObservableList<EntityProxy> entityList;
    private FilteredList<EntityProxy> filteredEntityList;
    private MenuButton gotoMenu;
    private Label playerCellLabel;
    private ContextMenu entityContextMenu;

    // Inventories Pane
    private TableView<ItemProxy> itemView;
    private ObservableList<ItemProxy> itemList;
    private FilteredList<ItemProxy> filteredInventoryList;

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
        makeItemsTab();

        // Output Log
        outputLog = new TextArea();
        outputLog.setEditable(false);
        outputLog.setFont(Font.font("Menlo", 14));

        // main split
        mainSplit = new SplitPane();
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
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        idColumn.setPrefWidth(80);

        TableColumn<EntityProxy,String> cellColumn = new TableColumn<>("Cell");
        cellColumn.setCellValueFactory(new PropertyValueFactory<>("cell"));
        cellColumn.setPrefWidth(80);

        TableColumn<EntityProxy,String> textColumn = new TableColumn<>("Detail");
        textColumn.setCellValueFactory(new PropertyValueFactory<>("text"));
        textColumn.setPrefWidth(2000);
        entitiesView.getColumns().add(idColumn);
        entitiesView.getColumns().add(cellColumn);
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
                    menuItem(label, () -> app.doMagicMove(e.cell())));
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

        var entity = app.getCurrentRegion().get(Long.parseLong(proxy.id));

        entityContextMenu.getItems().addAll(
            label(proxy.getId()),
            separator(),
            menuItem("Go To Cell",
                with(entity.cell(), () -> app.doMagicMove(entity.cell())))
        );
    }

    //-------------------------------------------------------------------------
    // Items Tab

    private void makeItemsTab() {
        Tab tab = new Tab();
        tab.setText("Items");
        tabPane.getTabs().add(tab);

        // Toolbar
        ToolBar toolbar = new ToolBar();

        TextField filter = new TextField();
        filter.setPrefColumnCount(20);
        filter.textProperty().addListener((p, o, n) ->
            filteredInventoryList.setPredicate(ep -> doInventoryFilter(ep, n)));
        toolbar.getItems().add(new Label("Filter"));
        toolbar.getItems().add(filter);

        // Inventory View
        itemList = FXCollections.observableArrayList();
        itemView = new TableView<>();
        itemView.setStyle("-fx-font-family: Menlo;");
        filteredInventoryList = new FilteredList<>(itemList,
            ip -> doInventoryFilter(ip, null));
        itemView.setItems(filteredInventoryList);

        TableColumn<ItemProxy,String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        idColumn.setPrefWidth(80);

        TableColumn<ItemProxy,String> keyColumn = new TableColumn<>("Key");
        keyColumn.setCellValueFactory(new PropertyValueFactory<>("key"));
        keyColumn.setPrefWidth(120);

        TableColumn<ItemProxy,String> ownerColumn = new TableColumn<>("Owner");
        ownerColumn.setCellValueFactory(new PropertyValueFactory<>("owner"));
        ownerColumn.setPrefWidth(120);

        TableColumn<ItemProxy,String> textColumn = new TableColumn<>("Detail");
        textColumn.setCellValueFactory(new PropertyValueFactory<>("text"));
        textColumn.setPrefWidth(2000);
        itemView.getColumns().add(idColumn);
        itemView.getColumns().add(keyColumn);
        itemView.getColumns().add(ownerColumn);
        itemView.getColumns().add(textColumn);

        // BorderPane
        BorderPane content = new BorderPane();
        content.setTop(toolbar);
        content.setCenter(itemView);

        tab.setContent(content);
    }

    private boolean doInventoryFilter(ItemProxy proxy, String filterString) {
        if (filterString == null || filterString.isEmpty()) {
            return true;
        } else {
            return proxy.getText().contains(filterString);
        }
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

        // Display George's location
        var player = region.query(Player.class).findFirst().orElseThrow();
        playerCellLabel.setText(player.cell().displayString());

        // Populate the entities table
        var selectedEntity = entitiesView.getSelectionModel().getSelectedItem();
        var selectedItem = itemView.getSelectionModel().getSelectedItem();
        entityList.clear();
        itemList.clear();

        for (long id : region.getEntities().ids()) {
            var entity = region.get(id);
            var newProxy = new EntityProxy(entity);
            entityList.add(newProxy);
            if (selectedEntity != null && selectedEntity.id.equals(newProxy.id)) {
                entitiesView.getSelectionModel().select(newProxy);
            }

            if (entity.item() != null) {
                var ip = new ItemProxy(entity);
                itemList.add(ip);
                if (selectedItem != null &&
                    selectedItem.item.id() == entity.id())
                {
                    itemView.getSelectionModel().select(ip);
                }
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
        private final String id;
        private final String text;
        private final String cell;

        EntityProxy(Entity entity) {
            this.id = String.format("%04d", entity.id());
            this.text = entity.componentString().replaceAll("\\s+", " ");
            this.cell = entity.loc() != null
                ? entity.loc().cell().displayString()
                : "--,--";
        }

        public String getId() { return id; }
        public String getText() { return text; }
        public String getCell() { return cell; }
    }

    public class ItemProxy {
        private final Entity item;

        ItemProxy(Entity item) {
            this.item = item;
        }

        public String getId() {
            return String.format("%04d", item.id());
        }

        public String getKey() {
            return item.item().key();
        }

        public String getOwner() {
            if (item.owner() != null) {
                var id = item.owner().ownerId();
                return id + " " + app.getCurrentRegion().get(id).label().text();
            } else {
                return item.cell().displayString();
            }
        }

        public String getText() {
            return item.componentString().replaceAll("\\s+", " ");
        }
    }
}
