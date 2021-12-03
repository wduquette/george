package com.wjduquette.george.model;

import com.wjduquette.george.TileSets;
import com.wjduquette.george.ecs.Cell;
import com.wjduquette.george.ecs.Entity;
import com.wjduquette.george.ecs.EntityTable;
import com.wjduquette.george.ecs.Feature;
import com.wjduquette.george.graphics.TerrainTileSet;
import com.wjduquette.george.tmx.TiledMapReader;
import com.wjduquette.george.tmx.TiledMapReader.Layer;
import com.wjduquette.george.util.KeywordParser;
import com.wjduquette.george.util.Resource;
import com.wjduquette.george.util.ResourceException;
import com.wjduquette.george.util.StringsTable;
import javafx.scene.image.Image;

import java.util.*;
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

    // Object type strings
    private static final String POINT_OBJECT = "Point";
    private static final String SIGN_OBJECT = "Sign";

    //-------------------------------------------------------------------------
    // Instance Variables

    // The resource string, for debugging.
    private String resource;

    // The terrain tile set.
    private TerrainTileSet terrainTileSet;

    // The Strings table
    private StringsTable strings;

    // The tile size for this map.
    private int tileHeight = 0;
    private int tileWidth = 0;

    // The size of this map.
    private int height = 0;
    private int width = 0;

    // The Terrain List: The terrain tiles, in row major order, drawn
    // from the terrainTileSet to match the TiledMap's TERRAIN_LAYER.
    private final ArrayList<TerrainTile> terrain = new ArrayList<>();

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
            terrainTileSet = new TerrainTileSet(cls, filename);
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

        this.width = map.width;
        this.height = map.height;
        this.tileHeight = map.tileheight;
        this.tileWidth = map.tilewidth;

        readTerrainLayer(map);
        readFeaturesLayer(map);
        readObjects(map);
    }

    private void readTerrainLayer(TiledMapReader map) {
        Layer terrainLayer = map.tileLayer(TERRAIN_LAYER).orElseThrow();

        terrain.ensureCapacity(terrainLayer.data.length);

        for (int i = 0; i < terrainLayer.data.length; i++) {
            // FIRST, Tiled numbers tiles from 1 to N; we use 0 to N-1.
            // TODO: Base this on the layer's firstgid.
            int tileIndex = terrainLayer.data[i] - 1;

            // Skip empty tiles.
            if (tileIndex < 0) {
                continue;
            }

            // Save the tile to the terrain list.
            TerrainTile tile = terrainTileSet.get(tileIndex);
            terrain.add(tile);
        }
    }

    private void readFeaturesLayer(TiledMapReader map) {
        Layer layer = map.tileLayer(FEATURES_LAYER).orElse(null);

        if (layer == null) {
            return;
        }

        for (int i = 0; i < layer.data.length; i++) {
            // FIRST, get the row, column, and tile set index.
            // Tiled numbers tiles from 1 to N; we use 0 to N-1.
            int r = i / map.height;
            int c = i % map.width;
            int tileIndex = layer.data[i] - 1;

            // Skip empty tiles.
            if (tileIndex < 0) {
                continue;
            }

            TerrainTile tile = terrainTileSet.get(tileIndex);

            Entity feature = entities.make()
                .feature(tile.type())
                .tile(tile.image())
                .cell(r, c);
        }
    }


    private void readObjects(TiledMapReader map) {
        for (Layer layer : map.layers()) {
            if (!layer.type.equals(TiledMapReader.OBJECT_GROUP)) {
                continue;
            }

            for (TiledMapReader.MapObject obj : layer.objects()) {
                Cell cell = object2cell(obj);

                switch (obj.type) {
                    case POINT_OBJECT:
                        entities.make()
                            .point(obj.name)
                            .put(object2cell(obj));
                        break;
                    case SIGN_OBJECT:
                        Image tile =
                            TileSets.FEATURES.get("feature.sign").orElseThrow();
                        entities.make()
                            .feature(TerrainType.NONE)
                            .sign(obj.name)
                            .tile(tile)
                            .put(object2cell(obj));
                        break;
                    default:
                        // Nothing to do
                        break;
                }
            }
        }
    }

    // Get the cell corresponding to the MapObject's x/y coordinate.
    //
    // For normal objects, we assume that the x,y coordinate is the
    // pixel coordinate of the upper left corner of the cell.
    private Cell object2cell(TiledMapReader.MapObject object) {
        return new Cell(object.y / tileHeight, object.x / tileWidth);
    }


    //-------------------------------------------------------------------------
    // Public Methods

    /**
     * Gets the resource ID.
     * @return the ID string
     */
    public String resource() {
        return resource;
    }

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
     * Returns true if the cell is within the map's bounds, and false
     * otherwise
     * @param cell The cell
     * @return true or false
     */
    public boolean contains(Cell cell) {
        return cell.row() >= 0 && cell.row() <= height
            && cell.col() >= 0 && cell.col() <= width;
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

    /**
     * Query the entities table for entities with matching components.
     * @param classes A list of component classes to match
     * @return The stream of found entities.
     */
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

    /**
     * Get the terrain tile at the given row and column.
     * @param row The row index, 0 to height - 1
     * @param col The column index, 0 to width - 1
     * @return The terrain tile
     */
    public TerrainTile getTerrain(int row, int col) {
        return terrain.get(row * width + col);
    }

    /**
     * Get the terrain tile for the given cell.
     * @param cell The cell
     * @return The terrain tile
     */
    public TerrainTile getTerrain(Cell cell) {
        return getTerrain(cell.row(), cell.col());
    }

    /**
     * Gets the terrain type at the cell, taking features into account.
     * TODO: This can be more efficient.
     * @param cell The cell
     * @return The terrain type.
     */
    public TerrainType getTerrainType(Cell cell) {
        TerrainType type = entities.query(Feature.class)
            .filter(e -> e.isAt(cell))
            .map(e -> e.terrainType())
            .findFirst()
            .orElse(TerrainType.NONE);

        return type != TerrainType.NONE ? type : getTerrain(cell).type();
    }
}
