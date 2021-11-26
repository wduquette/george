package com.wjduquette.george.util;

import java.util.*;

/**
 * A tool for parsing a keyword file.
 */
public final class KeywordParser {
    //-------------------------------------------------------------------------
    // Instance Variables

    // Definitions of the keywords.
    private final Map<String, Definition> definitions = new HashMap<>();

    //-------------------------------------------------------------------------
    // Constructor

    public KeywordParser() {
        // Nothing to do
    }

    //-------------------------------------------------------------------------
    // Configuration

    /**
     * Defines a keyword in the file.  The handler will be called when the
     * keyword is found.
     * @param keyword The keyword
     * @param handler The handler
     */
    public void defineKeyword(String keyword, KeywordHandler handler) {
        definitions.put(keyword, new Definition(handler, null));
    }

    /**
     * Defines a block, a collection of lines that begins with a keyword and
     * ends with an ending keyword.  The handler will receive a scanner on
     * the starting keyword, and the trimmed text of the block.
     * @param keyword The keyword, e.g., "%start"
     * @param endKeyword The ending keyword, e.g., "%end"
     * @param handler The handler
     */
    public void defineBlock(String keyword, String endKeyword, KeywordHandler handler) {
        definitions.put(keyword, new Definition(handler, endKeyword));
    }

    //-------------------------------------------------------------------------
    // Parsing

    /**
     * Parses the lines.
     * @param lines The input lines
     * @throws KeywordException on keyword error.
     */
    public void parse(List<String> lines) throws KeywordException {
        Queue<String> queue = new LinkedList<>(lines);

        while (!queue.isEmpty()) {
            var line = queue.poll().trim();

            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }

            var scanner = new Scanner(line);
            var keyword = scanner.next();

            var def = definitions.get(keyword);

            if (def == null) {
                throw new KeywordException("Unrecognized keyword: " + keyword);
            }

            String block = getAnyBlock(queue, def.endKeyword());
            def.handler().accept(scanner, block);
            scanner.close();
        }
    }

    // Accumulate lines from the file into a block, if we have an
    // endKeyword.
    private String getAnyBlock(Queue<String> queue, String endKeyword)
        throws KeywordException
    {
        if (endKeyword == null) {
            return null;
        }

        List<String> block = new ArrayList<>();

        while (!queue.isEmpty()) {
            var blockLine = queue.poll();
            if (blockLine.trim().equals(endKeyword)) {
                return String.join("\n", block).trim();
            }
        }

        throw new KeywordException("Missing block end keyword: \"" +
            endKeyword + "\"");
    }

    // A keyword definition
    private record Definition(KeywordHandler handler, String endKeyword) {}

    /**
     * A keyword handler.
     */
    public interface KeywordHandler {
        /**
         * A Scanner for reading arguments from the keyword line
         * @param scanner The scanner
         * @param block For block keywords, the text block.
         */
        void accept(Scanner scanner, String block);
    }

    /**
     * An exception raised while parsing the input.
     */
    public static class KeywordException extends Exception {
        public KeywordException(String message) {
            super(message);
        }
    }
}
