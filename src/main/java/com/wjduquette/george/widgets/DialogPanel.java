package com.wjduquette.george.widgets;

import com.wjduquette.george.App;
import com.wjduquette.george.graphics.ImageUtils;
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
public class DialogPanel extends CanvasPane implements Panel {
    //-------------------------------------------------------------------------
    // Constants

    private final static double INSET = 50;
    private final static double NAME_SIZE = 24;
    private final static double NAME_SPACING = 30;
    private final static double DESCRIPTION_SIZE = 12;
    private final static double DESCRIPTION_SPACING = 26;
    private final static double MAIN_SIZE = 16;
    private final static double MAIN_SPACING = 20;
    private final static double RESPONSE_SIZE = 16;
    private final static double RESPONSE_SPACING = 30;

    //-------------------------------------------------------------------------
    // Instance Variables

    // The application
    private final App app;

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
        this.app = app;
        this.dialog = dialog;
        setPadding(new Insets(INSET));
        setOnResize(this::repaint);
    }

    //-------------------------------------------------------------------------
    // Panel API

    @Override public Node asNode() { return this; }
    @Override public void setOnClose(Runnable func) { this.onClose = func; }

    //-------------------------------------------------------------------------
    // Implementation

    // Paint the current state of the dialog.
    private void repaint() {
        // FIRST, Clear any old responses.
        getChildren().removeAll(widgets);

        var region = app.getCurrentRegion();
        var w = getWidth() - 2*INSET;
        var h = getHeight() - 2*INSET;

        // Fill the background
        gc().setFill(Color.DARKBLUE);
        gc().fillRect(0, 0, w, h);

        // Draw the entity's image
        var ix = 30;
        var iy = 30;
        var bgimage = app.sprites().get(dialog.backgroundSprite());
        var fgimage = app.sprites().get(dialog.foregroundSprite());
        gc().drawImage(ImageUtils.embiggen(bgimage, 2), ix, iy);
        gc().drawImage(ImageUtils.embiggen(fgimage, 2), ix, iy);

        // Draw the text.  tx and ty are in canvas coordinates
        var tx = 30 + 2*bgimage.getHeight() + 30;
        var ty = 30.0;
        gc().setTextBaseline(VPos.TOP);
        gc().setFill(Color.WHITE);

        // Name
        gc().setFont(Font.font("Helvetica", NAME_SIZE));
        gc().fillText(dialog.getName(), tx, ty);
        ty += NAME_SPACING;

        // Description
        gc().setFont(Font.font("Helvetica", DESCRIPTION_SIZE));
        var description = dialog.getDescription();

        if (description.isPresent()) {
            gc().fillText(description.get(), tx, ty);
            ty += DESCRIPTION_SPACING;
        }

        gc().setStroke(Color.WHITE);
        gc().setLineWidth(2);
        gc().strokeLine(tx, ty, w - 30, ty);

        ty += MAIN_SPACING;

        gc().setFont(Font.font("Helvetica", MAIN_SIZE));
        fillTextBlock(dialog.getDisplayText(), tx, ty, MAIN_SPACING);

        // Draw the responses
        var responses = dialog.getResponses();
        ty = h - 30 - 20*(1 + responses.size()) - 25;

        gc().setFill(Color.WHITE);
        gc().setFont(Font.font("Helvetica", RESPONSE_SIZE));
        gc().fillText("You respond:", tx, ty);
        ty += RESPONSE_SPACING;

        widgets.clear();
        for (var response : responses) {
            Text text = new Text(response.text());

            text.setTextOrigin(VPos.TOP);
            text.setFill(Color.YELLOW);
            text.setFont(Font.font("Helvetica", RESPONSE_SIZE));
            text.setX(INSET + tx + 20); // widget coordinates
            text.setY(INSET + ty);
            text.setOnMouseClicked(evt -> onResponse(response));

            widgets.add(text);
            getChildren().add(text);

            ty += RESPONSE_SPACING;
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
