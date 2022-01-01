package com.wjduquette.george.widgets;

/**
 * A UI option.  The option is disabled if the action is null.
 * @param label A label for display
 * @param handler The action to take, or null.
 */
public record Action(String label, Runnable handler) {
    /**
     * Is the action disabled?
     * TODO: Might not use this
     * @return true or false
     */
    public boolean isDisabled() {
        return handler == null;
    }

    public void perform() {
        handler.run();
    }
}
