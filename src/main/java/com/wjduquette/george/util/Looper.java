package com.wjduquette.george.util;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class Looper {
    //-------------------------------------------------------------------------
    // Instance Variables

    // The timeline to control the timing
    private final Timeline timeline;

    // The function to call on update
    private final Runnable loopFunc;

    //-------------------------------------------------------------------------
    // Constructor

    /**
     * Creates a Looper that executes its loopFunc every so many milliseconds
     * while running.
     *
     * @param msecs The duration between calls
     * @param loopFunc The function to call
     */
    public Looper(int msecs, Runnable loopFunc) {
        this.loopFunc = loopFunc;
        var frame = new KeyFrame(Duration.millis(msecs), ae -> loopFunc.run());
        this.timeline = new Timeline(frame);
        timeline.setCycleCount(Animation.INDEFINITE);
    }

    //-------------------------------------------------------------------------
    // Public Methods

    /**
     * Starts the loop going.
     */
    public void run() {
        if (timeline.getStatus() != Animation.Status.RUNNING) {
            timeline.play();
        }
    }

    /**
     * Stops the loop for user input.
     */
    public void stop() {
        timeline.stop();
    }

    /**
     * Determines whether the loop is running or stopped.
     * @return true or false
     */
    public boolean isRunning() {
        return timeline.getStatus() == Animation.Status.RUNNING;
    }
}
