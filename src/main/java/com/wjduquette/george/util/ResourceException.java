package com.wjduquette.george.util;

/**
 * The exception thrown when a resource cannot be found.
 */
public class ResourceException extends RuntimeException {
    /**
     * The application failed to find a resource at the expected path.
     *
     * @param resourcePath The path
     */
    public ResourceException(String resourcePath) {
        this(resourcePath, null);
    }

    /**
     * Failed to find a resource relative to the given class.
     *
     * @param cls     The class
     * @param relPath The path relative to the class
     */
    public ResourceException(Class<?> cls, String relPath) {
        this(cls, relPath, null);
    }

    /**
     * The application failed to find a resource at the expected path.
     *
     * @param resourcePath The path
     * @param cause        The cause
     */
    public ResourceException(String resourcePath, Throwable cause) {
        super("Failed to find expected resource: " + resourcePath, cause);
    }

    /**
     * Failed to find a resource relative to the given class.
     *
     * @param cls     The class
     * @param relPath The path relative to the class
     * @param cause   The cause
     */
    public ResourceException(Class<?> cls, String relPath, Throwable cause) {
        super("Failed to find expected resource: " +
            cls.getCanonicalName() + ":" + relPath, cause);
    }
}
