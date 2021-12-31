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

    //-------------------------------------------------------------------------
    // Instance Variables

    private Runnable onClose = null;
    private final Region region;
    private final Entity player;

    private ItemSlot selectedSlot = null;

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

    private void onSelectBackpackSlot(ItemSlot slot) {
        App.println("Clicked backpack slot: " + slot);
        selectedSlot = slot;
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

    // Transient repainting data
    private double px;    // The panel x origin
    private double py;    // The panel y origin
    private double pw;    // The panel width
    private double ph;    // The panel height

    private double cx;    // Character button origin
    private double cy;
    private double cw;    // Character button column width

    private double bx;    // Backpack origin
    private double by;

    protected void onRepaint() {
        // Fill the background
        fill(Color.DARKBLUE, 0, 0, getWidth(), getHeight());

        // The usable region
        px = MARGIN;
        py = MARGIN;
        pw = getWidth() - 2*MARGIN;
        ph = getHeight() - 2*MARGIN;

        // Area origins
        cx = px;
        cw = sprites().width() + 4; // sprite + frame width
        bx = cx + cw + COLUMN_GAP;          // character button column + spacing
        by = py + NORMAL_LEADING;   // Room for name header
        cy = by;

        // Draw components
        drawPlayerCharacterButtons();
        drawBackpack();
        drawBackButton();
    }

    private void drawPlayerCharacterButtons() {
        drawFramedSprites(toImage(player), null, cx, cy, 1);
        var box = new BoundingBox(cx, cy,
            sprites().width() + 4, sprites().height() + 4);
        target(box, () -> onClickPlayerCharacter(player));
    }

    private void drawBackpack() {
        // Draw title
        var tx = bx;
        var ty = by - NORMAL_LEADING;
        gc().setFont(NORMAL_FONT);
        gc().setFill(Color.WHITE);
        gc().setTextBaseline(VPos.TOP);
        gc().fillText(player.label().text() + "'s Backpack", tx, ty);

        // Draw Backpack slots
        drawSlots(bx, by, 4, getBackpackSlots(), selectedSlot,
            this::onSelectBackpackSlot);
    }

    private void drawBackButton() {
        // Draw the "Back" button
        Text text = new Text("Back");

        text.setTextOrigin(VPos.BOTTOM);
        text.setFill(Color.YELLOW);
        text.setFont(TITLE_FONT);
        text.setOnMouseClicked(evt -> onClose());

        place(text, px + pw - text.getLayoutBounds().getWidth(), py + ph);
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
