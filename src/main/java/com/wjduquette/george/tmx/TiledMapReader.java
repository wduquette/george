package com.wjduquette.george.tmx;

import java.util.List;
import java.util.Optional;

import com.wjduquette.george.util.Resource;
import com.wjduquette.george.util.ResourceException;
import com.wjduquette.george.ecs.Cell;

import com.google.gson.Gson;

/**
 * A class representing a Tiled Map Editor tile map, as exported into
 * JSON format and loaded by GSon.  This is expected to be a transient
 * object, loaded, used to build the real data structures, and then
 * discarded; the instance variables are public and it has few convenience
 * methods.
 *
 * <p>All classes and instance variables are defined to match the Tiled
 * JSON schema; however, no attempt is made to capture everything
 * in the schema.</p>
 *
 * <p>See "devdocs/Tiled JSON Schema 1.6.md" for details.</p>
 * @author will
 */
public class TiledMapReader {
	/** The supported version of the Tiled JSON schema. */
	public static final String SCHEMA_VERSION = "1.6";

	private static final String TILE_LAYER = "tilelayer";
	private static final String OBJECT_GROUP = "objectgroup";

	//-------------------------------------------------------------------------
	// Nested Types

	/** A layer in the tile map */
	public static class Layer {
		public String name;           // The user-defined name
		public String type;           // "tilelayer" or "objectgroup"
		public int[] data;            // tilelayer: Array of tile GIDs
		public MapObject[] objects;   // objectgroup: Array of objects
		public Property[] properties; // Custom properties

		// Omitted fields: draworder, height, id, opacity, visible, width, x, y

		/**
		 * Gets the objects, for object groups.
		 * @return The list
		 */
		public List<MapObject> objects() {
			return objects != null ? List.of(objects) : List.of();
		}

		/**
		 * Gets the custom properties, if any.
		 * @return The list
		 */
		public List<Property> properties() {
			return properties != null ? List.of(properties) : List.of();
		}
	}

	/** An object in an "objectgroup" layer.  Objects are defined
	 * in pixel coordinates rather than tile coordinates. */
	public static class MapObject {
		public String name;            // The user-defined name
		public String type;            // The user-defined type
		public int x;                  // Upper-left x-coordinate, in pixels
		public int y;                  // Upper-left y-coordinate, in pixels.
		public int width;              // Width in pixels.
		public int height;             // Height in pixels.
		public Property[] properties;  // Custom properties

		// Omitted fields: id, rotation, visible

		/**
		 * Gets the custom properties, if any.
		 * @return The list
		 */
		public List<Property> properties() {
			return properties != null ? List.of(properties) : List.of();
		}
	}

	/** A Custom Property */
	public static class Property {
		public String name;  // The property name
		public String type;  // The property type
		public String value; // The value string
	}


	//-------------------------------------------------------------------------
	// Instance Variables

	// All names are as they appear in the .json input.  Do not change them!

	public String type;            // Should always be "map"
	public String version;         // The .json schema version
	public int height;             // Map height in tiles
	public int width;              // Map width in tiles.
	public int tileheight;         // Height of one tile, in pixels
	public int tilewidth;          // Width of one tile, in pixels
	public Layer[] layers;         // The array of Layer records
	public Property[] properties;  // Custom map properties

	// Omitted: infinite, nextlayerid, nextobjectid, orientation, renderorder,
	// tiledversion, tilesets

	//------------------------------------------------------------------------
	// Convenience Methods

	/**
	 * Gets the layers.
	 * @return The list
	 */
	public List<Layer> layers() {
		return layers != null ? List.of(layers) : List.of();
	}

	/** Returns the tile layer with the given name.
	 * @param name The name.
	 * @return A tile layer
	 */
	public Optional<Layer> tileLayer(String name) {
		return layers().stream()
			.filter(layer -> layer.type.equals(TILE_LAYER))
			.filter(layer -> layer.name.equals(name))
			.findFirst();
	}
	
	/**
	 * Returns the object group with the given name.
	 * @param name The name.
	 * @return The Layer
	 */
	public Optional<Layer> objectGroup(String name) {
		return layers().stream()
			.filter(layer -> layer.type.equals(OBJECT_GROUP))
			.filter(layer -> layer.name.equals(name))
			.findFirst();
	}

	/**
	 * Gets the custom properties, if any.
	 * @return The list
	 */
	public List<Property> properties() {
		return properties != null ? List.of(properties) : List.of();
	}

	//------------------------------------------------------------------------
	// Static Methods.
	
	/** Read a Tiled Map Editor tile map from disk.  The tile map must be
	 * saved in JSON format as a resource of a known class.  If the resource
	 * cannot be read, the application terminates.
	 * @param cls The class
	 * @param resource The resource name
	 * @return The TiledMapReader object.
	 */
	public static TiledMapReader read(Class<?> cls, String resource) {
		try {
			List<String> lines = Resource.getLines(cls, resource);
			String jsonText = String.join("\n", lines);

			// NEXT, parse the JSON.
			Gson gson = new Gson();

			return gson.fromJson(jsonText, TiledMapReader.class);
		} catch (ResourceException e) {
			throw e;
		} catch (Exception e) {
			throw new ResourceException(cls, resource, e);
		}
	}
}
