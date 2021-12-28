package com.wjduquette.george.model;

import com.wjduquette.george.App;
import com.wjduquette.george.ecs.Entity;

import java.util.ArrayList;
import java.util.List;

/**
 * A dialog class for simple mannikins. The mannikin will pick a speech
 * randomly.  A "please continue" response will be available if there are
 * more speeches the player hasn't seen.  A "goodbye" response is always
 * available.
 */
public class MannikinDialog extends AbstractDialog {
    private enum State {
        CONTINUE,
        END
    }
    private static final List<Response> RESPONSES = List.of(
        new Response(State.CONTINUE, "Is that so?"),
        new Response(State.CONTINUE, "I see."),
        new Response(State.CONTINUE, "Tell me more."),
        new Response(State.CONTINUE, "Fascinating."),
        new Response(State.CONTINUE, "I wouldn't know."),
        new Response(State.CONTINUE, "Truly?"),
        new Response(State.CONTINUE, "You don't say!")
    );

    //-------------------------------------------------------------------------
    // Instance Variables

    // The Mannikin's speeches
    private final List<String> speeches = new ArrayList<>();

    // The dialog state
    private State state = State.CONTINUE;

    //-------------------------------------------------------------------------
    // Constructor

    /**
     * Creates a dialog for a Mannikin entity.
     * @param region The region
     * @param npc The entity
     */
    public MannikinDialog(Region region, Entity npc) {
        super(region, npc, npc.mannikin().key());

        // FIRST, get the text strings.
        speeches.addAll(region.info().values(key() + ".greeting*"));
    }

    //-------------------------------------------------------------------------
    // NPCDialog API

    @Override
    public boolean isComplete() {
        return state == State.END;
    }

    @Override
    public String getDisplayText() {
        return App.RANDOM.takeFrom(speeches);
    }

    @Override
    public List<Response> getResponses() {
        List<Response> list = new ArrayList<>();

        if (!speeches.isEmpty()) {
            list.add(App.RANDOM.pickFrom(RESPONSES));
        }

        list.add(new Response(State.END, "Goodbye."));

        return list;
    }

    @Override
    public void respond(Response response) {
        state = (State)response.state();
    }
}
