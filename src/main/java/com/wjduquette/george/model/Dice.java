package com.wjduquette.george.model;

import com.wjduquette.george.App;

/**
 * Dice to roll.
 * @param number The number of dice
 * @param sides The number of sides for each die.
 * @param plus Any additional plus to the roll
 */
public record Dice(int number, int sides, int plus) {
    @Override public String toString() {
        var n = number > 0 ? Integer.toString(number) : "";
        var p = plus != 0 ? "+" + Integer.toString(plus) : "";

        return n + "D" + sides + p;
    }

    /**
     * Rolls the dice and returns the result.
     * @return
     */
    public int roll() {
        int result = plus;

        for (int i = 0; i < number; i++) {
            result += App.RANDOM.roll(1, sides);
        }

        return result;
    }
}
