# Supporting Geographic Algorithms
#design, #pending 

The big question is how to efficiently to geographic calculations.

For route-finding, for example, I need to be able to efficiently query the terrain between hither and yon.  I can't loop through the entity table each time I want to find a cell, so I'll need to compute some kind of lookup table.

There are three questions:

- What data do I need?
- How should it be stored?
- How should the stored data be maintained as the entity table changes?

The data I need will be determined by the systems I implement.  The other two questions are addressed here.

## Nut Shell

- Store fixed data in a cell array at load time.
- Store dynamic data in cell map(s) that are rendered in the game loop as needed, and passed to relevant systems.

## Data Storage

There are two basic options.

**Cell array**: Store data by cell in an array, where the cell coordinates map to an index.  (I could use a two-dimensional array; but really this is easier.)

**Cell map**: Store data by cell in a map, where the `Cell` is the key.  

I'm inclined to use cell maps, since they are flexible enough to cope with irregular and partial maps.

## Update Discipline

**Fixed**: Store the data once, on map load, and don't allow it to be changed as the game executes.  This is clearly wrong in general, given how many entities move around, but could possibly be used for the basic terrain data.

**Maintained**: Define the lookup tables at the same time as we populate the entity table.  Update them as needed on each change to the entity table.  This is tricky and annoying.

**Rendered**: Each time through the game loop, recreate all needed lookup tables and pass to all systems that require them.  These tables only need to cover the region around the player.  This is the easiest thing to do.  It's always right, and it doesn't require tricky logic.

**Conclusion**: If there is fixed data that never changes, it might be reasonable to save on load in a fixed cell array for easy lookup.  Other data should be rendered each time through the loop.


_Created on 2021-11-28._