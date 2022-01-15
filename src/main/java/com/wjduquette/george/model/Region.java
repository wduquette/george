package com.wjduquette.george.model;

import com.wjduquette.george.App;
import com.wjduquette.george.ecs.*;
import com.wjduquette.george.graphics.TerrainTileSet;
import com.wjduquette.george.util.AStar;
import com.wjduquette.george.util.KeyDataTable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * A Region is an area in the game with its own terrain and entities, which
 * the player can explore. A Region has several components:
 *
 * <ul>
 *     <li>A "prefix", used in entity keys and sprite names.</li>
 *     <li>A TerrainTileSet.</li>
 *     <li>A map file, defined as a JSON export from a Tiled .tmx map</li>
 *     <li>A game info table.</li>
 * </ul>
 *
 * <p>The {@link DataDrivenRegion} class loads a region from disk.  Other
 * subclasses may provide a mixture of loading map data and other
 * resources.</p>
 */
public abstract class Region {
    //-------------------------------------------------------------------------
    // Static Data

    // The metric frame for the generic AStar algorithm
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

    /**
     * Finds a route from start to end given the AStar assessment function.
     * The route will be empty if no route could be found.
     * @param assessor The assessment function
     * @param start The starting cell
     * @param end The ending cell
     * @return The route
     */
    public static List<Cell> findRoute(
        AStar.Assessor<Cell> assessor,
        Cell start,
        Cell end)
    {
        return AStar.findRoute(ASTAR_FRAME, assessor, start, end);
    }

    /**
     * Finds the distance from start to end given the AStar assessment function.
     * The distance will be Integer.MAX_VALUE if there's no way to get there.
     * @param assessor The assessment function
     * @param start The starting cell
     * @param end The ending cell
     * @return The distance
     */
    public static int distance(
        AStar.Assessor<Cell> assessor,
        Cell start,
        Cell end)
    {
        var route = AStar.findRoute(ASTAR_FRAME, assessor, start, end);

        return !route.isEmpty() ? route.size() : Integer.MAX_VALUE;
    }

    //-------------------------------------------------------------------------
    // Instance Variables

    // The application, for application resources.
    protected final App app;

    // The resource string, for debugging.
    protected String resource = null;

    // The region's prefix, for keys.
    protected String prefix;

    // The terrain tile set.
    protected TerrainTileSet terrainTileSet;

    // The game info table
    protected KeyDataTable info;

    // The tile size for this map.
    protected int tileHeight = 0;
    protected int tileWidth = 0;

    // The size of this map.
    protected int height = 0;
    protected int width = 0;

    // The Terrain List: The terrain tiles, in row major order, drawn
    // from the terrainTileSet to match the TiledMap's TERRAIN_LAYER.
    protected final ArrayList<TerrainTile> terrain = new ArrayList<>();

    // The Seen List: true if the tile has been seen by the player, and false
    // otherwise.
    protected final ArrayList<Boolean> seen = new ArrayList<>();

    // The Entities Table
    protected final EntityTable entities = new EntityTable();

    //-------------------------------------------------------------------------
    // Constructor

    public Region(App app) {
        this.app = app;
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
    public Stream<Entity> query(Class<?>... classes) {
        return entities.query(classes);
    }

    /**
     * Query the entities table for entities with matching components.
     * @param classes A list of component classes to match
     * @return The stream of found entities.
     */
    public Optional<Entity> findAt(Cell cell, Class<?>... classes) {
        return entities.findAt(cell, classes);
    }

    /**
     * Gets the region's entities table.
     * @return The table.
     */
    public EntityTable entities() {
        return entities;
    }

    /**
     * Gets the cell associated with the point name
     * @param name The point name
     * @return The cell, if found.
     */
    public Optional<Cell> point(String name) {
        return query(Point.class)
            .filter(e -> e.point().name().equals(name))
            .map(Entity::cell)
            .findFirst();
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
     * Determines whether the cell has been seen or not.
     * @param row The row index, 0 to height - 1
     * @param col The column index, 0 to width - 1
     * @return true or false
     */
    public boolean isSeen(int row, int col) {
        if (row < 0 || row >= height || col < 0 || col >= width) {
            return false;
        } else {
            return seen.get(row * width + col);
        }
    }

    /**
     * Determines whether the cell has been seen or not.
     * @param cell The cell
     * @return true or false
     */
    public boolean isSeen(Cell cell) {
        return isSeen(cell.row(), cell.col());
    }

    /**
     * Marks a cell seen.
     * @param row The row
     * @param col The column
     */
    public void markSeen(int row, int col) {
        if (row >= 0 && row < height && col >= 0 && col < width) {
            seen.set(row * width + col, true);
        }
    }

    /**
     * Marks seen all cells within the radius of the given cell.
     * @param here The cell
     * @param radius the radius in cells
     */
    public void markSeen(Cell here, int radius) {
        for (int r = here.row() - radius; r <= here.row() + radius; r++) {
            for (int c = here.col() - radius; c <= here.col() + radius; c++) {
                if (here.distance(new Cell(r,c)) <= radius + 1) {
                    markSeen(r, c);
                }
            }
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
     * Gets an info parameter, which must exist.
     * @param key The parameter's key
     * @return The value we found.
     */
    public String getInfo(String key) {
        return info.get(key).orElseThrow(() ->
            new IllegalArgumentException("Unknown info key: " + key));
    }

    /**
     * Gets the info parameter for an object, which must exist, e.g.,
     * the value "{key}.{suffix}"
     * @param key The object's key
     * @param suffix The value's suffix
     * @return The value we found.
     */
    public String getInfo(String key, String suffix) {
        return info.get(key, suffix).orElseThrow(() ->
            new IllegalArgumentException(
                "Unknown info key: " + key + "." + suffix));
    }

    /**
     * Get the entire info table.
     * @return The table.
     */
    public KeyDataTable info() {
        return info;
    }

    /**
     * Logs a single line of text to the user's screen.
     * @param text The text.
     */
    public void log(String text) {
        app.log(text);
    }

    /**
     * Gets the game's flags table
     * @return The table
     */
    public Flags flags() {
        return app.flags();
    }

    /**
     * Return a string that describes the content of the cell.
     * @param cell The cell
     * @return The string
     */
    public String describe(Cell cell) {
        // Mobile
        var mobile = findAt(cell, Mobile.class);

        if (mobile.isPresent()) {
            return "You see: " + mobile.get().label().text();
        }

        // Item: TODO could be several
        var itemStack = findAt(cell, ItemStack.class);

        if (itemStack.isPresent()) {
            if (itemStack.get().inventory().count() == 1) {
                return "You see: an item";
            } else {
                return "You see: some items";
            }
        }

        // Feature
        var feature = findAt(cell, Feature.class);

        if (feature.isPresent()) {
            return "You see: " + feature.get().label().text();
        }

        var tile = getTerrain(cell);
        return "You see: " + tile.description();
    }

    /**
     * Gets an NPC dialog for the given entity.  Subclasses can override to
     * provide custom dialogs.
     * @param id The entity ID
     * @return The dialog
     */
    public Optional<Dialog> findDialog(long id) {
        var entity = get(id);

        return entity.mannikin() != null
            ? Optional.of(new MannikinDialog(this, entity))
            : Optional.empty();
    }

    /**
     * Terrain Assessor function.  From a movement planning perspective, can
     * this mobile expect to be able to enter the given cell given its current
     * capabilities and the cell's content?
     *
     * TODO: At present, all mobiles can walk, and that's all they can do.
     *
     * @param mob The mobile entity
     * @param cell The cell in question
     * @return true or false
     */
    public boolean isPassable(Entity mob, Cell cell) {
        // FIRST, if there's a mobile blocking the cell, he can't enter.
        if (query(Mobile.class).anyMatch(m -> m.isAt(cell))) {
            return false;
        }

        // NEXT, otherwise it's a matter of the effective terrain and the
        // mover's capabilities.
        return getTerrainType(cell).isWalkable();
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
     * Returns a route from the mobile to the target cell that is passable to
     * the mobile in terms of Region::isPassable.  The route will be empty if
     * there's no way to get there.
     * @param mobile The mobile
     * @param target The target cell
     * @return The route
     */
    public List<Cell> findPassableRoute(
        Entity mobile,
        Cell target)
    {
        return Region.findRoute(c -> isPassable(mobile, c),
            mobile.cell(), target);
    }

    /**
     * Finds the distance from the mobile to the target cell using
     * Region::isPassable for this mobile. The distance will be
     * Integer.MAX_VALUE if there's no way to get there.
     * @param mobile The mobile
     * @param target The target cell
     * @return The distance
     */
    public int passableDistance(
        Entity mobile,
        Cell target)
    {
        return Region.distance(c -> isPassable(mobile, c),
            mobile.cell(), target);
    }

    /**
     * Gets whether the target cell is in the line of sight of the mobile.
     * @param mobile The mobile
     * @param target The target cell
     * @return true or false
     */
    public boolean isInLineOfSight(Entity mobile, Cell target) {
        // TODO: Do line of sight computation
        return true;
    }

    //-------------------------------------------------------------------------
    // Entity Factories

    /**
     * Makes a standard chest entity, given the key.  The entity has no
     * Loc.
     * @param key The key
     * @return The entity
     */
    public Entity makeChest(String key) {
        var chest = new Chest(
            key,
            "feature.chest",
            "feature.open_chest",
            Opening.CLOSED);
        return new Entity()
            .tagAsFeature()
            .chest(chest)
            .put(new Inventory(Chest.INVENTORY_SIZE))
            .terrain(TerrainType.FENCE);
    }

    /**
     * Makes a standard Exit entity, converting a "{regionName}:{pointName}"
     * string.  The entity has no Loc.
     * @param regionPoint A "{pointName}" or "{regionName}:{pointName} string.
     * @return The entity
     */
    public Entity makeExit(String regionPoint) {
        String[] tokens = regionPoint.split(":");

        if (tokens.length == 2) {
            return new Entity().exit(tokens[0], tokens[1]);
        } else if (tokens.length == 1) {
            return new Entity().exit(null, regionPoint);
        } else {
            throw new IllegalArgumentException("Invalid Exit name: \"" +
                regionPoint + "\"");
        }
    }

    public Entity makeItemStack() {
        return new Entity()
            .tagAsItemStack()
            .put(new Inventory(ItemStack.INVENTORY_SIZE))
            .label("Stack of items");
    }

    /**
     * Makes a standard Mannikin entity, getting its details from the
     * info table. The entity has no Loc.
     * @param key The mannikin's info key
     * @return The entity
     */
    public Entity makeMannikin(String key) {
        return new Entity()
            .tagAsFeature()
            .mannikin(key)
            .label(getInfo(key, "label"))
            .sprite(getInfo(key, "sprite"))
            .terrain(TerrainType.FENCE);
    }

    /**
     * Makes a standard narrative entity with a radius of 2 cells, getting its
     * details from the info table. The entity has no Loc.
     * @param key The narrative's info key
     * @return The entity
     */
    public Entity makeNarrative(String key) {
        var narrative = new Entity()
            .narrative(key);
        narrative.tripwire(
            new Trigger.RadiusOnce(2, key + ".triggered"),
            new Step.Interact(narrative.id()));
        return narrative;
    }

    /**
     * Makes a standard Point entity. The entity has no Loc.
     * @param name The Point's name.
     * @return The entity
     */
    public Entity makePoint(String name) {
        return new Entity().point(name);
    }

    /**
     * Makes a standard Sign entity, getting its details from the
     * info table. The entity has no Loc.
     * @param key The sign's info key
     * @return The entity
     */
    public Entity makeSign(String key) {
        return new Entity()
            .tagAsFeature()
            .sign(key)
            .label("sign")
            .terrain(TerrainType.FENCE)
            .sprite(getInfo(key, "sprite"));
    }
}
