package com.wjduquette.george.model;

import javafx.scene.image.Image;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * An NPC Dialog is an object that provides the data model for a dialog
 * interaction with a normal NPC (as opposed to, say, a shopkeeper).  It
 * provides the necessary information for NPCDialogPanel to manage the dialog
 * with the user.
 */
public interface NPCDialog {
    /**
     * Returns the NPC's name.
     * @return the name
     */
    String getName();

    /**
     * Returns the NPC's description.
     * @return the name
     */
    Optional<String> getDescription();

    /**
     * The foreground image for the NPC, i.e., its sprite image.
     * @return The image
     */
    String foregroundSprite();

    /**
     * The background image for the NPC, i.e., the terrain tile image on
     * which it is standing.
     * @return The image
     */
    String backgroundSprite();

    /**
     * Returns true if the player has ended the dialog, and false otherwise.
     * @return true or false
     */
    boolean isComplete();

    /**
     * Gets the current dialog text to display to the player, if !isComplete().
     * @return The text
     */
    String getDisplayText();

    /**
     * Gets the responses available to the player, if !isComplete().
     * @return The list
     */
    List<Response> getResponses();

    /**
     * Provides the user's response to the dialog, moving it to its next state.
     * @param response The response.
     */
    void respond(Response response);

    //-------------------------------------------------------------------------
    // Response

    /**
     * A response the player can make.
     * @param tag An identifying tag
     * @param text The text to display
     */
    record Response(Object tag , String text) {}
}
