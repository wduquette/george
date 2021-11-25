package com.wjduquette.george.tilesets;

import com.wjduquette.george.util.Resource;

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
    // Constructor

    public TileSet(Class<?> cls, String relPath) {
        Resource.getLines(cls, relPath).forEach(line -> System.out.println(line));
    }
}
