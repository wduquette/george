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
 * tiles, provided that all files have the same size.</p>
 *
 * <h3>Keyword File Syntax</h3>
 *
 * <p>By convention, a TileSet keyword file has a `.tileset` file type.
 * A TileSet file looks like this:</p>
 *
 * <pre>
 * %prefix {tileset prefix}
 * %size {tile width} {tile height}
 * %file {name of PNG file}
 * %tile {name of tile}
 * ...
 * %skip
 * </pre>
 *
 * <p>The {@code %prefix} keyword gives the tile set's prefix.  Each tile
 * is identified by a string "{prefix}.{tilename}, so that tiles from
 * multiple sets won't have name collisions.</p>
 *
 * <p>The {@code %size} keyword gives the size of the tile set's tiles in
 * pixels.</p>
 *
 * <p>The {@code %file} keyword specifies a file containing tile
 * images.  It is a PNG file of rectangular tiles of the given {@code %size},
 * such as might be created by PyxelEdit.  The PNG file must be in the same
 * folder as the keyword file.  A tile set may contain tiles from multiple
 * files.</p>
 *
 * <p>The {@code %tile} keyword gives the name of a tile in the
 * current {@code %file}.  {%tile} keywords are matched to the tiles in the
 * file from left to right and top to bottom.  Indices are assigned in the
 * same order.</p>
 *
 * <p>The {@code %unused} keyword is used to skip blank or unwanted tiles, so
 * as to preserve the relation of indices to PNG file tiles (as this is
 * important to external tools like the Tiled map editor).  The {@code %unused}
 * keyword is only required in the middle of a {@code %file}'s tiles.</p>
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

    // The list of sprites, by name.
    private final List<SpriteInfo> sprites = new ArrayList<>();

    // The map from name to tile.
    private final Map<String, SpriteInfo> tileMap = new HashMap<>();

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
            prefix = scanner.next();
        });
        parser.defineKeyword("%size", (scanner, $) -> {
            width = scanner.nextInt();
            height = scanner.nextInt();
        });
        parser.defineKeyword("%file", (scanner, $) -> {
            String filename = Resource.relativeTo(relPath, scanner.next());
            Image fileImage = loadTileSetImage(cls, relPath, filename);
            images = ImageUtils.getTiles(fileImage, width, height);
            nextIndex = 0;
        });
        parser.defineKeyword("%tile", (scanner, $) -> {
            var name = prefix + "." + scanner.next();
            var info = new SpriteInfo(name, images.get(nextIndex++));
            sprites.add(info);
            tileMap.put(info.name(), info);
        });
        parser.defineKeyword("%unused", (scanner, $) -> {
            var unused = new SpriteInfo("unused", images.get(nextIndex++));
            sprites.add(unused);
            // Do not add to name lookup.
        });

        parser.parse(Resource.getLines(cls, relPath));
    }

    private Image loadTileSetImage(
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
     * Returns the TileSet's resource identifier.
     * @return The string
     */
    public String resource() {
        return resource;
    }

    /**
     * Gets the tile set's prefix string.
     * @return The prefix
     */
    public String prefix() {
        return prefix;
    }

    /**
     * Gets the number of tiles in the set.
     * @return The size
     */
    public int size() {
        return sprites.size();
    }

    /**
     * Get a tile image given its index.
     * @param index The index
     * @return The image.
     */
    public Image get(int index) {
        return sprites.get(index).image();
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
        return Optional.ofNullable(tileMap.get(name));
    }

    /**
     * Get a tile image given its name.
     * @param name The name, including the prefix.
     * @return The image
     */
    public Optional<Image> find(String name) {
        SpriteInfo info = tileMap.get(name);
        return info != null ? Optional.of(info.image()) : Optional.empty();
    }

    /**
     * Get a read-only list of the tile set's tile info.
     * @return The list
     */
    public List<SpriteInfo> getInfo() {
        return Collections.unmodifiableList(sprites);
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
