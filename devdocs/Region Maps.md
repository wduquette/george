# Region Maps
#design

The `RegionMap` class is used to load a predefined region from resource files.  A region map requires:

- A `.region` file, which points at the following:
	- A `.terrain` file that defines the region's terrain tiles; see the `TerrainTileSet` class.
		- This file will have an associated `.png` tile set file.
	- A `.strings` file that defines region-specific strings; see the `StringsTable` class.
	- A `.json` file containing an exported [[Tiled Map Editor|Tiled Map]].

## Tiled Map Layers and Objects

The [[Tiled Map Editor|Tiled Map]] may contain the following tile layers:

- The `Terrain` layer.  This is the basic map, consisting of opaque terrain tiles from the region's `.terrain` file.  It need not be regular, i.e., cells can be empty.  
- Terrain cells are saved the `RegionMap`'s `terrain` array.
- The `Features` layer.  This layer can add feature tiles, also from the region's `.terrain` file, which will be composed onto the basic terrain. This is usually used for static terrain features.

These layers are used _only_ to determine the appearance and terrain type of the cells.

The terrain type of a cell will be that of the `Features` tile, if any, and that of the `Terrain` tile otherwise.

- Features go in the `entities` table.

The [[Tiled Map Editor|Tiled Map]] may contain any number of object groups.  At present New George doesn't ascribe any semantics to the names of the object groups, but will load whatever objects are defined, regardless of group.

Each object has a `type` and a `name`, and may have `properties`; these will determine how the object is used.

## Object Types

This section will detail the object types New George supports, and the metadata required by each.

- `Point`
	- A named point on the map.  The `name` is the name of the point.
	- George first appears at the point called `origin` on the Overworld map.
	- We also use points as arrival locations when George steps from one map to another.
- `Sign`
	- A sign the player can walk up to and read.  The `name` of the sign is the name  of a string from the region's strings table.
	- The sign is a feature with "sign" tile and no terrain effects.

 
_Created on 2021-11-27._