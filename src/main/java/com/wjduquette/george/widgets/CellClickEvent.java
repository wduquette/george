package com.wjduquette.george.widgets;

import com.wjduquette.george.model.Cell;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

/**
 * The user clicked on a map cell; notify listeners.
 */
public class CellClickEvent extends Event {
    public static final EventType<CellClickEvent> CELL_CLICK =
        new EventType<>("CELL_CLICK");

    //-------------------------------------------------------------------------
    // Instance variables

    private final Cell cell;
    private final MouseEvent mouseEvent;

    //-------------------------------------------------------------------------
    // Constructor

    /**
     * Creates a new CellClickEvent for the given map cell and underlying
     * mouse event.
     * @param cell The cell
     * @param mouseEvent The event
     */
    public CellClickEvent(Cell cell, MouseEvent mouseEvent) {
        super(CELL_CLICK);
        this.cell = cell;
        this.mouseEvent = mouseEvent;
    }

    //-------------------------------------------------------------------------
    // Accessors

    /**
     * Get the coordinates of the map cell they clicked on
     * @return The cell
     */
    public Cell getCell() {
        return cell;
    }

    /**
     * Get the underlying mouse event
     * @return The event
     */
    public MouseEvent getMouseEvent() {
        return mouseEvent;
    }

    /**
     * Generate a CellClickEvent for the node they clicked on.
     * @param cell The cell
     * @param evt The mouse event
     */
    public static void generate(Cell cell, MouseEvent evt) {
        Node node = evt.getPickResult().getIntersectedNode();
        node.fireEvent(new CellClickEvent(cell, evt));
    }
}
