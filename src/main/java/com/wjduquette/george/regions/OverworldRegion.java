package com.wjduquette.george.regions;

import com.wjduquette.george.App;
import com.wjduquette.george.ecs.Entity;
import com.wjduquette.george.model.*;
import com.wjduquette.george.tmx.TiledMapReader;
import com.wjduquette.george.util.StringResult;

import java.util.ArrayList;
import java.util.List;
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
                var tutor = makeMannikin(key).loc(object2cell(obj));
                tutor.tagAsTripwire(
                    new Trigger.RadiusOnce(3, "overworld.tutor.triggered"),
                    new Step.Interact(tutor.id()));
                entities.add(tutor);

                return true;
            default: return false;
        }
    }

    @Override
    public final Optional<Dialog> findDialog(long id) {
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

    private class TutorDialog extends AbstractDialog {
        enum State {
            START,
            LEFTCLICK,
            INTERACT,
            FINAL,
            END
        }

        //---------------------------------------------------------------------
        // Instance Variables

        private State state = State.START;

        //---------------------------------------------------------------------
        // Constructor

        TutorDialog(Entity npc) {
            super(OverworldRegion.this, npc, npc.mannikin().key());
        }

        //---------------------------------------------------------------------
        // NPCDialog API

        @Override public String getName() { return "A Man in Black"; }

        @Override public StringResult getDescription() {
            return StringResult.EMPTY;
        }

        @Override
        public boolean isComplete() {
            return state == State.END;
        }

        @Override
        public String getDisplayText() {
            return info.get(key(), state.toString().toLowerCase()).asIs();
        }

        @Override
        public List<Response> getResponses() {
            List<Response> result = new ArrayList<>();

            switch (state) {
                case START -> result.add(new Response(State.LEFTCLICK,
                    "What do you mean \"Left-click\"?"));
                case LEFTCLICK ->
                    result.add(new Response(State.INTERACT,
                        "Interact?"));
                case INTERACT ->
                    result.add(new Response(State.FINAL,
                        "Anything else I should know?"));
                default -> {}
            }

            result.add(new Response(State.END, "Goodbye."));

            return result;
        }

        @Override
        public void respond(Response response) {
            state = (State)response.state();
        }
    }
}
