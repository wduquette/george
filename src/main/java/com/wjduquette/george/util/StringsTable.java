package com.wjduquette.george.util;

import java.util.*;

/**
 * This class will load a Strings Table resource from disk, returning the
 * resource as a Map from keys to strings.  The file's contents is line
 * oriented.
 *
 * <ul>
 *     <li> Strings are bracketed by {@code %string}/{@code %end} keywords.</li>
 *     <li> Leading and trailing whitespace is trimmed.</li>
 *     <li> Outside of strings, blank lines and lines beginning with "#" are
 *          ignored.</li>
 *     <li> The {@code %string} keyword has one argument, the string's name.</li>
 *     <li> String names may contain no whitespace, but are otherwise
 *          unconstrained.</li>
 *     <li> The {@code %prefix} keyword defines a prefix for the string names,
 *          so that the full name is "{prefix}.{string name}".</li>
 * </ul>
 *
 * For example,
 *
 * <pre>
 * # My Strings File
 *
 * %prefix my
 *
 * # This string is called "my.MyString".
 * %string MyString
 * This is text in the string.
 *
 * This is more text in the string.
 * %end
 * </pre>
 *
 * @author will
 *
 */
public final class StringsTable {
    //-------------------------------------------------------------------------
    // Instance variables

    // The table's resource ID (class:relPath)
    private final String resource;

    // The table's prefix.
    private String prefix = null;

    // The table containing the data.
    private final Map<String,String> table = new HashMap<>();

    /** Creates a Strings table using the strings in the named resource.
     * @param cls  The class that owns the resource.
     * @param relPath The resource name.
     */
    public StringsTable(Class<?> cls, String relPath) {
        // FIRST, Save the resource ID
        this.resource = (relPath.startsWith("/"))
            ? relPath : cls.getCanonicalName() + ":" + relPath;

        // NEXT, load the data.
        try {
            loadData(cls, relPath);
        } catch (KeywordParser.KeywordException ex) {
            throw new ResourceException(cls, relPath, ex.getMessage());
        } catch (ResourceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ResourceException(cls, relPath, ex);
        }
    }

    /** Parses a StringsTable resource into a map.
     *
     * @param cls The class relative which the resource is found.
     * @param relPath The path to the resource relative to the class.
     * @throws ResourceException on error.
     */
    private void loadData(Class<?> cls, String relPath)
        throws KeywordParser.KeywordException
    {
        var parser = new KeywordParser();
        parser.defineKeyword("%parser", (scanner, $) -> {
            this.prefix = scanner.next();
        });
        parser.defineBlock("%string", "%end", (scanner, block) -> {
            var key = scanner.next();
            table.put(prefix + "." + key, block);
        });

        parser.parse(Resource.getLines(cls, relPath));
    }

    //-------------------------------------------------------------------------
    // Getters

    /**
     * Gets the table's resource ID.
     * @return The resource
     */
    public String resource() {
        return resource;
    }

    /**
     * Gets the table's prefix.
     * @return The prefix
     */
    public String prefix() {
        return prefix;
    }

    /** Retrieves a string from the table given its key
     * @param key The key
     * @return The string
     */
    public Optional<String> get(String key) {
        return Optional.ofNullable(table.get(key));
    }

    /** @return a list of the keys. */
    public List<String> keyList() {
        return new ArrayList<>(table.keySet());
    }

    // Old methods we might or might not want to support.

//    /** Return a list of the keys that match a glob pattern
//     * @param pattern The glob pattern
//     * @return the list.
//     */
//    public List<String> keyList(String pattern) {
//        List<String> keys = new ArrayList<>();
//
//        for (String key : table.keySet()) {
//            if (StringUtil.matches(pattern, key))
//                keys.add(key);
//        }
//
//        return keys;
//    }

//    /** Return a list of the strings whose keys match a glob pattern
//     * @param pattern The glob pattern
//     * @return the list.
//     */
//    public List<String> strings(String pattern) {
//        List<String> strings = new ArrayList<>();
//
//        for (String key : table.keySet()) {
//            if (StringUtil.matches(pattern, key))
//                strings.add(table.get(key));
//        }
//
//        return strings;
//    }
}
