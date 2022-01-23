package com.wjduquette.george.util;

import java.util.*;

/**
 * This class defines a key/data file format, resulting in a map from keys to
 * values. The keys have the syntax "ident[[.ident]*]"; the dots are used to
 * indicate a hierarchy, e.g., "myobject.name", "myobject.description".
 * The store is read/write, but is usually loaded from a resource file.  By convention,
 * such files have the type ".keyfile".
 *
 * <p>The file's content is line-oriented:</p>
 *
 * <pre>
 * %prefix {prefix}
 *
 * %record {record}
 * %field {name} {value....}
 * %block {name}
 * ...text...
 * %end
 * </pre>
 *
 * <ul>
 *     <li> The {@code %prefix} keyword defines a prefix for all keys in the
 *          file.</li>
 *     <li> The {@code %record} keyword defines a record name, which defaults to
 *          the empty string.</li>
 *     <li> The {@code %field} keyword defines a key/value pair with a brief
 *          string value.  All tokens following the {name} are part of the
 *          value. (Note: {@code %string} is a deprecated synonym for
 *          {@code %field}.)</li>
 *     <li> Longer strings are bracketed by {@code %block {name}}/{@code %end}
 *          keywords.</li>
 *     <li> The full key for a value is {@code [{prefix}.[{record}.]{name}}</li>
 *     <li> Leading and trailing whitespace is trimmed.</li>
 *     <li> Outside of blocks, blank lines and lines beginning with "#" are
 *          ignored.</li>
 *     <li> String names may contain no whitespace, but are otherwise
 *          unconstrained; they are usually dotted identifiers.</li>
 * </ul>
 *
 * <p>For example,</p>
 *
 * <pre>
 * %field my.sword.name My Sword
 * %field my.sword.sprite item.sword
 * %field my.sword.description
 * This is my sword.
 *
 * It's really sharp.
 * %end
 *
 * </pre>
 *
 * <p>Or, using {@code %prefix} and {%record}</p>
 *
 * <pre>
 * %prefix my
 *
 * %record sword
 * %field name My Sword
 * %field sprite item.sword
 * %field description
 * This is my sword.
 *
 * It's really sharp.
 * %end
 * </pre>
 *
 * @author will
 *
 */
public final class KeyFile {
    //-------------------------------------------------------------------------
    // Instance variables

    // The table's resource ID (class:relPath)
    private final String resource;

    // The table's prefix.
    private String prefix = null;

    // The current record name
    private transient String record = null;

    // The table containing the data.
    private final Map<String,String> table = new HashMap<>();

    /** Creates a ".keydata" table using the strings in the named resource.
     * @param cls  The class that owns the resource.
     * @param relPath The resource name.
     */
    public KeyFile(Class<?> cls, String relPath) {
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
        parser.defineKeyword("%record", (scanner, $) -> {
            if (scanner.hasNext()) {
                this.record = scanner.next();
            } else {
                this.record = null;
            }
        });
        parser.defineKeyword("%field", (scanner, $) -> {
            var name = scanner.next();
            var value = scanner.nextLine().trim();
            table.put(fullKey(name),value);
        });
        parser.defineBlock("%block", "%end", (scanner, block) -> {
            var name = scanner.next();
            table.put(fullKey(name), block);
        });

        parser.parse(Resource.getLines(cls, relPath));
    }

    private String fullKey(String fieldName) {
        if (record != null) {
            fieldName = record + "." + fieldName;
        }

        if (prefix != null) {
            fieldName = prefix + "." + fieldName;
        }

        return fieldName;
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

    /**
     * Retrieves a string from the table given its key
     * @param key The key
     * @return The result, which will not be empty.
     * @throws IllegalArgumentException if the key does not exist.
     */
    public StringResult get(String key) {
        var value = table.get(key);
        if (value != null) {
            return new StringResult(value);
        } else {
            throw new IllegalArgumentException("Unknown key: " + key);
        }
    }

    /**
     * Retrieves a string from the table given its record prefix and a suffix,
     * e.g., get("my.sword, "label") retrieves "my.sword.label"
     * @param record The record's key prefix
     * @return The result, which will not be empty.
     * @throws IllegalArgumentException if the key does not exist.
     */
    public StringResult get(String record, String suffix) {
        return get(record + "." + suffix);
    }

    /**
     * Looks up a string from the table given its key
     * @param key The key
     * @return The result, which may be empty.
     */
    public StringResult lookup(String key) {
        return new StringResult(table.get(key));
    }

    /**
     * Retrieves a string from the table given its record prefix and a suffix,
     * e.g., get("my.sword, "label") retrieves "my.sword.label"
     * @param record The record's key prefix
     * @return The result, which might not be empty.
     */
    public StringResult lookup(String record, String suffix) {
        return lookup(record + "." + suffix);
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
    public List<Map.Entry<String,String>> pairs(String pattern) {
        return table.entrySet().stream()
            .filter(e -> StringUtil.matches(pattern, e.getKey()))
            .toList();
    }

    /** Return a list of the values whose keys match a glob pattern
     * @param pattern The glob pattern
     * @return the list.
     */
    public List<String> values(String pattern) {
        return table.entrySet().stream()
            .filter(e -> StringUtil.matches(pattern, e.getKey()))
            .map(Map.Entry::getValue)
            .toList();
    }
}
