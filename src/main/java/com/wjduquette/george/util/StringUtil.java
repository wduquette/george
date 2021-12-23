package com.wjduquette.george.util;

import java.util.List;

public class StringUtil {
    private StringUtil() {} // Not instantiable

    /**
     * Determines whether a given string matches a glob pattern.
     * Note that the glob syntax supports only "*" and "?".
     * I copied this code (with minor changes) from a StackOverflow answer:
     * http://stackoverflow.com/questions/1247772/is-there-an-equivalent-of-java-util-regex-for-glob-type-patterns
     * @param pattern  The glob pattern
     * @param text  The text to match
     * @return true if the text matches the pattern, and false otherwise.
     */
    public static boolean matches(String pattern, String text) {
        String rest = null;
        int pos = pattern.indexOf('*');
        if (pos != -1) {
            rest = pattern.substring(pos + 1);
            pattern = pattern.substring(0, pos);
        }

        if (pattern.length() > text.length())
            return false;

        // handle the part up to the first *
        for (int i = 0; i < pattern.length(); i++)
            if (pattern.charAt(i) != '?'
                && !pattern.substring(i, i + 1).equalsIgnoreCase(text.substring(i, i + 1)))
                return false;

        // recurse for the part after the first *, if any
        if (rest == null) {
            return pattern.length() == text.length();
        } else {
            for (int i = pattern.length(); i <= text.length(); i++) {
                if (matches(rest, text.substring(i)))
                    return true;
            }
            return false;
        }
    }

    /** Given a list of strings, joins them together with commas
     * and "and".
     * @param list The list of strings.
     * @return The joined string.
     */
    public static String joinProse(List<String> list) {
        StringBuilder text = new StringBuilder();

        if (list.size() == 1) {
            text.append(list.get(0));
        } else {
            for (int i = 0; i < list.size(); i++) {
                text.append(list.get(i));

                if (i < list.size() - 2)
                    text.append(", ");
                else if (i == list.size() - 2)
                    text.append(", and ");
            }
        }

        return text.toString();
    }

}
