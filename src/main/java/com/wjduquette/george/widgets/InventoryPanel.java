package com.wjduquette.george.widgets;

import com.wjduquette.george.App;
import com.wjduquette.george.ecs.Entity;
import com.wjduquette.george.ecs.Inventory;
import com.wjduquette.george.model.ItemSlot;
import com.wjduquette.george.model.Region;
import javafx.geometry.BoundingBox;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;

public class InventoryPanel extends GamePane implements Panel {
    private static final double MARGIN = 30;
    private static final double COLUMN_GAP = 20;
    private static final double OPTION_LEADING = 24;
    private static final double BACKPACK_COLS = 4;

    //-------------------------------------------------------------------------
    // Instance Variables

    private Runnable onClose = null;
    private final Region region;
    private final Entity player;

    private SlotBox selectedSlot = null;

    //-------------------------------------------------------------------------
    // Constructor

    public InventoryPanel(App app, Entity player) {
        super(app);
        this.player = player;
        this.region = app().getCurrentRegion();
    }

    //-------------------------------------------------------------------------
    // Panel API

    @Override public Node asNode() { return this; }
    @Override public void setOnClose(Runnable func) { this.onClose = func; }

    //-------------------------------------------------------------------------
    // Event Handling

    private void onClose() { onClose.run(); }

    private void onClickPlayerCharacter(Entity pc) {
        App.println("Clicked PC: " + pc);
    }

    private void onSelectBackpackSlot(SlotBox box) {
        App.println("Clicked backpack slot: " + box.slot());
        selectedSlot = box;
        repaint();
    }

    //-------------------------------------------------------------------------
    // GamePane Framework

    /*
     +--------------------------------------------------------------------+
     | +---+  George's Backpack            George's Equipment
     | |PC1|  +---+ +---+ +---+ +---+
     | +---+  |itm| |itm| |itm| |itm| ...
     | +---+  +---+ +---+ +---+ +---+
     | |PC2|  +---+ +---+ +---+ +---+
     | +---+  |itm| |itm| |itm| |itm| ...
     |        +---+ +---+ +---+ +---+
     |        ...
     |
     |        Party Baggage
     |        +---+ +---+ +---+ +---+ +---+ +---+
     |        |itm| |itm| |itm| |itm| |itm| |itm| ...
     |        +---+ +---+ +---+ +---+ +---+ +---+
     |        ...
    */

    protected void onRepaint() {
        // Fill the background
        fill(Color.DARKBLUE, 0, 0, getWidth(), getHeight());

        // The panel's usable area
        var px = MARGIN;                     // X origin of panel content
        var py = MARGIN;                     // Y origin of panel content
        var pw = getWidth() - 2*MARGIN;      // width of panel content
        var ph = getHeight() - 2*MARGIN;     // height of panel content
        var pbox = new BoundingBox(px, py, pw, ph);

        // Area origins
        var pcx = px;                        // X origin of PC box column
        var pcw = sprites().width() + 4;     // width of PC box column
        var bpx = pcx + pcw + COLUMN_GAP;    // X origin of backpack array
        var bpy = py + NORMAL_LEADING;       // Y origin of backpack array
        var bpw = bpx +                      // Width of backpack array
            (4 + sprites().width())*BACKPACK_COLS;
        var pcy = bpy;                       // Y origin of PC box column
        var iox = bpw + COLUMN_GAP;          // X origin of item options
        var ioy = py;                        // Y origin of item options
        var bbx = px + pw;                   // X origin of back button
        var bby = py + ph;                   // Y origin of back button

        // Draw components
        drawPCBox(pcx, pcy);
        drawBackpackArray(bpx, bpy);
        drawItemOptions(iox, ioy);
        drawBackButton(bbx, bby);
    }

    // Draws the player character buttons in a column, starting at (x,y)
    private void drawPCBox(double x, double y) {
        drawFramedSprites(toImage(player), null, x, y, 1);
        var box = new BoundingBox(x, y,
            sprites().width() + 4, sprites().height() + 4);
        target(box, () -> onClickPlayerCharacter(player));
    }

    // Draws the array of backpack slots at (x,y)
    private void drawBackpackArray(double x, double y) {
        // Draw title
        var tx = x;
        var ty = y - NORMAL_LEADING;
        gc().setFont(NORMAL_FONT);
        gc().setFill(Color.WHITE);
        gc().setTextBaseline(VPos.TOP);
        gc().fillText(player.label().text() + "'s Backpack", tx, ty);

        // Draw Backpack slots
        drawSlots(x, y, 4, getBackpackSlots(), selectedSlot,
            this::onSelectBackpackSlot);
    }

    // Draws the item options for the selected item
    private void drawItemOptions(double x, double y) {
        if (selectedSlot == null ||
            selectedSlot.item() == null)
        {
            drawText("Select an Item", x, y);
            return;
        }

        // The item name
        var tx = x;
        var ty = y;

        drawText(selectedSlot.item().label().text(), tx, ty);
        ty += OPTION_LEADING;

        for (var action : selectedSlot.actions()) {
            drawAction(action, tx, ty);
            ty += OPTION_LEADING;
        }
    }

    private void drawAction(Action action, double x, double y) {
        Text text = new Text(action.label());

        text.setTextOrigin(VPos.TOP);
        text.setFill(action.isDisabled() ? Color.LIGHTGRAY : Color.YELLOW);
        text.setFont(NORMAL_FONT);

        if (!action.isDisabled()) {
            text.setOnMouseClicked(evt -> action.perform());
        }

        place(text, x, y);

    }

    // Draws the back button with its lower right corner at (x,y)
    private void drawBackButton(double x, double y) {
        // Draw the "Back" button
        Text text = new Text("Back");

        text.setTextOrigin(VPos.BOTTOM);
        text.setFill(Color.YELLOW);
        text.setFont(TITLE_FONT);
        text.setOnMouseClicked(evt -> onClose());

        place(text, x - text.getLayoutBounds().getWidth(), y);
    }

    // Draws a text string with the normal font and leading, using the given
    // color.  The origin is the top left.
    private void drawText(String text, double x, double y) {
        gc().setFont(NORMAL_FONT);
        gc().setFill(Color.WHITE);
        gc().setTextBaseline(VPos.TOP);
        gc().fillText(text, x, y);
    }

    //-------------------------------------------------------------------------
    // Slot Management

    List<SlotBox> getBackpackSlots() {
        var list = new ArrayList<SlotBox>();
        var inv = player.inventory();

        for (int i = 0; i < inv.size(); i++) {
            var slot = inv.get(i);
            var itemSlot = new ItemSlot.Inventory(player.id(), i);

            SlotBox box;
            if (slot == Inventory.EMPTY) {
                box = new SlotBox(itemSlot, 0, null);
            } else {
                box = new SlotBox(itemSlot, slot.count(), slot.entity());

                box.actions().add(new Action("Drop",
                    () -> App.println("TODO: Drop!")));
            }

            list.add(box);
        }

        return list;
    }
}
