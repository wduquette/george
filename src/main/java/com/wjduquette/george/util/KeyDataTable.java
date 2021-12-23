package com.wjduquette.george.util;

import java.util.*;

/**
 * This class defines a key/data table, a map from keys to string values.
 * The keys have the syntax "ident[[.ident]*]"; the dots are used to indicate
 * a hierarchy, e.g., "myobject.name", "myobject.description".  The store
 * is read/write, but is usually loaded from a resource file.  By convention,
 * such files have the type ".keydata".
 *
 * <p>The file's content is line-oriented:</p>
 *
 * <pre>
 * %prefix {prefix}
 *
 * %string {name} {value....}
 * %block {name}
 * ...text...
 * %end
 * </pre>
 *
 * <ul>
 *     <li> The {@code %prefix} keyword defines a prefix for the keys in the
 *          file, so that the full name is "{prefix}.{string name}".</li>
 *     <li> The {@code %string} keyword defines a key/value pair with a brief
 *          string value.  All tokens following the {name} are part of the
 *          value.</li>
 *     <li> Longer strings are bracketed by {@code %block {name}}/{@code %end}
 *          keywords.</li>
 *     <li> Leading and trailing whitespace is trimmed.</li>
 *     <li> Outside of blocks, blank lines and lines beginning with "#" are
 *          ignored.</li>
 *     <li> String names may contain no whitespace, but are otherwise
 *          unconstrained; they are usually dotted identifiers.</li>
 * </ul>
 *
 * For example,
 *
 * <pre>
 * # My Strings File
 *
 * %prefix my
 *
 * # Definitions for my.sword
 * %string sword.name My Sword
 * %string sword.sprite item.sword
 * %block sword.description
 * This is my sword.
 *
 * It's really sharp.
 * %end
 * </pre>
 *
 * @author will
 *
 */
public final class KeyDataTable {
    //-------------------------------------------------------------------------
    // Instance variables

    // The table's resource ID (class:relPath)
    private final String resource;

    // The table's prefix.
    private String prefix = null;

    // The table containing the data.
    private final Map<String,String> table = new HashMap<>();

    /** Creates a ".keydata" table using the strings in the named resource.
     * @param cls  The class that owns the resource.
     * @param relPath The resource name.
     */
    public KeyDataTable(Class<?> cls, String relPath) {
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

    /** Parses a ".keydata" resource
     *
     * @param cls The class relative which the resource is found.
     * @param relPath The path to the resource relative to the class.
     * @throws ResourceException on error.
     */
    private void loadData(Class<?> cls, String relPath)
        throws KeywordParser.KeywordException
    {
        var parser = new KeywordParser();
        parser.defineKeyword("%prefix", (scanner, $) -> {
            this.prefix = scanner.next();
        });
        parser.defineKeyword("%string", (scanner, $) -> {
            var key = scanner.next();
            if (prefix != null) {
                key = prefix + "." + key;
            }
            var value = scanner.nextLine().trim();
            table.put(key,value);
        });
        parser.defineBlock("%block", "%end", (scanner, block) -> {
            var key = scanner.next();
            if (prefix != null) {
                key = prefix + "." + key;
            }
            table.put(key, block);
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

    /** Retrieves a string from the table given its key and suffix, e.g.,
     * get("my.sword, "label") retrieves "my.sword.label"
     * @param key The key
     * @return The string
     */
    public Optional<String> get(String key, String suffix) {
        return Optional.ofNullable(table.get(key + "." + suffix));
    }

    /** @return a list of the keys. */
    public Set<String> keys() {
        return table.keySet();
    }

    /** Return a list of the keys that match a glob pattern
     * @param pattern The glob pattern
     * @return the list.
     */
    public List<String> keys(String pattern) {
        return table.keySet().stream()
            .filter(key -> StringUtil.matches(pattern, key))
            .toList();
    }

    /** Return a list of the entries whose keys match a glob pattern
     * @param pattern The glob pattern
     * @return the list.
     */
    public List<Map.Entry<String,String>> values(String pattern) {
        return table.entrySet().stream()
            .filter(e -> StringUtil.matches(pattern, e.getKey()))
            .toList();
    }
}
