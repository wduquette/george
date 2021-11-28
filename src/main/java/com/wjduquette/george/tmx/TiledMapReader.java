package com.wjduquette.george.tmx;

import java.util.List;

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

	/** Returns the tile layer with the given name, or null if no
	 * such layer is found.
	 * TODO: Use Optional
	 * @param name The name.
	 * @return A tile layer, or null.
	 */
	public Layer tileLayer(String name) {
		for (Layer layer : layers) {
			if (layer.type.equals("tilelayer") && layer.name.equals(name)) {
				return layer;
			}
		}
		
		return null;
	}
	
	/** Returns the object group with the given name, or null if no
	 * such layer is found.
	 * TODO: Use Optional
	 * @param name The name.
	 * @return A Layer, or null.
	 */
	public Layer objectGroup(String name) {
		for (Layer x : layers) {
			if (x.name.equals(name) && x.type.equals("objectgroup")) {
				return x;
			}
		}
		
		return null;
	}
	
	/** Given a MapObject, return the Cell of its upper left corner,
	 * using the map's tile size.
	 * TODO: Move to RegionMap
	 *
	 * @param object A MapObject
	 * @return A Cell coordinate
	 */
	public Cell getObjectCell(MapObject object) {
		return new Cell(object.y / tileheight, object.x / tilewidth);
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
