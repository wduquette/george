package com.wjduquette.george.model;

import com.wjduquette.george.ecs.Cell;
import com.wjduquette.george.ecs.Entity;
import com.wjduquette.george.ecs.EntityTable;
import com.wjduquette.george.graphics.TerrainTileSet;
import com.wjduquette.george.tmx.TiledMapReader;
import com.wjduquette.george.tmx.TiledMapReader.Layer;
import com.wjduquette.george.util.KeywordParser;
import com.wjduquette.george.util.Resource;
import com.wjduquette.george.util.ResourceException;
import com.wjduquette.george.util.StringsTable;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

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
 *     <li>The relationship between RegionMap and the ECS structure.</li>
 *     <li>How region-specific code is attached to the RegionMap, i.e., do
 *         we subclass RegionMap, or do we define some other class for the
 *         region that has a RegionMap object as a component?</li>
 * </ul>
 */
public class RegionMap {
    // The name of the Tiled tile layer containing the basic terrain.
    private static final String TERRAIN_LAYER = "Terrain";

    // The name of the Tiled tile layer containing additional static
    // terrain features.
    private static final String FEATURES_LAYER = "Features";

    //-------------------------------------------------------------------------
    // Instance Variables

    // The resource string, for debugging.
    private String resource;

    // The terrain tile set
    private TerrainTileSet terrain;

    // The Strings table
    private StringsTable strings;

    // The tile size for this map.
    private int tileHeight = 0;
    private int tileWidth = 0;

    // The size of this map.
    private int height = 0;
    private int width = 0;

    // The Entities Table
    private final EntityTable entities = new EntityTable();

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
            var filename = Resource.relativeTo(relPath, scanner.next());
            readTiledMap(cls, filename);
        });

        parser.parse(Resource.getLines(cls, relPath));
    }

    // Populates the entities table given the content of the reader.
    private void readTiledMap(Class<?> cls, String filename) {
        TiledMapReader map = TiledMapReader.read(cls, filename);

        this.width = map.getWidth();
        this.height = map.getHeight();
        this.tileHeight = map.getTileHeight();
        this.tileWidth = map.getTileWidth();

        Map<Cell,TerrainType> featureTypes = readFeaturesLayer(map);
        readTerrainLayer(map, featureTypes);
    }

    private Map<Cell,TerrainType> readFeaturesLayer(TiledMapReader map) {
        var featureType = new HashMap<Cell,TerrainType>();
        Layer layer = map.getTileLayer(FEATURES_LAYER);

        if (layer == null) {
            return featureType;
        }

        for (int i = 0; i < layer.data.length; i++) {
            // FIRST, get the row, column, and tile set index.
            // Tiled numbers tiles from 1 to N; we use 0 to N-1.
            int r = i / map.getWidth();
            int c = i % map.getWidth();
            int tileIndex = layer.data[i] - 1;

            // Skip empty tiles.
            if (tileIndex < 0) {
                continue;
            }

            TerrainTileSet.TerrainTile tile = terrain.get(tileIndex);

            Entity feature = entities.make()
                .putFeature()
                .putTile(tile.image())
                .putCell(r, c);

            featureType.put(feature.cell(), tile.type());
        }

        return featureType;
    }

    private void readTerrainLayer(
        TiledMapReader map,
        Map<Cell,TerrainType> featureTypes)
    {
        Layer terrainLayer = map.getTileLayer(TERRAIN_LAYER);

        for (int i = 0; i < terrainLayer.data.length; i++) {
            // FIRST, get the row, column, and tile set index.
            // Tiled numbers tiles from 1 to N; we use 0 to N-1.
            int r = i / map.getWidth();
            int c = i % map.getWidth();
            int tileIndex = terrainLayer.data[i] - 1;

            // Skip empty tiles.
            if (tileIndex < 0) {
                continue;
            }

            // If there is a static terrain feature, use its terrain type.
            // Otherwise use the terrain tile's terrain type.
            TerrainTileSet.TerrainTile tile = terrain.get(tileIndex);
            TerrainType featureType = featureTypes.get(new Cell(r, c));

            entities.make()
                .putTerrain(featureType != null ? featureType : tile.type())
                .putTile(tile.image())
                .putCell(r, c);
        }
    }

    //-------------------------------------------------------------------------
    // Public Methods

    /**
     * Gets the height of the map in tiles.
     * @return the height.
     */
    public int getHeight() {
        return height;
    }

    /**
     * Gets the width of the map in tiles.
     * @return the width
     */
    public int getWidth() {
        return width;
    }

    /**
     * Gets the height of one tile in pixels.
     * @return the height.
     */
    public int getTileHeight() {
        return tileHeight;
    }

    /**
     * Gets the width of one tile in pixels.
     * @return the width
     */
    public int getTileWidth() {
        return tileWidth;
    }

    public Stream<Entity> query(Class<?>... classes) {
        return entities.query(classes);
    }

    /**
     * Gets the region's entities table.
     * TODO: Possibly, should return a copy.
     * @return The table.
     */
    public EntityTable getEntities() {
        return entities;
    }
}
