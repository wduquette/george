package com.wjduquette.george.model;

import com.wjduquette.george.graphics.TerrainTileSet;
import com.wjduquette.george.util.KeywordParser;
import com.wjduquette.george.util.Resource;
import com.wjduquette.george.util.ResourceException;
import com.wjduquette.george.util.StringsTable;

/**
 * RegionMap is a class for loading and querying region definitions defined as
 * resources.  A RegionMap resource has several components:
 *
 * <ul>
 *     <li>A TerrainTileSet.</li>
 *     <li>A map file, defined as a JSON export from a Tiled .tmx map</li>
 *     <li>A StringsTable.</li>
 * </ul>
 *
 * These entities are called out in a text file, "{name}.region".
 *
 * <p><b>Things to be determined:</b></p>
 *
 * <ul>
 *     <li>The relationship between RegionMap and the World ECS structure.</li>
 *     <li>How region-specific code is attached to the RegionMap, i.e., do
 *         we subclass RegionMap, or do we define some other class for the
 *         region that has a RegionMap object as a component?</li>
 * </ul>
 */
public class RegionMap {
    //-------------------------------------------------------------------------
    // Instance Variables

    // The resource string, for debugging.
    private String resource;

    // The terrain tile set
    private TerrainTileSet terrain;

    // The Strings table
    private StringsTable strings;

    //-------------------------------------------------------------------------
    // Constructor

    public RegionMap(Class<?> cls, String relPath) {
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
        throws KeywordParser.KeywordException {
        // FIRST, prepare to accumulate data.
        this.resource = (relPath.startsWith("/"))
            ? relPath : cls.getCanonicalName() + ":" + relPath;

        // NEXT, parse the data.
        var parser = new KeywordParser();

        parser.defineKeyword("%terrain", (scanner, $) -> {
            var filename = Resource.relativeTo(relPath, scanner.next());
            terrain = new TerrainTileSet(cls, filename);
        });
        parser.defineKeyword("%strings", (scanner, $) -> {
            var filename = Resource.relativeTo(relPath, scanner.next());
            strings = new StringsTable(cls, filename);
        });
        parser.defineKeyword("%tilemap", (scanner, $) -> {
            // TODO
        });

        parser.parse(Resource.getLines(cls, relPath));
    }
}
