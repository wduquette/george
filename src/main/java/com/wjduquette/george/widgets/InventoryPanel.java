package com.wjduquette.george.widgets;

import com.wjduquette.george.App;
import com.wjduquette.george.ecs.Entity;
import com.wjduquette.george.ecs.Owner;
import com.wjduquette.george.ecs.Player;
import com.wjduquette.george.model.Region;
import javafx.geometry.BoundingBox;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.util.List;

public class InventoryPanel extends GamePane implements Panel {
    private static final double MARGIN = 30;
    private static final double COLUMN_GAP = 20;

    //-------------------------------------------------------------------------
    // Instance Variables

    private Runnable onClose = null;
    private final Region region;

    //-------------------------------------------------------------------------
    // Constructor

    public InventoryPanel(App app) {
        super(app);

        this.region = app().getCurrentRegion();
    }

    //-------------------------------------------------------------------------
    // Event Handling

    private void onClose() { onClose.run(); }

    private void onClickPlayerCharacter(Entity pc) {
        App.println("Clicked PC: " + pc);
    }

    private void onClickBackpack(int index) {
        App.println("Clicked package slot: " + index);
    }

    //-------------------------------------------------------------------------
    // Panel API

    @Override public Node asNode() { return this; }
    @Override public void setOnClose(Runnable func) { this.onClose = func; }

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

    Entity player;        // The player

    protected void onRepaint() {
        // TODO: This will change when we have a party.
        player = region.query(Player.class).findFirst().orElseThrow();

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
        // TODO Items need a slot location in the inventory.
        List<Entity> items = region.query(Owner.class)
            .filter(e -> e.owner().ownerId() == player.id())
            .toList();

        var iw = sprites().width() + 4;
        var ih = sprites().height() + 4;

        for (int r = 0; r < 4; r++) {
            var iy = by + r*ih;
            for (int c = 0; c < 5; c++) {
                var index = r*5 + c;
                var ix = bx + c*iw;

                if (index < items.size()) {
                    drawItemBox(index, items.get(index), ix, iy);
                } else {
                    drawItemBox(index, null, ix, iy);
                }
            }
        }
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

    private void drawItemBox(int index, Entity entity, double x, double y) {
        var border = 2;
        var ix = x + border;
        var iy = y + border;
        var iw = sprites().width();
        var ih = sprites().height();
        var w = iw + 2*border;
        var h = ih + 2*border;

        var box = new BoundingBox(x, y, w, h);
        target(box, () -> onClickBackpack(index));

        fill(Color.WHITE, box);
        fill(Color.LIGHTGRAY, ix, iy, iw, ih);

        if (entity != null) {
            drawImage(toImage(entity), ix, iy);
        }
    }

}
