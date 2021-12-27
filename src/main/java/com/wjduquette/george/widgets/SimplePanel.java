package com.wjduquette.george.widgets;

import com.wjduquette.george.App;
import com.wjduquette.george.GameView;
import com.wjduquette.george.ecs.Entity;
import com.wjduquette.george.ecs.Sprite;
import com.wjduquette.george.graphics.ImageUtils;
import com.wjduquette.george.model.Region;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * SimplePanel implements a basic sign or mannikin panel.
 */
public class SimplePanel extends CanvasPane {
    private final static double INSET = 50;
    private final GameView main;
    private final Entity entity;
    private final String text;
    private Runnable onClose = null;

    public SimplePanel(GameView main, Entity entity, String text) {
        this.main = main;
        this.entity = entity;
        this.text = text;
        setPadding(new Insets(INSET));
        setOnResize(this::repaint);
        setOnMouseClicked(this::onMouseClick);
    }

    public void setOnClose(Runnable onClose) {
        this.onClose = onClose;
    }

    private void onMouseClick(MouseEvent evt) {
        Point2D mouse = ofMouse(evt);
        App.println("SimplePanel: " + mouse);

        if (onClose != null) {
            onClose.run();
        }
    }

    private void repaint() {
        var region = main.getRegion();
        var w = getWidth() - 2*INSET;
        var h = getHeight() - 2*INSET;

        // Fill the background
        gc().setFill(Color.DARKBLUE);
        gc().fillRect(0, 0, w, h);

        // Draw the entity's image
        var ix = 30;
        var iy = 30;
        var terrain = region.getTerrain(entity.cell());
        var sprite = main.getSprites().get(entity.sprite().name());
        gc().drawImage(ImageUtils.embiggen(terrain.image(), 2), ix, iy);
        gc().drawImage(ImageUtils.embiggen(sprite, 2), ix, iy);

        // Draw the text
        gc().setTextBaseline(VPos.TOP);
        gc().setFill(Color.WHITE);
        gc().setFont(Font.font("Helvetica", 18));
        gc().fillText(text, 30 + 2*terrain.image().getHeight() + 30, 30);

        // Draw the "Click to continue..."
        gc().setFill(Color.WHITE);
        gc().setFont(Font.font("Helvetica", 14));
        gc().setTextBaseline(VPos.BASELINE);
        gc().fillText("Click to continue...", 30, h - 30);
    }

    private Image img(Sprite sprite) {
        return main.getSprites().get(sprite.name());
    }
}
