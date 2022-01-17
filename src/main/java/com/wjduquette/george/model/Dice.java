package com.wjduquette.george.model;

import com.wjduquette.george.App;

import java.util.Scanner;

/**
 * Dice to roll.
 * @param number The number of dice
 * @param sides The number of sides for each die.
 * @param plus Any additional plus to the roll
 */
public record Dice(int number, int sides, int plus) {
    @Override public String toString() {
        var n = number > 0 ? Integer.toString(number) : "";
        var p = plus != 0 ? "+" + plus : "";

        return n + "D" + sides + p;
    }

    /**
     * Rolls the dice and returns the result.
     * @return The quantity rolled.
     */
    public int roll() {
        int result = plus;

        for (int i = 0; i < number; i++) {
            result += App.RANDOM.roll(1, sides);
        }

        return result;
    }

    /**
     * Converts a string of the form [{number}]D{sides}[+{plus}] into a
     * Dice value.
     * @param spec The spec
     * @return The Dice
     * @throws IllegalArgumentException if the spec can't be parsed.
     */
    public static Dice valueOf(String spec) {
        try (Scanner scanner = new Scanner(spec.trim().toLowerCase())) {
            scanner.useDelimiter("");
            var num = scanner.hasNextInt() ? scanner.nextInt() : 1;
            scanner.skip("d");
            var sides = scanner.nextInt();

            int plus = 0;
            if (scanner.hasNext("\\+")) {
                scanner.skip("\\+");
                plus = scanner.nextInt();
            }

            if (scanner.hasNext()) {
                throw new IllegalArgumentException(
                    "Unexpected token at end of spec: \"" + scanner.next() + "\"");
            }

            return new Dice(num, sides, plus);
        } catch (RuntimeException ex) {
            throw new IllegalArgumentException(
                "Invalid Dice spec: \"" + spec + "\", " + ex.getMessage(), ex);
        }
    }
}
