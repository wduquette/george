package com.wjduquette.george.model;

import com.wjduquette.george.App;
import com.wjduquette.george.ecs.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A dialog class for simple mannikins. The mannikin will pick a speech
 * randomly.  A "please continue" response will be available if there are
 * more speeches the player hasn't seen.  A "goodbye" response is always
 * available.
 */
public class MannikinDialog implements NPCDialog {
    private enum State {
        CONTINUE,
        GOODBYE
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

    // The region
    private final Region region;

    // The Mannikin entity
    private final Entity npc;

    // The Mannikin's info key
    private final String key;

    // The Mannkin's speeches
    private final List<String> speeches = new ArrayList<>();

    // Is the dialog at an end?
    private boolean isComplete = false;

    //-------------------------------------------------------------------------
    // Constructor

    /**
     * Creates a dialog for a Mannikin entity.
     * @param region The region
     * @param npc The entity
     */
    public MannikinDialog(Region region, Entity npc) {
        this.region = region;
        this.npc = npc;
        this.key = npc.mannikin().key();

        // FIRST, get the text strings.
        speeches.addAll(region.info().values(key + ".greeting*"));
    }

    //-------------------------------------------------------------------------
    // NPCDialog API

    @Override
    public String getName() {
        return region.getInfo(key, "label");
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.of(region.getInfo(key, "description"));
    }

    @Override
    public String foregroundSprite() {
        return npc.sprite().name();
    }

    @Override
    public String backgroundSprite() {
        return region.getTerrain(npc.cell()).name();
    }

    @Override
    public boolean isComplete() {
        return isComplete;
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

        list.add(new Response(State.GOODBYE, "Goodbye."));

        return list;
    }

    @Override
    public void respond(Response response) {
        isComplete = response.tag() == State.GOODBYE;
    }
}
