package com.wjduquette.george.regions;

import com.wjduquette.george.App;
import com.wjduquette.george.ecs.Entity;
import com.wjduquette.george.model.*;
import com.wjduquette.george.tmx.TiledMapReader;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class OverworldRegion extends DataDrivenRegion {
    //-------------------------------------------------------------------------
    // Constructor

    public OverworldRegion(App app, Class<?> cls, String relPath) {
        super(app, cls, relPath);
    }

    //-------------------------------------------------------------------------
    // Customizations

    protected final boolean handleObject(
        String key,
        TiledMapReader.MapObject obj)
    {
        switch (obj.name) {
            case "tutor":
                var tutor = makeMannikin(key).cell(object2cell(obj));
                tutor.tripwire(
                    new Trigger.RadiusOnce(3),
                    new Step.Interact(tutor.id()));
                entities.add(tutor);

                return true;
            default: return false;
        }
    }

    @Override
    public final Optional<NPCDialog> findDialog(long id) {
        var entity = get(id);
        if (entity.mannikin() != null &&
            entity.mannikin().key().equals("overworld.tutor"))
        {
            return Optional.of(new TutorDialog(entity));
        } else {
            return super.findDialog(id);
        }
    }

    //-------------------------------------------------------------------------
    // NPCs

    private class TutorDialog implements NPCDialog {
        enum State {
            START,
            LEFTCLICK,
            INTERACT,
            FINAL,
            END
        }

        //---------------------------------------------------------------------
        // Instance Variables

        private final Entity npc;
        private final String key;
        private State state = State.START;

        //---------------------------------------------------------------------
        // Constructor

        TutorDialog(Entity npc) {
            this.npc = npc;
            this.key = npc.mannikin().key();
        }

        //---------------------------------------------------------------------
        // NPCDialog API

        @Override public String getName() {
            return "A Man in Black";
        }

        @Override public Optional<String> getDescription() {
            return Optional.empty();
        }

        @Override
        public String foregroundSprite() {
            return npc.sprite().name();
        }

        @Override
        public String backgroundSprite() {
            return getTerrain(npc.cell()).name();
        }

        @Override
        public boolean isComplete() {
            return state == State.END;
        }

        @Override
        public String getDisplayText() {
            return getInfo(key, state.toString().toLowerCase());
        }

        @Override
        public List<Response> getResponses() {
            List<Response> result = new ArrayList<>();

            switch (state) {
                case START ->
                    result.add(new Response(State.LEFTCLICK, "Left-click?"));
                case LEFTCLICK ->
                    result.add(new Response(State.INTERACT, "Interact?"));
                case INTERACT ->
                    result.add(new Response(State.FINAL, "Anything Else?"));
                default -> {}
            }

            result.add(new Response(State.END, "Goodbye."));

            return result;
        }

        @Override
        public void respond(Response response) {
            state = (State)response.tag();
        }
    }
}
