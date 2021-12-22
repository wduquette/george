package com.wjduquette.george.model;

import com.wjduquette.george.Sprites;
import com.wjduquette.george.ecs.*;
import com.wjduquette.george.graphics.TerrainTileSet;
import com.wjduquette.george.tmx.TiledMapReader;
import com.wjduquette.george.tmx.TiledMapReader.Layer;
import com.wjduquette.george.util.*;

import java.util.*;
import java.util.stream.Stream;

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
public class Region {
    // The name of the Tiled tile layer containing the basic terrain.
    private static final String TERRAIN_LAYER = "Terrain";

    // The name of the Tiled tile layer containing additional static
    // terrain features.
    private static final String FEATURES_LAYER = "Features";

    // Object type strings
    private static final String EXIT_OBJECT = "Exit";
    private static final String MANNIKIN_OBJECT = "Mannikin";
    private static final String POINT_OBJECT = "Point";
    private static final String SIGN_OBJECT = "Sign";

    private static final AStar.MetricFrame<Cell> ASTAR_FRAME =
        new AStar.MetricFrame<>() {
            @Override
            public double distance(Cell start, Cell end) {
                return Cell.cartesianDistance(start, end);
            }

            @Override
            public List<Cell> getAdjacent(Cell cell) {
                return cell.getAdjacent();
            }
        };

    //-------------------------------------------------------------------------
    // Static Functions

    public static List<Cell> findRoute(AStar.Assessor<Cell> assessor,
        Cell start, Cell end)
    {
        return AStar.findRoute(ASTAR_FRAME, assessor, start, end);
    }

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

    public Region(Class<?> cls, String relPath) {
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

            // NEXT, create the feature with its type, sprite, and cell.
            TerrainTile tile = terrainTileSet.get(tileIndex);

            Entity feature = entities.make()
                .feature(tile.type())
                .sprite(tile)
                .cell(r, c);

            // NEXT, Handle special cases.
            //
            // I'm not entirely happy about this convention, but it works well
            // enough for the majority of doors in a region.  We will also want
            // to have "door" objects allowed in Tiled object groups.
            var closed = prefix() + ".closed_door";
            var open = prefix() + ".open_door";

            if (tile.name().equals(closed)) {
                feature.put(new Door(DoorState.CLOSED, tile.type(), closed, open));
            } else if (tile.name().equals(open)) {
                feature.put(new Door(DoorState.OPEN, tile.type(), closed, open));
            }
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
                    // An exit to another region
                    case EXIT_OBJECT -> entities.make()
                        .put(makeExit(obj.name))
                        .cell(object2cell(obj));

                    // An NPC who just stands and talks when you poke him.
                    case MANNIKIN_OBJECT -> entities.make()
                        .put(new Mannikin(obj.name))
                        .feature(TerrainType.FENCE)
                        .sprite(obj.getProperty("sprite"))
                        .cell(object2cell(obj));

                    // A point to which the player can be warped
                    case POINT_OBJECT -> entities.make()
                        .point(obj.name)
                        .cell(object2cell(obj));

                    // A sign you can read
                    case SIGN_OBJECT ->
                        // TODO: Allow tile to be set from properties.
                        entities.make()
                            .feature(TerrainType.NONE)
                            .sign(obj.name)
                            .sprite(Sprites.ALL.getInfo("feature.sign"))
                            .cell(object2cell(obj));

                    default -> { }
                }
            }
        }
    }

    // Converts a "{regionName}:{pointName}" string into an Exit.
    private Exit makeExit(String regionPoint) {
        String[] tokens = regionPoint.split(":");

        if (tokens.length == 2) {
            return new Exit(tokens[0], tokens[1]);
        } else if (tokens.length == 1) {
            return new Exit(null, regionPoint);
        } else {
            throw new IllegalArgumentException("Invalid Exit name: \"" +
                regionPoint + "\"");
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

    /** This is the prefix to the region's tile names. */
    public String prefix() {
        return terrainTileSet.prefix();
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
     * Gets the region's terrain tile set.
     * @return The tile set.
     */
    public TerrainTileSet getTerrainTileSet() {
        return terrainTileSet;
    }

    public Entity get(long id) {
        var e = entities.get(id);
        if (e == null) {
            throw new IllegalArgumentException("No such entity: " + id);
        }
        return e;
    }

    public Optional<Entity> find(long id) {
        return Optional.ofNullable(entities.get(id));
    }

    /**
     * Query the entities table for entities with matching components.
     * @param classes A list of component classes to match
     * @return The stream of found entities.
     */
    @SuppressWarnings("unchecked")
    public Stream<Entity> query(Class<?>... classes) {
        return entities.query(classes);
    }

    /**
     * Query the entities table for entities with matching components.
     * @param classes A list of component classes to match
     * @return The stream of found entities.
     */
    @SuppressWarnings("unchecked")
    public Optional<Entity> findAt(Cell cell, Class<?>... classes) {
        return entities.findAt(cell, classes);
    }

    /**
     * Gets the region's entities table.
     * @return The table.
     */
    public EntityTable getEntities() {
        return entities;
    }

    /**
     * Get the terrain tile at the given row and column.
     * @param row The row index, 0 to height - 1
     * @param col The column index, 0 to width - 1
     * @return The terrain tile of null if the coordinates are out of bounds.
     */
    public TerrainTile getTerrain(int row, int col) {
        if (row < 0 || row >= height || col < 0 || col >= width) {
            return null;
        } else {
            return terrain.get(row * width + col);
        }
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
            .map(Entity::terrainType)
            .findFirst()
            .orElse(TerrainType.NONE);

        if (type != TerrainType.NONE) {
            return type;
        } else {
            TerrainTile tile = getTerrain(cell);

            return tile != null ? tile.type() : TerrainType.UNKNOWN;
        }
    }

    /**
     * Terrain Assessor: checks whether the cell exists and is simply
     * walkable, without any other concerns.
     * @param cell The cell
     * @return true or false
     */
    public boolean isWalkable(Cell cell) {
        return getTerrainType(cell).isWalkable();
    }

    /**
     * Gets the string, which must exist.
     * @param name The string's name in the table
     * @return The string we found.
     */
    public String getString(String name) {
        return strings.get(name).orElseThrow();
    }

    /**
     * Get the entire strings table.
     * @return The table.
     */
    public StringsTable strings() {
        return strings;
    }
}
