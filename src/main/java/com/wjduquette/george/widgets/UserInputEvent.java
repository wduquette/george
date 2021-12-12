package com.wjduquette.george.widgets;

import com.wjduquette.george.model.Cell;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

/**
 * The user clicked on a map cell; notify listeners.
 */
public class UserInputEvent extends Event {
    public static final EventType<UserInputEvent> USER_INPUT =
        new EventType<>("USER_INPUT");

    //-------------------------------------------------------------------------
    // Instance variables

    private final UserInput input;
    private final MouseEvent mouseEvent;

    //-------------------------------------------------------------------------
    // Constructor

    /**
     * Creates a new event for the given input and underlying
     * mouse event.
     * @param input The input
     * @param mouseEvent The event
     */
    public UserInputEvent(UserInput input, MouseEvent mouseEvent) {
        super(USER_INPUT);
        this.input = input;
        this.mouseEvent = mouseEvent;
    }

    //-------------------------------------------------------------------------
    // Accessors

    /**
     * Get the user input.
     * @return The input
     */
    public UserInput getInput() {
        return input;
    }

    /**
     * Get the underlying mouse event
     * @return The event
     */
    public MouseEvent getMouseEvent() {
        return mouseEvent;
    }

    /**
     * Generate an event
     * @param input The input
     * @param evt The mouse event
     */
    public static void generate(UserInput input, MouseEvent evt) {
        Node node = evt.getPickResult().getIntersectedNode();
        node.fireEvent(new UserInputEvent(input, evt));
    }
}
