# Tiled Map Editor
#design,

Tiled is a tile map editor available from www.mapeditor.org.  I used it in 
the original version of George's Saga; it remains a very popular editor.  It 
has many powerful features:

- Multiple layers of tiles and objects
- Objects can be annotated with a variety of metadata.

This document explains how we use Tiled's features to define region maps
for George's Saga.  See [[Region Maps]] for conventions on how George makes use of Tiled map data.

## JSON Schemas

These pages describe the schemas for Tiled's JSON map export.

- [[Tiled JSON Schema 1.0]], as used in Old George
- [[Tiled JSON Schema 1.6]], as used in New George


## Tiled Map Data

**Tile Sets**: Tiled works with an external tile set defined by a PNG file, like those created by PyxelEdit.  For convenience we define a tile set file for each region, containing the terrain and feature tiles needed by the region.

Tiled can load multiple tile sets; it assigns a unique _GID_ to each tile, starting at 1 for the first tile and incrementing from there.  Each tile set in the JSON has a `firstgid` that's the starting index for that tile set.  GID = 0 indicates the empty tile.

**Layers**: Most data in a Tiled map is on one _layer_ or another.  Tiled supports two kinds of layers:

- Tile layers, containing a vector of GIDs representing tiles in the map grid.  Empty cells are indicated by 0.
- Object groups, containing "object metadata" associated with rectangles.  The rectangle coordinates are in X/Y pixels.   

George uses tile layers to set the basic appearance of the map, and object groups to add metadata that the region's code can use to position mobiles, signs, and triggers.

## Accessing Tiled Map Data

A Java library, `libtiled`, is available to read Tiled `.tmx` files
directly.  It has many XML-related dependencies, and I've been unable to get it to work with IntelliJ under the Java module system.  I really don't want to pull in that many dependencies anyway.

Tiled can also export to JSON; this is how Legacy George read maps, and 
New George does the same.  See the `TiledMapReader` class.



# OLD STUFF



## Project Conventions

We use the following conventions in George's Saga. 

- **NOTE:** This describes the usage in Old George.  Everything is on the table in New George.
- **TODO:** This needs to be checked against the code and actual maps.

### The "Terrain" Tile Layer

The terrain map is implemented as a tile layer called `Terrain`.  Every
cell in the map should have a tile in this layer, corresponding to one
of the tiles in the map's tile set.

- **NOTE:** In Old George, every cell had to have a tile.  In New
  George, there's really no reason why maps can't be irregular.

### The "Features" Tile Layer

Additional features (i.e., statues, fountains, buildings) can be added to the map on the `Features` tile layer, using tiles found in the map's tile set.  This allows feature and terrain tiles to compose nicely; feature tiles often have transparent areas where the `Terrain` tile shows through.  Thus, the same feature tile can be used on grass, stone, brick, etc.

From Old George:

> By default, all such tiles will create Furniture objects: impassable features that are only present for looks.  
> 
> However, the region is free to capture particular feature tiles and do something special with them.  Door tiles, for example, might create standard Door objects, and Chests can create empty chests.
> 
> However, this requires that the relevant tiles are included in the region's tile set.  This is not always desirable.

It isn't yet clear how we will handle this in New George.  However, my expectation is that this layer should be almost entirely cosmetic; the cell's `TerrainType` should be determined by the `Terrain` layer if there is nothing on the `Features` layer, and by the `Feature` tile otherwise.

### The "Features" Object Group ###

More specific features, i.e., the chest containing the prize, traps, and
the like, can be defined as "objects" on an object layer also called
`Features`.

**TODO:** I have always hated that I reused the name `Features`.  That's got to be fixed.  Probably we call it `Objects`.

A Tiled object is a polygon on an object layer.  If an object covers only one tile, it appears in the map editor as a small square around the upper left corner of the tile;
this is how `Features` objects should be defined.

Objects can have attached metadata.  At present, each feature on the layer
should at least have a `name` associated with it; this will be used by
the region to place the actual object.  They will usually have a `type`
as well, which will sometimes be used.

From Old George:

> Note that there can only be one feature at a given spot; if there is a feature tile and a feature object, the feature object wins.

In New George, the tile layers should only determine terrain.  Objects determine other behavior.  Thus, there's no reason why we can't have both.

### The "Mobiles" Tile Layer

From Old George:

> Like Features, Mobiles can be placed on the map as tiles using a tile layer called "Mobiles".  This should be used sparingly, as it requires that the relevant tiles be included in the region-specific tile set.

This apparently wasn't used in Old George, and we won't use it in New George.  (Once I've verified that we weren't using it anywhere, this section can be deleted.) 

### The "Mobiles" Object Group ###

From Old George:

> Like Features, Mobiles can be placed on the map as objects in an object group called "Mobiles".  Each such object should have a "name", which the region will use to place the correct mobile.  This is the usual way to pre-place mobiles on a tile map.
>
> At present, all mobiles are placed by name by region-specific code. Eventually, some mobile types might get handled automatically.

From this description, it isn't clear whether I ever used this.  It would seem reasonable to have multiple object layers, though.

### The "Areas" Object Layer ###

An object layer called "Areas" can be used to define larger object polygons
used by the region.  This is all TBD, but uses might include:

- Turf: an area a particular mobile will protect, and not leave.
- Paths: A list of points that a mobile will traverse over and over, like a cop walking a beat.
- Areas of heightened danger.  For example, areas might determine the kinds of monsters that can appear, if any.

**TODO:** It would seem reasonable to support any number of object groups, of any name, using the `type` and `name` and possibly other attributes to control behavior.


## Quick Reference (OLD) ##

**NOTE:** This is all from Old George.  I need to verify how this is done, and record it in [[Region Map]].

The `Region` class does most of the work of translating a Tiled tile map.  This
section is a quick reference to the conventions used.

* Placing a Sign

  Features object, type="Sign", name="<strings table name>".
  TBD: If there's a Feature tile, use its appearance.

* Placing a Narrative

  Features object, type="Narrative", name="<strings table name>".
  The narrative will pop up the first time the player steps on the
  tile, or any other that uses the same name in this region.

* Placing an invisible Exit

  Features object, type="Exit", name="<region>:<point>".  The terrain
  tile is all that will appear, but the player will go to the given
  region and point.  Unless the object has an explicit "point" property,
  the "<region>" will be used to name the exit as a point of interest.

* Placing a visible Exit (e.g., a ladder down)

  Features tile with exit sprite (e.g., ladder down or tunnel entrance),
  plus Features object, type="Exit", name="<region>:<point>".  The Exit
  object will use the tile's name and sprite.  Unless the object has an
  explicit "point" property,
  the "<region>" will be used to name the exit as a point of interest.

* Placing a closed Door

  Features tile, name="Door".  Uses the sprites for the region's
  "Door" and "Open Door" tiles.

* Placing an open Door

  Features tile, name="Open Door".  Uses the sprites for the region's
  "Door" and "Open Door" tiles.

* Placing a closed empty PlainChest

  Features tile, name="Chest".  Uses the sprites for the region's
  "Chest" and "Open Chest" tiles.

* Placing an open empty PlainChest

  Features tile, name="Open Chest".  Uses the sprites for the region's
  "Chest" and "Open Chest" tiles.

* Defining a point of interest

  Add a Features object, type="Point", name="<point>".

  Add a Features object with property "point"="<point>".

  Add an Exit with no "point" property; its "<region>" will be used
  as a point of interest name.

* Placing a Mannikin with a fixed greeting

  Add a Mobiles object, type="Mannikin", name="<name>" with property
  "sprite=<spriteConstant>".  The displayed sprite is Mobiles.<spriteConstant>.
  The displayed name and greeting are "<name>.name" "<name>.greeting"
  from the strings table.  If there are multiple strings matching
  "<name>.greeting*", the mannikin will cycle through them randomly,
  over and over.



## Specific Entities ##

### Signs ###

Signs are placed on the map as objects on the Features layer.  The type should
be "Sign" and the name should be the sign's name in the region's strings file.

### NPCs ###

NPCs are placed on the map as objects on the Mobiles layer.  The type should be
"NPC" and the name should be the NPC's root name in the region's strings file.
