package com.wjduquette.george.model;

import java.util.HashSet;
import java.util.Set;

public class Flags {
    //-------------------------------------------------------------------------
    // Instance Variables

    // A flag is set if its string is in the set.
    private final Set<String> flags = new HashSet<>();

    //-------------------------------------------------------------------------
    // Constructor

    public Flags() {} // Nothing to do

    //-------------------------------------------------------------------------
    // API

    public boolean isSet(String flagName) {
        return flags.contains(flagName);
    }

    public void set(String flagName) {
        flags.add(flagName);
    }

    public void clear(String flagName) {
        flags.remove(flagName);
    }
}
