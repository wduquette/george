package com.wjduquette.george.widgets;

import javafx.scene.Node;

/**
 * A Panel is a UI widget that overlays the main game map.
 */
public interface Panel {
    /**
     * Specifies a function to call when the user closes the panel.
     * @param onClose The function
     */
    void setOnClose(Runnable onClose);

    /**
     * Returns the panel as a Node.
     * @return The Node
     */
    Node asNode();
}
