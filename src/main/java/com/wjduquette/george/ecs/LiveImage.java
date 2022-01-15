package com.wjduquette.george.ecs;

import com.wjduquette.george.model.Offset;
import javafx.scene.image.Image;

/**
 * A "live image" is an image plus a cell offset.  It's used by the Animator
 * when animating a sprite; the image is draw at the entity's cell + offset.
 * If the entity has both a Sprite and a LiveImage, it's the LiveImage that is
 * rendered.
 * @param name A name, as a debugging aid
 * @param image The image to draw
 * @param offset The offset from the cell (r,c), in fractional rows and columns.
 */
public record LiveImage(String name, Image image, Offset offset)
    implements Component
{
    /**
     * Returns a new LiveImage with an updated offset.
     * @param offset The new offset
     * @return The new LiveImage
     */
    public LiveImage offset(Offset offset) {
        return new LiveImage(name, image, offset);
    }

    @Override
    public String toString() {
        return "(LiveImage " + name + " " + offset + ")";
    }
}
