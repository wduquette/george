package com.wjduquette.george.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.stream.Stream;

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
     * @throws Resource.Exception if the resource could not be found.
     */
    public InputStream get(Class<?> cls, String relPath) {
        InputStream istream = cls.getResourceAsStream(relPath);

        if (istream == null) {
            throw new Exception(cls, relPath);
        } else {
            return istream;
        }
    }

    /**
     * Get an input stream on a text resource.
     * @param path the absolute resource path
     * @return The stream
     * @throws Resource.Exception if the resource could not be found.
     */
    public InputStream get(String path) {
        InputStream istream = ClassLoader.getSystemResourceAsStream(path);

        if (istream == null) {
            throw new Exception(path);
        } else {
            return istream;
        }
    }

    /**
     * Get a stream of the lines from a text resource.
     * @param cls The class
     * @param relPath the path relative to the class
     * @return The stream
     * @throws Resource.Exception if the resource could not be found or read.
     */
    public Stream<String> getLines(Class<?> cls, String relPath) {
        try (var reader =
                 new BufferedReader(new InputStreamReader(get(cls, relPath))))
        {
            return reader.lines();
        } catch (IOException ex) {
            throw new Exception(cls, relPath, ex);
        }
    }

    /**
     * Get a stream of the lines from a text resource.
     * @param path the absolute resource path.
     * @return The stream
     * @throws Resource.Exception if the resource could not be found or read.
     */
    public Stream<String> getLines(String path) {
        try (var reader =
                 new BufferedReader(new InputStreamReader(get(path))))
        {
            return reader.lines();
        } catch (IOException ex) {
            throw new Exception(path, ex);
        }
    }

    /**
     * The exception thrown when a resource cannot be found.
     */
    public static class Exception extends RuntimeException {
        /**
         * The application failed to find a resource at the expected path.
         *
         * @param resourcePath The path
         */
        public Exception(String resourcePath) {
            this(resourcePath, null);
        }

        /**
         * Failed to find a resource relative to the given class.
         *
         * @param cls     The class
         * @param relPath The path relative to the class
         */
        public Exception(Class<?> cls, String relPath) {
            this(cls, relPath, null);
        }

        /**
         * The application failed to find a resource at the expected path.
         *
         * @param resourcePath The path
         * @param cause        The cause
         */
        public Exception(String resourcePath, Throwable cause) {
            super("Failed to find expected resource: " + resourcePath, cause);
        }

        /**
         * Failed to find a resource relative to the given class.
         *
         * @param cls     The class
         * @param relPath The path relative to the class
         * @param cause   The cause
         */
        public Exception(Class<?> cls, String relPath, Throwable cause) {
            super("Failed to find expected resource: " +
                cls.getCanonicalName() + ":" + relPath, cause);
        }
    }
}
