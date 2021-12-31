package com.wjduquette.george.widgets;

import com.wjduquette.george.App;
import com.wjduquette.george.util.Looper;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class LogPane extends GamePane {
    private static final int MAX_VISIBLE = 3;
    private static final double MARGIN = 30;
    private static final Font MESSAGE_FONT = Font.font("Helvetica", 25);
    private static final double MESSAGE_LEADING = 30;
    private static final long MESSAGE_DURATION = 2000;

    //-------------------------------------------------------------------------
    // Instance Variables

    // The list of messages
    public final Deque<String> queue = new ArrayDeque<>();
    public final List<Message> messages = new ArrayList<>();
    public final Looper looper = new Looper(250, this::repaint);

    //-------------------------------------------------------------------------
    // Constructor

    public LogPane(App app) {
        super(app);
        setMouseTransparent(true);
        canvas().setMouseTransparent(true);
    }

    //-------------------------------------------------------------------------
    // Logic

    @Override
    protected void onRepaint() {
        // FIRST, prune old messages
        var limit = System.currentTimeMillis() - MESSAGE_DURATION;

        while (!messages.isEmpty() && messages.get(0).millis < limit) {
            messages.remove(0);
        }

        // NEXT, add messages until the message list is full
        while (messages.size() < MAX_VISIBLE && !queue.isEmpty()) {
            var text = queue.remove();
            messages.add(new Message(System.currentTimeMillis(), text));
        }

        // NEXT, if there are no messages left, we're done.  Stop the
        // timeout loop.
        if (messages.isEmpty()) {
            looper.stop();
            return;
        }

        // NEXT, display the visible items.
        var ty = getHeight() - MARGIN - MAX_VISIBLE*MESSAGE_LEADING;

        var count = Math.min(MAX_VISIBLE, messages.size());

        for (int i = 0; i < count; i++) {
            gc().setStroke(Color.BLACK);
            gc().setLineWidth(2);
            gc().setFill(Color.WHITE);
            gc().setFont(MESSAGE_FONT);
            gc().strokeText(messages.get(i).text, MARGIN, ty);
            gc().fillText(messages.get(i).text, MARGIN, ty);

            ty += MESSAGE_LEADING;
        }
    }

    //-------------------------------------------------------------------------
    // API

    public void log(String message) {
        queue.add(message);
        repaint();
        looper.run();
    }

    /**
     * A logged message.
     * @param millis The time when it was logged.
     * @param text The text.
     */
    private record Message(long millis, String text) {}
}
