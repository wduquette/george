package com.wjduquette.george.widgets;

import com.wjduquette.george.App;
import com.wjduquette.george.model.Dialog;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * A panel for talking to NPCs.
 */
public class DialogPanel extends GamePane implements Panel {
    //-------------------------------------------------------------------------
    // Constants

    private final static double INSET = 50;
    private final static double DESCRIPTION_SPACING = 26;
    private final static double RESPONSE_LEADING = NORMAL_LEADING + 10;

    //-------------------------------------------------------------------------
    // Instance Variables

    // The data model
    private final Dialog dialog;

    // Interaction widgets
    private final List<Node> widgets = new ArrayList<>();

    // The onClose handler
    private Runnable onClose = null;

    //-------------------------------------------------------------------------
    // Constructor

    /**
     * Creates a panel to display the given dialog.
     * @param app The application
     * @param dialog The dialog
     */
    public DialogPanel(App app, Dialog dialog) {
        super(app);
        this.dialog = dialog;
        setPadding(new Insets(INSET));
    }

    //-------------------------------------------------------------------------
    // Panel API

    @Override public Node asNode() { return this; }
    @Override public void setOnClose(Runnable func) { this.onClose = func; }

    //-------------------------------------------------------------------------
    // Implementation

    // Paint the current state of the dialog.
    protected void onRepaint() {
        // FIRST, Clear any old responses.
        canvas().getChildren().removeAll(widgets);

        var region = app().getCurrentRegion();
        var w = getWidth() - 2*INSET;
        var h = getHeight() - 2*INSET;

        // Fill the background
        gc().setFill(Color.DARKBLUE);
        gc().fillRect(0, 0, w, h);

        // Draw the entity's image
        var ix = 30;
        var iy = 30;

        drawFramedSprites(
            toImage(dialog.foregroundSprite()),
            toImage(dialog.backgroundSprite()),
            ix, iy, 2);

        // Draw the text.  tx and ty are in canvas coordinates
        var tx = 30 + 2*sprites().height() + 30;
        var ty = 30.0;
        gc().setTextBaseline(VPos.TOP);
        gc().setFill(Color.WHITE);

        // Name
        gc().setFont(TITLE_FONT);
        gc().fillText(dialog.getName(), tx, ty);
        ty += TITLE_LEADING;

        // Description
        gc().setFont(SMALL_FONT);
        var description = dialog.getDescription();

        if (description.isPresent()) {
            gc().fillText(description.get(), tx, ty);
            ty += DESCRIPTION_SPACING;
        }

        gc().setStroke(Color.WHITE);
        gc().setLineWidth(2);
        gc().strokeLine(tx, ty, w - 30, ty);

        ty += NORMAL_LEADING;

        gc().setFont(NORMAL_FONT);
        fillTextBlock(dialog.getDisplayText(), tx, ty, NORMAL_LEADING);

        // Draw the responses
        var responses = dialog.getResponses();
        ty = h - 30 - 20*(1 + responses.size()) - 25;

        gc().setFill(Color.WHITE);
        gc().setFont(NORMAL_FONT);
        gc().fillText("You respond:", tx, ty);
        ty += RESPONSE_LEADING;

        widgets.clear();
        for (var response : responses) {
            Text text = new Text(response.text());

            text.setTextOrigin(VPos.TOP);
            text.setFill(Color.YELLOW);
            text.setFont(NORMAL_FONT);
            text.setX(tx + 20); // widget coordinates
            text.setY(ty);
            text.setOnMouseClicked(evt -> onResponse(response));

            widgets.add(text);
            canvas().getChildren().add(text);

            ty += RESPONSE_LEADING;
        }
    }

    // Handle the response, and close the panel when the dialog is complete.
    private void onResponse(Dialog.Response response) {
        dialog.respond(response);

        if (dialog.isComplete()) {
            onClose.run();
        } else {
            repaint();
        }
    }
}
