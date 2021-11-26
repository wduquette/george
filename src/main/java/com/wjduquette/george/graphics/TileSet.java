package com.wjduquette.george.graphics;

import com.wjduquette.george.graphics.ImageUtils;
import com.wjduquette.george.util.Resource;
import com.wjduquette.george.util.ResourceException;
import javafx.scene.image.Image;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * A TileSet represents a set of tiles loaded from disk.  On the disk, a
 * TileSet is represented by a keyword file and a PNG image.  In memory,
 * each tile is accessible by index, 0 to N - 1, and by name.
 *
 * <p>A keyword file may include multiple PNG files, each with its own
 * tiles, provided that all files have the same size.</p>
 *
 * <h3>Keyword File Syntax</h3>
 *
 * <p>A keyword file looks like this:</p>
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
public class TileSet {
    //-------------------------------------------------------------------------
    // Instance Variables

    // The resource string, for debugging.
    private String resource;

    // The prefix string
    private String prefix;

    // The sizes
    private int tileWidth;
    private int tileHeight;

    // The list of tiles, by name.
    private final List<TileInfo> tileList = new ArrayList<>();

    // The map from name to tile.
    private final Map<String,TileInfo> tileMap = new HashMap<>();

    //-------------------------------------------------------------------------
    // Constructor

    public TileSet(Class<?> cls, String relPath) {
        try {
            loadData(cls, relPath);
        } catch (ResourceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ResourceException(cls, relPath, ex);
        }
    }

    private void loadData(Class<?> cls, String relPath) {
        // FIRST, prepare to accumulate data.
        prefix = null;
        tileWidth = -1;
        tileHeight = -1;
        var nextIndex = 0;
        List<Image> images = null;

        // NEXT, get the input lines
        this.resource = (relPath.startsWith("/"))
            ? relPath : cls.getCanonicalName() + ":" + relPath;

        for (String line : Resource.getLines(cls, relPath)) {
            line = line.trim();

            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }

            String[] tokens = line.split(" ");

            switch (tokens[0]) {
                case "%prefix":
                    prefix = tokens[1];
                    break;
                case "%size":
                    tileWidth = Integer.valueOf(tokens[1]);
                    tileHeight = Integer.valueOf(tokens[2]);
                    break;
                case "%file":
                    Image fileImage = loadTileSetImage(cls, relPath, tokens[1]);
                    images = ImageUtils.getTiles(fileImage, tileWidth, tileHeight);
                    nextIndex = 0;
                    break;
                case "%tile":
                    var name = prefix + "." + tokens[1];
                    var info = new TileInfo(name, images.get(nextIndex++));
                    tileList.add(info);
                    tileMap.put(info.name(), info);
                    break;
                case "%unused":
                    var unused = new TileInfo("unused", images.get(nextIndex++));
                    tileList.add(unused);
                    // Do not add to name lookup.
                    break;
                default:
                    throw new ResourceException(cls, relPath,
                        "Unexpected keyword: \"" + tokens[0] + "\"");
            }
        }

        // NEXT, report problems.
        // TODO: Could do a lot of error reporting; but it's in an
        // internal file.
    }

    private Image loadTileSetImage(
        Class<?> cls,
        String relPath,
        String imageName)
    {
        String imgPath =
            new File(relPath).toPath().getParent().resolve(imageName).toString();

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
        return tileList.size();
    }

    /**
     * Get a tile image given its index.
     * @param index The index
     * @return The image.
     */
    public Image get(int index) {
        return tileList.get(index).image();
    }

    /**
     * Get a tile image given its name.
     * @param name The name, including the prefix.
     * @return The image
     */
    public Optional<Image> get(String name) {
        TileInfo info = tileMap.get(name);
        return info != null ? Optional.of(info.image()) : Optional.empty();
    }

    /**
     * Get a read-only list of the tile set's tile info.
     * @return The list
     */
    public List<TileInfo> getInfo() {
        return Collections.unmodifiableList(tileList);
    }

    //-------------------------------------------------------------------------
    // TileInfo

    /**
     * A tile image.
     * @param name The name by which it's known in the tile set.
     * @param image The actual image.
     */
    public static record TileInfo(String name, Image image) {}
}
