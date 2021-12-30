package com.wjduquette.george.regions;

import com.wjduquette.george.App;
import com.wjduquette.george.ecs.Entity;
import com.wjduquette.george.model.*;
import com.wjduquette.george.tmx.TiledMapReader;

import java.util.*;

public class FloobhamRegion extends DataDrivenRegion {
    //-------------------------------------------------------------------------
    // Constructor

    public FloobhamRegion(App app, Class<?> cls, String relPath) {
        super(app, cls, relPath);
    }

    //-------------------------------------------------------------------------
    // Customizations

    protected final boolean handleObject(
        String key,
        TiledMapReader.MapObject obj)
    {
        switch (obj.name) {
            case "fillip": {
                var fillip = makeMannikin(key).cell(object2cell(obj));
                entities.add(fillip);
                return true;
            }
            case "town_chest1":
            case "town_chest2": {
                var chest = makeChest(key).cell(object2cell(obj));
                chest.chest().inventory().add(app.items().make("vial.healing"));
                entities.add(chest);
                return true;
            }
            case "town_chest3": {
                var chest = makeChest(key).cell(object2cell(obj));
                chest.chest().inventory().add(app.items().make("scroll.mapping"));
                entities.add(chest);
                return true;
            }
            default: return false;
        }
    }

    @Override
    public final Optional<Dialog> findDialog(long id) {
        var entity = get(id);
        if (entity.mannikin() != null &&
            entity.mannikin().key().equals("floobham.fillip"))
        {
            return Optional.of(new FillipDialog(entity));
        } else {
            return super.findDialog(id);
        }
    }

    //-------------------------------------------------------------------------
    // NPCs

    private class FillipDialog extends AbstractDialog {
        enum State {
            START,
            SPEND_NIGHT,
            RUMOUR,
            END
        }

        //---------------------------------------------------------------------
        // Instance Variables

        private State state = State.START;
        private final Set<State> seen = new HashSet<>();

        //---------------------------------------------------------------------
        // Constructor

        FillipDialog(Entity npc) {
            super(FloobhamRegion.this, npc, npc.mannikin().key());
        }

        //---------------------------------------------------------------------
        // NPCDialog API

        @Override public boolean isComplete() { return state == State.END; }

        @Override
        public String getDisplayText() {
            return getInfo(key(), state.toString().toLowerCase());
        }

        @Override
        public List<Response> getResponses() {
            List<Response> result = new ArrayList<>();

            if (!seen.contains(State.SPEND_NIGHT)) {
                result.add(new Response(State.SPEND_NIGHT,
                    "I'd like to spent the night."));
            }
            if (!seen.contains(State.RUMOUR)) {
                result.add(new Response(State.RUMOUR, "So, what's the latest?"));
            }
            result.add(new Response(State.END, "Goodbye."));

            return result;
        }

        @Override
        public void respond(Response response) {
            state = (State)response.state();
            seen.add(state);

            if (state == State.SPEND_NIGHT) {
                App.println("TODO: heal party");
            }
        }
    }
}
