package com.wjduquette.george.model;

/**
 * Conditions a mobile can suffer.
 */
public sealed interface Condition {
    record Sleep() implements Condition {}
    record Hayfever() implements Condition {}
    record Giddy() implements Condition {}
    record Newt() implements Condition {}
}
