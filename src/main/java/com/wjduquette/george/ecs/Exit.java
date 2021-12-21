package com.wjduquette.george.ecs;

/**
 * This component is associated with a Loc, and indicates an exit to another
 * region.  Stepping on it will take the player to given point in that region.
 * The Loc must be passable to the player.
 *
 * <p>If the region is null, then this is a link to another point in the same
 * region.</p>
 * @param region The name of the next region
 * @param point The name of the point in that region
 */
public record Exit(String region, String point) implements Component {

    @Override
    public String toString() {
        return "(Exit " + region + " " + point + ")";
    }
}
