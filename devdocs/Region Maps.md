# Region Maps
#design

The `RegionMap` class is used to load a predefined region from resource files.  A region map requires:

- A `.region` file, which points at the following:
	- A `.terrain` file that defines the region's tile set; see the `TerrainTileSet` class.
		- This file will have an associated `.png` tile set file.
	- A `.strings` file that defines region-specific strings; see the `StringsTable` class.
	- A `.json` file containing an exported [[Tiled Maps|Tiled Map]].

## Tiled Map Layers and Objects

TODO: This is all preliminary, and applies to New George only.

The [[Tiled Maps|Tiled Map]] may contain the following tile layers:

- `Terrain`.  This is the basic map, consisting of opaque terrain tiles from the region's `.terrain` file.  It need not be regular.
- `Features`.  This layer can add feature tiles, also from the region's `.terrain` file, which will be composed onto the basic terrain. 

The terrain type of a cell will be that of the `Features` tile, if any, and that of the `Terrain` tile otherwise.  It is an error to place a `Features` tile where there's no underlying `Terrain` tile.

These layers are used _only_ to determine the appearance and terrain type of the cells.

TODO: Presumably the region code can modify the terrain by adjust the features layer in memory.

The [[Tiled Maps|Tiled Map]] may contain any number of object groups.

TODO: It isn't yet clear whether we will have specific kinds of content on named object groups, or whether we will allow any number of object groups and do everything based on the metadata of the actual objects.

## Object Types

This section will detail the object types New George supports, and the metadata required by each.

 
_Created on 2021-11-27._