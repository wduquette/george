package com.wjduquette.george.model;

import java.util.List;
import java.util.Random;

/** Normal random number generation, plus additional
 * convenience methods.
 * @author will
 *
 */
public final class RandomPlus extends Random {
    /** Rolls a random number of points from min to max,
     * uniformly distributed.
     *
     * @param min The minimum value
     * @param max The maximum value
     * @return A random number from min to max inclusive.
     */
    public int roll(int min, int max) {
        return min + nextInt(max - min + 1);
    }

    /** Rolls D100 against a percentage.
     *
     * @param percentage The percentage
     * @return true if the roll is less than the percentage, and 0 otherwise.
     */
    public boolean chance(int percentage) {
        return nextInt(100) < percentage;
    }

    /** Choose an item randomly from a list,
     * leaving the list unchanged.
     *
     * @param list The list, or null.
     * @return The chosen item, or null if the list is empty.
     */
    public <T> T pickFrom(List<T> list) {
        if (list == null || list.size() == 0) {
            return null;
        } else if (list.size() == 1) {
            return list.get(0);
        } else {
            return list.get(nextInt(list.size()));
        }
    }

    /** Choose an item randomly from a list,
     * removing the item from the list.
     *
     * @param list The list, or null.
     * @return The chosen item, or null if the list is empty.
     */
    public <T> T takeFrom(List<T> list) {
        if (list == null || list.size() == 0) {
            return null;
        } else if (list.size() == 1) {
            return list.remove(0);
        } else {
            return list.remove(nextInt(list.size()));
        }
    }
}
