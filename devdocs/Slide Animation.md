# Slide Animation

An `Animation.Slide(id,speed,target,step)` animates the slide of a Tile from its current cell to the target cell.  The `id` is the ID of the Tile's entity.  The `speed` is a multiplier, nominally 1.0.  The `target` is the target `Cell`.

Suppose the Animation isn't a record, it's an object.  It's created with the two cell locations and the speed. It has a base rate in cell coordinates.  It computes a number of steps; at each step it will add a deltaX and a deltaY in cell coordinates to the TileOffset of the given Tile, and increment its step count. 

_Created on 2021-12-07._