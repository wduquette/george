package com.wjduquette.george.widgets;

import com.wjduquette.george.App;
import com.wjduquette.george.Stevedore;
import com.wjduquette.george.ecs.Entity;
import com.wjduquette.george.model.ItemSlot;
import com.wjduquette.george.model.Region;
import com.wjduquette.george.model.Role;
import javafx.geometry.BoundingBox;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private ItemSlot selectedSlot = null;
    private final Map<ItemSlot,SlotBox> slot2box = new HashMap<>();

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

    private void onSelectSlot(SlotBox box) {
        selectedSlot = box.slot();
        repaint();
    }

    // Drops the select item (or 1 item, if they are stacked) onto the ground
    // near the player.
    private void onDropBackpackItem(SlotBox box) {
        if (box.slot() instanceof ItemSlot.Inventory slot) {
            var owner = region.get(slot.id());
            Stevedore.dropItem(region, owner, slot.index());
        }
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
        // Clear retained data
        slot2box.clear();

        // Fill the background
        fill(Color.DARKBLUE, 0, 0, getWidth(), getHeight());

        // The panel's usable area
        var px = MARGIN;                     // X origin of panel content
        var py = MARGIN;                     // Y origin of panel content
        var pw = getWidth() - 2*MARGIN;      // width of panel content
        var ph = getHeight() - 2*MARGIN;     // height of panel content
        var pbox = new BoundingBox(px, py, pw, ph);

        // Area origins
        var pcw = sprites().width() + 4;     // width of PC box column
        var bpx = px + pcw + COLUMN_GAP;    // X origin of backpack array
        var bpy = py + NORMAL_LEADING;       // Y origin of backpack array
        var bpw = bpx +                      // Width of backpack array
            (4 + sprites().width())*BACKPACK_COLS;
        var iox = bpw + COLUMN_GAP;          // X origin of item options
        var bbx = px + pw;                   // X origin of back button
        var bby = py + ph;                   // Y origin of back button

        // Draw components
        drawPCBox(px, py);
        drawBackpackArray(bpx, bpy);
        drawEquipmentSlots(iox + 200, bpy);
        drawItemOptions(iox, py);
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
        // FIRST, Draw title
        var ty = y - NORMAL_LEADING;
        gc().setFont(NORMAL_FONT);
        gc().setFill(Color.WHITE);
        gc().setTextBaseline(VPos.TOP);
        gc().fillText(player.label() + "'s Backpack", x, ty);

        // NEXT, Draw Backpack Slot array
        var boxes = getBackpackSlots();
        boxes.forEach(b -> slot2box.put(b.slot(), b));

        // draw the slots.
        drawSlots(x, y, 4, boxes, selectedSlot,
            this::onSelectSlot);
    }

    private void drawEquipmentSlots(double x, double y) {
        var ty = y - NORMAL_LEADING;
        gc().setFont(NORMAL_FONT);
        gc().setFill(Color.WHITE);
        gc().setTextBaseline(VPos.TOP);
        gc().fillText("Equipment", x, ty);

        var boxMap = getEquipmentSlots();
        boxMap.values().forEach(b -> slot2box.put(b.slot(), b));

        var gap = 5;
        var bw = sprites().width() + 4 + gap;
        var bh = sprites().height() + 4 + gap;

        drawEquipSlot(boxMap.get(Role.HEAD), x + bw, y);
        drawEquipSlot(boxMap.get(Role.BODY), x + bw, y + bh);
        drawEquipSlot(boxMap.get(Role.FEET), x + bw, y + 2*bh);
        drawEquipSlot(boxMap.get(Role.HAND), x, y + 0.5*bh);
        drawEquipSlot(boxMap.get(Role.RANGED), x, y + 1.5*bh);
        drawEquipSlot(boxMap.get(Role.SHIELD), x + 2*bw, y + bh);
    }

    private void drawEquipSlot(SlotBox box, double x, double y) {
        if (box.slot() instanceof ItemSlot.Equipment slot) {
            String sprite;
            if (box.item() != null) {
                sprite = box.item().sprite();
            } else {
                sprite = switch (slot.role()) {
                    case HEAD -> "equip.helmet";
                    case BODY -> "equip.armor";
                    case FEET -> "equip.footwear";
                    case HAND -> "equip.weapon";
                    case RANGED -> "equip.bow";
                    case SHIELD -> "equip.shield";
                };
            }
            var bg = box.slot().equals(selectedSlot) ? Color.LIGHTGRAY : Color.DIMGRAY;
            var bounds = new BoundingBox(x, y,
                4 + sprites().width(), 4 + sprites().height());
            fill(Color.WHITE, bounds);
            fill(bg, x + 2, y + 2, sprites().width(), sprites().height());

            drawImage(sprites().get(sprite), x + 2, y + 2);

            target(bounds, () -> onSelectSlot(box));
        }
    }

    // Draws the item options for the selected item
    private void drawItemOptions(double x, double y) {
        if (selectedSlot == null ||
            slot2box.get(selectedSlot).item() == null)
        {
            drawText("Select an Item", x, y);
            return;
        }

        // The item name
        var ty = y;
        var box = slot2box.get(selectedSlot);

        drawText(box.item().label(), x, ty);
        ty += OPTION_LEADING;

        for (var action : box.actions()) {
            drawAction(action, x, ty);
            ty += OPTION_LEADING;
        }
    }

    private void drawAction(Action action, double x, double y) {
        Text text = new Text(action.label());

        text.setTextOrigin(VPos.TOP);
        text.setFill(action.isDisabled() ? Color.LIGHTGRAY : Color.YELLOW);
        text.setFont(NORMAL_FONT);

        if (!action.isDisabled()) {
            text.setOnMouseClicked(evt -> {
                action.perform();
                repaint();
            });
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

    // Gets a list of the player's backpack slot boxes
    private List<SlotBox> getBackpackSlots() {
        var list = new ArrayList<SlotBox>();

        for (int i = 0; i < player.inventory().size(); i++) {
            var itemSlot = new ItemSlot.Inventory(player.id(), i);
            list.add(Stevedore.getSlotBox(region, player, itemSlot));
        }

        return list;
    }

    // Gets a list of the player's equipment slot boxes
    private Map<Role,SlotBox> getEquipmentSlots() {
        var map = new HashMap<Role,SlotBox>();

        for (var role : Role.values()) {
            var itemSlot = new ItemSlot.Equipment(player.id(), role);
            map.put(role, Stevedore.getSlotBox(region, player, itemSlot));
        }

        return map;
    }
}
