package com.wjduquette.george.widgets;

import com.wjduquette.george.App;
import com.wjduquette.george.ecs.Entity;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

/**
 * FeaturePanel implements a basic sign or other feature description
 * panel.
 */
public class FeaturePanel extends GamePane implements Panel {
    private final static double INSET = 50;

    private final Entity entity;
    private final String text;
    private Runnable onClose = null;

    public FeaturePanel(App app, Entity entity, String text) {
        super(app);
        this.entity = entity;
        this.text = text;
        setPadding(new Insets(INSET));
    }

    @Override public Node asNode() { return this; }
    @Override public void setOnClose(Runnable func) { this.onClose = func; }

    protected void onMouseClick(MouseEvent evt) {
        onClose.run();
    }

    protected void onKeyPress(KeyEvent evt) {
        if (evt.getCode() == KeyCode.ESCAPE) {
            onClose.run();
        }
    }

    protected void onRepaint() {
        var region = app().getCurrentRegion();
        var w = getWidth() - 2*INSET;
        var h = getHeight() - 2*INSET;

        // Fill the background
        fill(Color.DARKBLUE, 0, 0, w, h);

        // Draw the entity's image
        var ix = 30;
        var iy = 30;

        drawFramedEntity(entity, ix, iy, 2);

        // Draw the text
        var tx = 30 + 2*sprites().width() + 30;
        gc().setTextBaseline(VPos.TOP);
        gc().setFill(Color.WHITE);
        gc().setFont(NORMAL_FONT);
        fillTextBlock(text, tx, 30, 20);

        // Draw the "Click to continue..."
        gc().setFill(Color.YELLOW);
        gc().setFont(NORMAL_FONT);
        gc().setTextBaseline(VPos.BASELINE);
        gc().fillText("Click to continue...", tx, h - 50);
    }
}
