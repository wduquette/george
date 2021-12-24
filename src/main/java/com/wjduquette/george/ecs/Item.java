package com.wjduquette.george.ecs;

public record Item(String key) implements Component {
    @Override public String toString() { return "(Item " + key + ")"; }
}
