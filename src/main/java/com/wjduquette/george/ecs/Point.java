package com.wjduquette.george.ecs;


/**
 * A named point on the map.  These are usually locations to which the
 * player can warp.  A Point will be associated with a Cell.
 * @param name The name
 */
public record Point(String name) { }
