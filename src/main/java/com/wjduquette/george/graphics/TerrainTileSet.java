package com.wjduquette.george.graphics;

import com.wjduquette.george.model.TerrainTile;
import com.wjduquette.george.model.TerrainType;
import com.wjduquette.george.util.KeywordParser;
import com.wjduquette.george.util.Resource;
import com.wjduquette.george.util.ResourceException;
import javafx.scene.image.Image;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * A TerrainTileSet is a TileSet explicitly for terrain tiles.  It loads not
 * only the tile images but also the related TerrainTypes and other metadata
 * from a `.terrain` resource file and associated PNG image.
 *
 * <h3>File Syntax</h3>
 *
 * <p>By convention, a TerrainTileSet keyword file has a `.terrain` file type.
 * The syntax is as follows:</p>
 *
 * <pre>
 * %prefix {tileset prefix}
 * %size {tile width} {tile height}
 * %file {name of PNG file}
 * %tile {name of tile} "{descriptive text}" [{terrain type}]
 * ...
 * %unused
 * </pre>
 *
 * <p>The {@code %prefix} keyword gives the tile set's prefix.  Each tile
 * is identified by a string "{prefix}.{tilename}, so that tiles from
 * multiple sets won't have name collisions.</p>
 *
 * <p>The {@code %size} keyword gives the width and height of the tile set's
 * tiles in pixels.</p>
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
 * same order.  More specifically, the keyword defines:</p>
 *
 * <ul>
 *     <li>The tile's name, for lookup.</li>
 *     <li>Descriptive text, for display.</li>
 *     <li>Optionally, the terrain type as a lower-case TerrainType string.</li>
 * </ul>
 *
 * <p>The {@code %unused} keyword is used to skip blank or unwanted tiles, so
 * as to preserve the relation of indices to PNG file tiles (as this is
 * important to external tools like the Tiled map editor).  The {@code %unused}
 * keyword is only required in the middle of the {@code %file}'s tiles.</p>
 */
public class TerrainTileSet {
    //-------------------------------------------------------------------------
    // Instance Variables

    // The resource string, for debugging.
    private String resource;

    // The prefix string
    private String prefix;

    // The sizes
    private int tileWidth;
    private int tileHeight;

    // The list of tiles, for looking by tile index.
    private final List<TerrainTile> tileList = new ArrayList<>();

    // The map from name to tile, for name lookups
    private final Map<String, TerrainTile> tileMap = new HashMap<>();

    // Transient; used during parsing.
    private transient List<Image> images;
    private transient int nextIndex = 0;

    //-------------------------------------------------------------------------
    // Constructor

    public TerrainTileSet(Class<?> cls, String relPath) {
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
        this.tileWidth = -1;
        this.tileHeight = -1;

        // NEXT, parse the data.
        var parser = new KeywordParser();

        parser.defineKeyword("%prefix", (scanner, $) -> {
            prefix = scanner.next();
        });
        parser.defineKeyword("%size", (scanner, $) -> {
            tileWidth = scanner.nextInt();
            tileHeight = scanner.nextInt();
        });
        parser.defineKeyword("%file", (scanner, $) -> {
            String filename = scanner.next();
            Image fileImage = loadTileSetImage(cls, relPath, filename);
            images = ImageUtils.getTiles(fileImage, tileWidth, tileHeight);
            nextIndex = 0;
        });
        parser.defineKeyword("%tile", (scanner, $) -> {
            var name = prefix + "." + scanner.next();
            var description = getDescription(scanner.next());
            var type = TerrainType.UNKNOWN;
            if (scanner.hasNext()) {
                type = getTerrainType(scanner.next());
            }
            var info = new TerrainTile(name, type, description,
                images.get(nextIndex++));
            tileList.add(info);
            tileMap.put(info.name(), info);
        });
        parser.defineKeyword("%unused", (scanner, $) -> {
            // Do not add to name lookup.
            var unused = new TerrainTile(
                "unused", TerrainType.UNKNOWN, "unused",
                images.get(nextIndex++));
            tileList.add(unused);
        });

        parser.parse(Resource.getLines(cls, relPath));
        images = null;
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

    private TerrainType getTerrainType(String token) {
        return TerrainType.valueOf(token.toUpperCase());
    }

    private String getDescription(String token) {
        return token.replace("_", " ");
    }

    //-------------------------------------------------------------------------
    // Public Methods

    /**
     * Returns the tile set's resource identifier.
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
     * Get a tile's data given its index.
     * @param index The index
     * @return The data.
     */
    public TerrainTile get(int index) {
        return tileList.get(index);
    }

    /**
     * Get a tile's data given its name.  Throws an exception if the tile
     * doesn't exist.
     * @param name The name, including the prefix.
     * @return The data
     */
    public TerrainTile get(String name) {
        return find(name).orElseThrow();
    }

    /**
     * Get a tile's data given its name.
     * @param name The name, including the prefix.
     * @return The data
     */
    public Optional<TerrainTile> find(String name) {
        return Optional.ofNullable(tileMap.get(name));
    }

    /**
     * Get a read-only list of the tile set's tile info.
     * @return The list
     */
    public List<TerrainTile> getInfo() {
        return Collections.unmodifiableList(tileList);
    }
}
