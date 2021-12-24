package com.wjduquette.george.ecs;

import com.wjduquette.george.model.Items;

public record Item(String key, Items.Type type) implements Component {
    @Override public String toString() { return "(Item " + key + " " + type + ")"; }
}
