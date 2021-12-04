package com.wjduquette.george.model;

/**
 * This class represents a player character, with its name, stats, status,
 * etc. For the moment it's just a record with a name; that will change.
 * It will be used as a component in the entities table.
 */
public record Player(String name) {
}
