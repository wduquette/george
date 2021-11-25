package com.wjduquette.george.util;

/**
 * The exception thrown when a resource cannot be found.
 */
public class ResourceException extends RuntimeException {
    private static final String PREFIX =
        "Failed to find expected resource: ";

    /**
     * Failed to find a resource relative to the given class.
     *
     * @param cls     The class
     * @param relPath The path relative to the class
     */
    public ResourceException(Class<?> cls, String relPath) {
        super(PREFIX + cls.getCanonicalName() + ":" + relPath);
    }

    /**
     * Failed to find a resource relative to the given class.
     *
     * @param cls     The class
     * @param relPath The path relative to the class
     * @param cause   The cause
     */
    public ResourceException(Class<?> cls, String relPath, Throwable cause) {
        super(PREFIX + cls.getCanonicalName() + ":" + relPath, cause);
    }

    /**
     * Failed to find a resource relative to the given class.
     *
     * @param cls     The class
     * @param relPath The path relative to the class
     * @param detail  Detail text.
     */
    public ResourceException(Class<?> cls, String relPath, String detail) {
        super(PREFIX + cls.getCanonicalName() + ":" + relPath + ", " + detail);
    }
}
