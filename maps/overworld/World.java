/**
 * 
 */
package rpg.world;

import graphics.ImageUtils;
import graphics.SpriteImage;

import java.awt.Image;

import javax.swing.ImageIcon;

import map.TerrainTile;
import map.TerrainType;
import map.tiled.TiledMap;
import rpg.Region;
import util.StringsTable;
import app.Strings;

/** The World Map for George's Saga.
 * @author will
 *
 */
public final class World extends Region {
	private static final long serialVersionUID = 1L;

	/** This region's ID */
	public static final String ID = "world";
	
	// Points of Interest
	
	/** The origin point, where the party first appears. */
	public static final String ORIGIN = "origin";
	
	/** The entrance to Floob Castle. */
	//	public static final String FLOOB_CASTLE = "floob_castle";
	
	/** The entrance to Castle Arrrgh. */
	// public static final String CASTLE_ARRRGH = "castle_arrgh";
	
	/** The entrance to Arid Castle. */
	// public static final String ARID_CASTLE = "arid_castle";
	
	/** The entrance to the Black Forest. */
	// public static final String BLACK_FOREST = "black_forest";
	
	/** The entrance to the test dungeon. */
	public static final String TEST = "test";
		
	/** The strings table. */
	@SuppressWarnings("unused")
	private static StringsTable strings;

	static {
		// FIRST, loads the strings.
		strings = Strings.register(ID, World.class,"world.strings");
	}
	
	// Constructor
	
	/** Creates the region.	 
	 */
	public World() {
		super(ID, 1);
		
		// FIRST, load the region Tiled map.
		TiledMap tm = TiledMap.read(World.class, "world.json");
		
		setupRegion(tm, MyTile.values(), ID);
	}

	@Override public String title() { return "World"; }

	//-------------------------------------------------------------------------
	private enum MyTile implements TerrainTile {
		// Terrain Tiles
		
		UNKNOWN("Unknown", 					TerrainType.UNKNOWN),
		TILE_FLOOR("Tile floor", 			TerrainType.FLOOR),
		BLOCK_WALL("Block wall", 			TerrainType.WALL),
		COBBLE_FLOOR("Cobblestone floor", 	TerrainType.FLOOR),
		STONE_WALL("Stone wall", 			TerrainType.WALL),
		CASTLE11("Castle"),
		CASTLE12("Castle"),
		CASTLE13("Castle"),
		WATER("Water", 						TerrainType.WATER),
		EARTH("Earth", 						TerrainType.FLOOR),
		SAND("Sand", 						TerrainType.FLOOR),
		GRASS("Grass", 						TerrainType.FLOOR),
		FOREST_ENTRY("Forest", 				TerrainType.FLOOR),
		CASTLE21("Castle"),
		CASTLE22("Castle Entrance"),
		CASTLE23("Castle"),
		MOUNTAINS("Mountains", 				TerrainType.WALL),
		CAVE("Tunnel entrance"),
		FOREST_GATE("Forest Gate"),
		BRIDGE_EW_TOP("Bridge",             TerrainType.FLOOR),
		BLANK1, 
		TOWN1("Town"),
		TOWN2("Town Entrance"),
		TOWN3("Town"),
		BLANK2,
		BLANK3,
		BLANK4,
		BRIDGE_EW_BOTTOM("Bridge",          TerrainType.FLOOR),
		BRIDGE_NS_LEFT("Bridge",            TerrainType.FLOOR),
		BRIDGE_NS_RIGHT("Bridge",           TerrainType.FLOOR);
		
		// Instance variables
		private String name;
		private TerrainType terrain;
		
		/** Use this one for blanks. */
		MyTile() {
			this.name = "";
			this.terrain = TerrainType.UNKNOWN;
		}
		
		/** Use this one for features. */
		MyTile(String name) {
			this.name = name;
			this.terrain = TerrainType.UNKNOWN;
		}
		
		/** Use this one for actual terrain. */
		MyTile(String name, TerrainType terrain) {
			this.name = name;
			this.terrain = terrain;
		}
		
		/** @return the tile's name. */
		public String getName() {
			return name;
		}
		
		/** @return the tile's terrain type. */
		public TerrainType getTerrain() {
			return terrain;
		}
		
		/** @return true if the tile is opaque, and false otherwise. */
		public boolean isOpaque() {
			return terrain.opaque;
		}
		
		/** @return true if a mobile can walk on the tile, and false otherwise. */
		public boolean isWalkable() {
			return terrain.walkable;
		}
		
		/** @return true if the a mobile can fly through the tile, and false otherwise. */
		public boolean isFlyable() {
			return terrain.flyable;
		}
		
		/** @return the sprite's image. */
		public Image image() {
			return images[this.ordinal()].image();
		}
		
		public Image bigImage() {
			return images[this.ordinal()].bigImage();
		}
		
		/** @return the sprite's image as an icon */
		public ImageIcon icon() {
			return images[this.ordinal()].icon();
		}

		/** @return the sprite's image as a magnified icon */
		public ImageIcon bigIcon() {
			return images[this.ordinal()].bigIcon();
		}

		// Static Data

		/** The list of tile images, read from the disk. */
		static private SpriteImage[] images;
		
		static {
			images = ImageUtils.loadTileSet(World.class,"world.png");
		}
	}
}
