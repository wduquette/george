package com.wjduquette.george.model;

import com.wjduquette.george.App;
import com.wjduquette.george.ecs.*;
import com.wjduquette.george.graphics.TerrainTileSet;
import com.wjduquette.george.tmx.TiledMapReader;
import com.wjduquette.george.tmx.TiledMapReader.Layer;
import com.wjduquette.george.util.*;

/**
 * Region is a class for loading and querying region definitions defined as
 * resources.  A Region resource has several components:
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
public class DataDrivenRegion extends Region {
    // The name of the Tiled tile layer containing the basic terrain.
    private static final String TERRAIN_LAYER = "Terrain";

    // The name of the Tiled tile layer containing additional static
    // terrain features.
    private static final String FEATURES_LAYER = "Features";

    // Object type strings
    public static final String CHEST = "Chest";
    public static final String EXIT = "Exit";
    public static final String MANNIKIN = "Mannikin";
    public static final String NARRATIVE = "Narrative";
    public static final String POINT = "Point";
    public static final String SIGN = "Sign";

    //-------------------------------------------------------------------------
    // Constructor

    public DataDrivenRegion(App app, Class<?> cls, String relPath) {
        super(app);

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

    //-------------------------------------------------------------------------
    // Data Loading

    // Loads the data from the region's .region file.  This is a separate
    // method to make exception handling easier.
    private void loadData(Class<?> cls, String relPath)
        throws KeywordParser.KeywordException {
        // FIRST, prepare to accumulate data.
        this.resource = (relPath.startsWith("/"))
            ? relPath : cls.getCanonicalName() + ":" + relPath;

        // NEXT, parse the data.
        var parser = new KeywordParser();

        parser.defineKeyword("%prefix", (scanner, $) -> prefix = scanner.next());

        parser.defineKeyword("%terrain", (scanner, $) -> {
            var filename = Resource.relativeTo(relPath, scanner.next());
            terrainTileSet = new TerrainTileSet(cls, filename);
        });

        parser.defineKeyword("%info", (scanner, $) -> {
            var filename = Resource.relativeTo(relPath, scanner.next());
            info = new KeyDataTable(cls, filename);
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

        for (int i = 0; i < height*width; i++) {
            seen.add(false);
        }
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

            // NEXT, create the feature with its type, sprite, and cell.
            TerrainTile tile = terrainTileSet.get(tileIndex);

            Entity feature = entities.make()
                .tagAsFeature()
                .label(tile.description())
                .terrain(tile.type())
                .tagAsSprite(tile)
                .cell(r, c);

            // NEXT, Handle special cases.
            //
            // I'm not entirely happy about this convention, but it works well
            // enough for the majority of doors in a region.  We will also want
            // to have "door" objects allowed in Tiled object groups.
            var closed = prefix() + ".closed_door";
            var open = prefix() + ".open_door";

            if (tile.name().equals(closed)) {
                var door = new Door(Opening.CLOSED, tile.type(), closed, open);
                feature.tagAsDoor(door);
            } else if (tile.name().equals(open)) {
                var door = new Door(Opening.OPEN, tile.type(), closed, open);
                feature.tagAsDoor(door);
            }
        }
    }

    private void readObjects(TiledMapReader map) {
        for (Layer layer : map.layers()) {
            if (!layer.type.equals(TiledMapReader.OBJECT_GROUP)) {
                continue;
            }

            for (TiledMapReader.MapObject obj : layer.objects()) {
                // For those objects whose name is an info key.
                var key = prefix + "." + obj.name;
                var cell = object2cell(obj);

                // FIRST, see if a subclass wants to handle it.
                if (handleObject(key, obj)) {
                    continue;
                }

                // NEXT, if not handle it in the standard way.
                var entity = switch (obj.type) {
                    case CHEST    -> makeChest(key).cell(cell);
                    case EXIT     -> makeExit(obj.name).cell(cell);
                    case MANNIKIN -> makeMannikin(key).cell(cell);
                    case NARRATIVE -> makeNarrative(key).cell(cell);
                    case POINT    -> makePoint(obj.name).cell(cell);
                    case SIGN     -> makeSign(key).cell(cell);
                    default -> null;
                };

                // NEXT, save the entity, if any
                if (entity != null) {
                    entities.add(entity);
                }
            }
        }
    }

    /**
     * Subclasses can override to handle special cases.
     * @param key The info key
     * @param obj The map object
     */
    protected boolean handleObject(
        String key,
        TiledMapReader.MapObject obj)
    {
        return false;
    }


    /**
     * Gets the cell corresponding to the MapObject's x/y coordinate.
     *
     * @param object The map object
     * @return The cell
     */
    protected Cell object2cell(TiledMapReader.MapObject object) {
        return new Cell(object.y / tileHeight, object.x / tileWidth);
    }
}
