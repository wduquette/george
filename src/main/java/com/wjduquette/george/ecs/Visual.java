package com.wjduquette.george.ecs;

import javafx.scene.image.Image;

/**
 * A "visual" is an image plus X/Y pixel offsets.  It's used by the Animator
 * when animating a sprite.  A Visual is relative to its entity's Loc.
 * If the entity has both a Sprite and a Visual, it's the Visual that is
 * rendered.
 * @param name A name, as a debugging aid
 * @param image The image to draw
 * @param xOffset The xOffset from the cell (x,y), in pixels
 * @param yOffset The yOffset from the cell (x,y), in pixels
 */
public record Visual(String name, Image image, double xOffset, double yOffset)
    implements Component
{
    /**
     * Returns a new Loc with an updated offset.
     * @param newRowOffset The new row offset, in fractional rows
     * @param newColOffset The new column offset, in fractional columns
     * @return The new Loc
     */
    public Visual offset(double newRowOffset, double newColOffset) {
        return new Visual(name, image, newRowOffset, newColOffset);
    }

    @Override
    public String toString() {
        return "(Visual " + name + " " +
            String.format(" %.2f %.2f", xOffset, yOffset) + ")";
    }
}
