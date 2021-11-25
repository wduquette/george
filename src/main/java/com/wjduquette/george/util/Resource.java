package com.wjduquette.george.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * A utility class for reading application resources.  All calls will throw
 * Resource.Exception on error, terminating the program.
 */
public class Resource {
    private Resource() {} // Not instantiable

    /**
     * Get an input stream on a text resource.
     * @param cls The class
     * @param relPath the path relative to the class
     * @return The stream
     * @throws ResourceException if the resource could not be found.
     */
    public static InputStream get(Class<?> cls, String relPath) {
        InputStream istream = cls.getResourceAsStream(relPath);

        if (istream == null) {
            throw new ResourceException(cls, relPath);
        } else {
            return istream;
        }
    }

    /**
     * Get a list of the lines from a text resource.
     * @param cls The class
     * @param relPath the path relative to the class
     * @return The list
     * @throws ResourceException if the resource could not be found or read.
     */
    public static List<String> getLines(Class<?> cls, String relPath) {
        try (var reader =
                 new BufferedReader(new InputStreamReader(get(cls, relPath))))
        {
            return reader.lines().toList();
        } catch (IOException ex) {
            throw new ResourceException(cls, relPath, ex);
        }
    }

}
