package com.wjduquette.george.graphics;

import com.wjduquette.george.util.KeywordParser;
import com.wjduquette.george.util.Resource;
import com.wjduquette.george.util.ResourceException;
import javafx.scene.image.Image;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * A SpriteSet represents a set of sprites loaded from disk.  On the disk, a
 * SpriteSet is represented by a ".sprite" keyword file and a PNG image.  In
 * memory, each sprite is accessible by index, 0 to N - 1, and by name.
 *
 * <p>A keyword file may include multiple PNG files, each with its own
 * sprites, provided that all files contain sprites of the same size.</p>
 *
 * <h3>Keyword File Syntax</h3>
 *
 * <p>By convention, a SpriteSet keyword file has a `.sprite` file type.
 * A SpriteSet file looks like this:</p>
 *
 * <pre>
 * %prefix {set prefix}
 * %size {sprite width} {sprite height}
 * %file {name of PNG file}
 * %sprite {name of sprite}
 * ...
 * %skip
 * </pre>
 *
 * <p>The {@code %prefix} keyword gives the set's prefix.  Each sprite
 * is identified by a string "{prefix}.{sprite name}, so that sprites from
 * multiple sets won't have name collisions.</p>
 *
 * <p>The {@code %size} keyword gives the size of the set's sprites in
 * pixels.</p>
 *
 * <p>The {@code %file} keyword specifies a file containing sprite
 * images.  It is a PNG file of rectangular tiles of the given {@code %size},
 * one tile per sprite, such as might be created by PyxelEdit.  The PNG file
 * must be in the same folder as the keyword file.  A set may contain sprites
 * from multiple files.</p>
 *
 * <p>The {@code %sprite} keyword gives the name of a sprite in the
 * current {@code %file}.  {%sprite} keywords are matched to the sprites in the
 * file from left to right and top to bottom.  Indices are assigned in the
 * same order.</p>
 *
 * <p>The {@code %unused} keyword is used to skip blank or unwanted sprites, so
 * as to preserve the relation of indices to PNG file tiles (as this is
 * important to external tools like the Tiled map editor).  The {@code %unused}
 * keyword is only required in the middle of a {@code %file}'s sprites.</p>
 */
public class SpriteSet {
    //-------------------------------------------------------------------------
    // Instance Variables

    // The resource string, for debugging.
    private String resource;

    // The prefix string
    private String prefix;

    // The sprite width and height in pixels
    private int width;
    private int height;

    // The map from name to sprite, in order of definition
    private final Map<String, SpriteInfo> spriteMap = new LinkedHashMap<>();

    // Transient; used during parsing.
    private transient List<Image> images;
    private transient int nextIndex = 0;

    //-------------------------------------------------------------------------
    // Constructor

    public SpriteSet(Class<?> cls, String relPath) {
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

    private void loadData(Class<?> cls, String relPath)
        throws KeywordParser.KeywordException
    {
        // FIRST, prepare to accumulate data.
        this.resource = (relPath.startsWith("/"))
            ? relPath : cls.getCanonicalName() + ":" + relPath;
        this.prefix = null;
        this.width = -1;
        this.height = -1;

        // NEXT, parse the data.
        var parser = new KeywordParser();

        parser.defineKeyword("%prefix", (scanner, $) -> {
            // TODO allow only once
            prefix = scanner.next();
        });
        parser.defineKeyword("%size", (scanner, $) -> {
            // TODO allow only once
            width = scanner.nextInt();
            height = scanner.nextInt();
        });
        parser.defineKeyword("%file", (scanner, $) -> {
            String filename = Resource.relativeTo(relPath, scanner.next());
            Image fileImage = loadSpriteSetImage(cls, relPath, filename);
            images = ImageUtils.getTiles(fileImage, width, height);
            nextIndex = 0;
        });
        parser.defineKeyword("%sprite", (scanner, $) -> {
            var name = prefix + "." + scanner.next();
            var info = new SpriteInfo(name, images.get(nextIndex++));
            spriteMap.put(info.name(), info);
        });
        parser.defineKeyword("%unused", (scanner, $) -> {
            // Do not add to name lookup.
            nextIndex++;
        });

        parser.parse(Resource.getLines(cls, relPath));
    }

    private Image loadSpriteSetImage(
        Class<?> cls,
        String relPath,
        String imgPath)
    {
        try (InputStream istream = Resource.get(cls, imgPath)) {
            return new Image(istream);
        } catch (IOException ex) {
            throw new ResourceException(cls, imgPath, ex);
        }
    }

    //-------------------------------------------------------------------------
    // Public Methods

    /**
     * Returns the SpriteSet's resource identifier.
     * @return The string
     */
    public String resource() {
        return resource;
    }

    /**
     * Gets the set's prefix string.
     * @return The prefix
     */
    public String prefix() {
        return prefix;
    }

    /**
     * Gets the number of sprites in the set.
     * @return The size
     */
    public int size() {
        return spriteMap.size();
    }

    /**
     * Get a tile image given its name.  Throws an exception if the name
     * is unknown.
     * @param name The name, including the prefix.
     * @return The image
     */
    public Image get(String name) {
        return find(name).orElseThrow();
    }

    /**
     * Get a tile's info given its name.  Throws an exception if the name
     * is unknown.
     * @param name The name, including the prefix.
     * @return The image
     */
    public SpriteInfo getInfo(String name) {
        return findInfo(name).orElseThrow();
    }

    /**
     * Get a tile's info given its name.
     * @param name The name, including the prefix.
     * @return The info
     */
    public Optional<SpriteInfo> findInfo(String name) {
        return Optional.ofNullable(spriteMap.get(name));
    }

    /**
     * Get a tile image given its name.
     * @param name The name, including the prefix.
     * @return The image
     */
    public Optional<Image> find(String name) {
        SpriteInfo info = spriteMap.get(name);
        return info != null ? Optional.of(info.image()) : Optional.empty();
    }

    /**
     * Get a read-only map of the set's contents
     * @return The map
     */
    public Map<String,SpriteInfo> getInfo() {
        return Collections.unmodifiableMap(spriteMap);
    }

    //-------------------------------------------------------------------------
    // TileInfo

    /**
     * A tile image.
     * @param name The name by which it's known in the tile set.
     * @param image The actual image.
     */
    public record SpriteInfo(String name, Image image) implements ImageInfo {}
}
