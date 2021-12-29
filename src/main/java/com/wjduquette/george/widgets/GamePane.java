package com.wjduquette.george.widgets;

import com.wjduquette.george.App;
import com.wjduquette.george.graphics.SpriteSet;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;

/**
 * A canvas pane extended with tools related to the game and its data.
 * This is used for the main game view and a variety of other panels.
 */
public abstract class GamePane extends StackPane {
    //-------------------------------------------------------------------------
    // Instance Variables

    // The application
    private final App app;

    // The canvas pane, for content.
    private final CanvasPane canvas;

    //-------------------------------------------------------------------------
    // Constructor

    /**
     * Creates the new pane.
     * @param app The application object.
     */
    public GamePane(App app) {
        this.app = app;
        this.canvas = new CanvasPane();

        canvas.setOnResize(this::repaint);
        getChildren().add(canvas);
    }

    //-------------------------------------------------------------------------
    // Event Handlers

    //-------------------------------------------------------------------------
    // Protected API

    // Components
    protected App             app()     { return app; }
    protected CanvasPane      canvas()  { return canvas; }
    protected GraphicsContext gc()      { return canvas.gc(); }
    protected SpriteSet       sprites() { return app.sprites(); }

    /**
     * Subclasses must override to provide content.
     */
    abstract protected void onRepaint();

    //-------------------------------------------------------------------------
    // Public API

    public void repaint() {
        // FIRST, repaint the content
        onRepaint();

        // NEXT, request the keyboard focus.
        canvas.requestFocus();
    }
}
