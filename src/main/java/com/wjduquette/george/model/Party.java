package com.wjduquette.george.model;

import com.wjduquette.george.App;
import com.wjduquette.george.ecs.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Details about George and his Companions
 */
public class Party {
    public static final int BAGGAGE_SIZE = 32;
    private final int INITIAL_GOLD = 20;

    //-------------------------------------------------------------------------
    // Instance Variables

    // The application
    private final App app;

    // The Player Characters
    private final Entity george;

    // How much gold the party has.
    private int gold = INITIAL_GOLD;

    // The party's baggage
    private final Inventory baggage = new Inventory(BAGGAGE_SIZE);

    // The party's members, in order.
    List<Entity> members = new ArrayList<>();

    //-------------------------------------------------------------------------
    // Constructor

    public Party(App app) {
        this.app = app;
        george = makeGeorge();
        members.add(george);
    }

    //-------------------------------------------------------------------------
    // API

    public Inventory baggage() { return baggage; }
    public int gold() { return gold; }
    public Entity george() { return george; }
    public List<Entity> members() { return members; }

    public Entity leader() {
        return members.get(0);
    }

    public void addGold(int sum) {
        gold += sum;
    }

    public void deductGold(int sum) {
        gold = Math.max(gold - sum, 0);
    }

    //-------------------------------------------------------------------------
    // PC creation

    // Creates George as of the beginning of the game.
    private Entity makeGeorge() {
        var inv = new Inventory(Player.INVENTORY_SIZE);
        inv.add(app.items().make("vial.healing"));
        inv.add(app.items().make("vial.healing"));
        inv.add(app.items().make("scroll.mapping"));

        var equip = new Equipment();
        equip.wear(Role.HAND, app.items().make("weapon.small_wrench"));
        equip.wear(Role.BODY, app.items().make("body.overalls"));
        equip.wear(Role.HEAD, app.items().make("head.hat"));
        equip.wear(Role.FEET, app.items().make("foot.shoes"));

        return new Entity()
            .tagAsPlayer("George") // name
            .tagAsMobile("george") // key
            .sprite("mobile.george")
            .put(new Health(10))
            .put(inv)
            .put(equip);
    }
}
