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

    //-------------------------------------------------------------------------
    // Constructor

    /**
     * Creates a new event for the given input and underlying
     * mouse event.
     * @param input The input
     */
    public UserInputEvent(UserInput input) {
        super(USER_INPUT);
        this.input = input;
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
}
