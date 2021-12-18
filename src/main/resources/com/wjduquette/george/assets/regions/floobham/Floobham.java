/**
 * 
 */
package rpg.world.floobham;

import graphics.ImageUtils;
import graphics.Items;
import graphics.Mobiles;
import graphics.SpriteImage;

import java.awt.Image;

import javax.swing.ImageIcon;

import map.Cell;
import map.TerrainTile;
import map.TerrainType;
import map.tiled.TiledMap;
import rpg.Condition;
import rpg.Item;
import rpg.KeyItem;
import rpg.Mannikin;
import rpg.Region;
import rpg.Shopkeeper;
import rpg.armor.LeatherShield;
import rpg.armor.PotHelmet;
import rpg.armor.Sandals;
import rpg.feature.PlainChest;
import rpg.item.Handkerchief;
import rpg.item.ScrollOfMagicMapping;
import rpg.item.Spittoon;
import rpg.item.VialOfHealing;
import rpg.pc.PlayerCharacter;
import rpg.pc.SirFred;
import rpg.weapon.Bow;
import rpg.weapon.Dagger;
import rpg.weapon.LongSword;
import rpg.weapon.SmallSword;
import rpg.world.Quests;
import util.StringsTable;
import app.Strings;

/**
 * @author will
 *
 */
public final class Floobham extends Region {
	private static final long serialVersionUID = 1L;

	/** This region's ID */
	public static final String ID = "floobham";
	
	// Points of Interest
	
	/** Entrance to the Sewers */
	public static final String SEWERS = "sewers";

	/** The Tasty Egg Maguffin */
	public static Item MAGUFFIN = new Maguffin();
	
	/** The strings table. */
	private static StringsTable strings;

	static {
		// FIRST, loads the strings.
		strings = Strings.register(ID, Floobham.class,"floobham.strings");
	}
	
	
	// Constructor
	
	/** Creates the region.	 
	 */
	public Floobham() {
		super(ID, 0);
		
		// FIRST, load the region Tiled map.
		TiledMap tm = TiledMap.read(Floobham.class, "floobham.json");
		
		setupRegion(tm, MyTile.values(), ID);
	}
	
	@Override public String title() { return "Floobham"; }

		

	@Override
	protected void setupObjectFeature(Cell cell, TiledMap.MapObject o) {
		switch (o.type) {
		case "Chest":
			PlainChest chest = new PlainChest();
			switch (o.name) {
			case "town_chest1":
			case "town_chest2":
				chest.putItem(new VialOfHealing());
				break;
			case "town_chest3":
				chest.putItem(new ScrollOfMagicMapping());
				break;
			}

			addFeature(cell, chest);
			break;
		default:
			// Handle unimplemented features in the usual way.
			super.setupObjectFeature(cell, o);
			break;
		}
	}

	@Override
	protected void setupMobile(Cell cell, TiledMap.MapObject o) {
		switch (o.name) {
		case "sirfred":
			addNPC(cell, new SirFredNPC());
			break;
		case "mayhem":
			addNPC(cell, new MagisterMayhem());
			break;
		case "jerome":
			addNPC(cell, new Jerome());
			break;
		case "fillip":
			addNPC(cell, new Fillip());
			break;
		case "arnulf":
			addNPC(cell, new Arnulf());
			break;
		case "gottleib":
			addNPC(cell, new Gottleib());
			break;
		case "hogworth":
			addNPC(cell, new Hogworth());
			break;
		case "cyneros":
			addNPC(cell, new Cyneros());
			break;
		default:
			// Handle unimplemented mobiles in the usual way.
			super.setupMobile(cell, o);
			break;
		}
	}
	

	//-------------------------------------------------------------------------
	// Methods for use by the Board
	
	// TBD.

	//-------------------------------------------------------------------------
	// Level Inner Classes
		
	/** A Tasty Egg Maguffin.
	 * @author will
	 */
	private static class Maguffin extends KeyItem {
		private static final long serialVersionUID = 1L;
		
		Maguffin() {
			super("Tasty Egg Maguffin", Items.TROPHY);
		}

		@Override
		public String getDescription() {
			return strings.get("item.maguffin");
		}
	}
	
	private class Arnulf extends Shopkeeper {
		private static final long serialVersionUID = 1L;
	
		Arnulf() {
			super("arnulf", Mobiles.THIEF, Floobham.ID);

			stocks(VialOfHealing.class);
			stocks(ScrollOfMagicMapping.class);
			stocks(Sandals.class);
			stocks(LeatherShield.class);
			stocks(PotHelmet.class);
			stocks(Dagger.class);
			stocks(SmallSword.class);
			stocks(LongSword.class);
			stocks(Bow.class);
			stocks(Handkerchief.class);
			stocks(Spittoon.class);
		}
		
		// TBD: Move these into the Shopkeeper class, using string().
		@Override
		public String getShopName() {
			return strings.get("arnulf.shopname");
		}
	
		@Override
		public String getDialogOnBuying() {
			return random.pickFrom(strings.strings("arnulf.buy*"));
		}
	
		@Override
		public String getDialogOnSelling() {
			return random.pickFrom(strings.strings("arnulf.sell*"));
		}
	}

	private class Fillip extends Mannikin {
		private static final long serialVersionUID = 1L;
	
		Fillip() {
			super("fillip", Mobiles.FILLMORE, Floobham.ID);
		}
		
		@Override
		public void talk(PlayerCharacter pc) {
			super.talk(pc);
	
			for (PlayerCharacter pc2 : board().party.members()) {
				if (pc2.isInjured()) {
					pc2.heal();
					pc2.log("is rested and well");
				}
			}
		}
	}

	private class Gottleib extends Mannikin {
		private static final long serialVersionUID = 1L;

		Gottleib() {
			super("gottleib", Mobiles.THIEF, Floobham.ID);
		}
		
		@Override
		public void talk(PlayerCharacter pc) {
			if (!board().quest.isCompleted(Quests.BUG_VALLEY)) {
				setGreeting(strings.get("gottleib.bug_valley"));
				board().quest.accept(Quests.BUG_VALLEY);
			} else {
				setGreeting(strings.get("gottleib.drunk"));
			}

			super.talk(pc);
		}
	}

	private class Hogworth extends Mannikin {
		private static final long serialVersionUID = 1L;

		Hogworth() {
			super("hogworth", Mobiles.PEASANT2, Floobham.ID);
		}
		
		@Override
		public void talk(PlayerCharacter pc) {
			if (!board().quest.isCompleted(Quests.UNHAPPY_PRINCESS)) {
				setGreeting(strings.get("hogworth.unhappy_princess"));
				board().quest.accept(Quests.UNHAPPY_PRINCESS);
			} else {
				setGreeting(strings.get("hogworth.happy"));
			}

			super.talk(pc);
		}
	}

	private class Jerome extends Mannikin {
		private static final long serialVersionUID = 1L;
	
		Jerome() {
			super("jerome", Mobiles.FRIAR, Floobham.ID);
		}
		
		@Override
		public void talk(PlayerCharacter pc) {
			for (PlayerCharacter pc2 : board().party.allMembers()) {
				if (!pc2.isAlive()) {
					pc2.revive();
					log("revived " + pc2);
				}
			}
			
			super.talk(pc);
		}
	}

	private class MagisterMayhem extends Mannikin {
		private static final long serialVersionUID = 1L;

		MagisterMayhem() {
			super("mayhem", Mobiles.DESK_WIZARD, Floobham.ID);
		}
		
		@Override
		public void talk(PlayerCharacter pc) {
			if (board().party.has(Floobham.MAGUFFIN)) {
				setGreeting(strings.get("mayhem.maguffin3"));
				board().quest.complete(Quests.MAGUFFIN);
				board().party.removeItem(Floobham.MAGUFFIN);
			} else if (!board().quest.isAccepted(Quests.MAGUFFIN)) {
				setGreeting(strings.get("mayhem.maguffin1"));
				board().quest.accept(Quests.MAGUFFIN);
			} else if (!board().quest.isCompleted(Quests.MAGUFFIN)) {
				setGreeting(strings.get("mayhem.maguffin2"));
			} else {
				setGreeting(strings.get("mayhem.nothing"));
			}

			super.talk(pc);
		}
	}

	private class SirFredNPC extends Mannikin {
		private static final long serialVersionUID = 1L;

		SirFredNPC() {
			super("sirfred", Mobiles.KNIGHT, Floobham.ID);
		}
		
		@Override
		public void talk(PlayerCharacter pc) {
			// FIRST, set the greeting
			if (!board().quest.isAccepted(Quests.MAGUFFIN)) {
				setGreeting(strings.get("sirfred.noquest"));
				board().quest.accept(Quests.SIR_FRED);
			} else {
				setGreeting(strings.get("sirfred.gotquest"));
			}
			
			// NEXT, have the conversation
			super.talk(pc);
			
			// NEXT, if George has the quest, Sir Fred joins the party.
			if (board().quest.isAccepted(Quests.MAGUFFIN)) {
				board().join(new SirFred());
				region().removeNPC(this);
				board().quest.complete(Quests.SIR_FRED);
			}
		}
	}

	private class Cyneros extends Mannikin {
		private static final long serialVersionUID = 1L;
		private boolean irritated = false;

		Cyneros() {
			super("cyneros", Mobiles.DARK_WIZARD, Floobham.ID);
		}
		
		@Override
		public void talk(PlayerCharacter pc) {
			if (!irritated) {
				setGreeting(strings.get("cyneros.greeting"));
				super.talk(pc);
				irritated = true;
			} else {
				setGreeting(strings.get("cyneros.newt"));
				super.talk(pc);
				pc.setCondition(Condition.NEWT,20);
			}
		}
	}

	
	
	//-------------------------------------------------------------------------
	// Tile Set
	
	private enum MyTile implements TerrainTile {
		// Terrain Tiles
		
		/** Unknown tile */     	UNKNOWN("Unknown", TerrainType.UNKNOWN),
		/** Tile floor */			TILE_FLOOR("Tile floor", TerrainType.FLOOR),
		/** Block wall */			BLOCK_WALL("Block wall", TerrainType.WALL),
		/** Cobble floor */			COBBLE_FLOOR("Cobblestone floor", TerrainType.FLOOR),
		/** Stone wall */			STONE_WALL("Stone wall", TerrainType.WALL),
		/** Water */				WATER("Water", TerrainType.WATER),
		/** Earth */				EARTH("Earth", TerrainType.FLOOR),
		/** Grass */				GRASS("Grass", TerrainType.FLOOR),
		/** Sand */                 SAND("Sand", TerrainType.FLOOR),
		/** Gravel */               GRAVEL("Gravel", TerrainType.FLOOR),
		/** Plank floor 1 */        PLANK1("Plank floor", TerrainType.FLOOR),
		/** Plank floor 2 */        PLANK2("Plank floor", TerrainType.FLOOR),
		
		// Feature Sprites
		/** Closed Door */			CLOSED_DOOR("Door"),
		/** Chest, closed */		CHEST("Chest"),
		/** Chest, open */          OPEN_CHEST("Open Chest"),
		/** Bed, head */            BED_HEAD("Bed"),
		/** Bed, foot */            BED_FOOT("Bed"),
		/** Altar, North */         ALTAR_NORTH("Altar"),
		/** Altar, Center */        ALTAR_CENTER("Altar"),
		/** Altar, South */         ALTAR_SOUTH("Altar"),
		/** Counter NS */           COUNTER_NS("Counter"),
		/** Counter EW */           COUNTER_EW("Counter"),
		/** Open Door */            OPEN_DOOR("Open Door"),
		/** Stairs Down */			STAIRS_DOWN("Stairs Down"),
		/** Stairs Up */            STAIRS_UP("Stairs Up"),
		/** Ladder Down */          LADDER_DOWN("Ladder Down"),
		/** Ladder Up */            LADDER_UP("Ladder Up"),
		/** Sign */                 SIGN("Sign");
		

		// Instance variables
		private String name;
		private TerrainType terrain;
		
		MyTile(String name, TerrainType terrain) {
			this.name = name;
			this.terrain = terrain;
		}
		
		// Use this for feature tiles.
		MyTile(String name) {
			this(name, TerrainType.UNKNOWN);
		}
		
		
		/** @return the tile's name. */
		@Override
		public String getName() {
			return name;
		}
		
		/** @return the tile's terrain type. */
		@Override
		public TerrainType getTerrain() {
			return terrain;
		}
		
		/** @return true if the tile is opaque, and false otherwise. */
		@Override
		public boolean isOpaque() {
			return terrain.opaque;
		}
		
		/** @return true if a mobile can walk on the tile, and false otherwise. */
		@Override
		public boolean isWalkable() {
			return terrain.walkable;
		}
		
		/** @return true if the a mobile can fly through the tile, and false otherwise. */
		@Override
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
			images = ImageUtils.loadTileSet(Floobham.class,"floobham.png");
		}
	}
}
