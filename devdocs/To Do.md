# To Do

My current To Do list.

- [x] Ponder [[Supporting Geographic Algorithms]].
- [x] Revise `TiledMapReader` to read the current Tiled `.json` format.
- [x] Refactor `TiledMapReader` to be clean. 
- [x] `RegionMap` should save `TerrainTypes` to an array, not to `Terrain` components.
- [x] Add some objects, e.g., signs and other triggers.
- [ ] Revise `MapViewer` to only draw the "visible" area.
	- In terms of rectangular radius, not line-of-sight.
- [x] Allow George to be moved by cursor keys, taking terrain type into account.
- [ ] Allow George to be moved about the region by clicking, using A* for route finding, taking terrain type into account.
	- Requires defining the basic [[Game Loop]], goals, movers, efficient terrain access.
- [ ] Script to copy asset files to `resources`
- [ ] Found out whether Old George did line-of-sight?
- [ ] Define a basic `Board`, with `MapViewer` in the middle, allowing resize but with maximum map size shown.
- [ ] Allow George to interact with them.
- [ ] Ponder how to structure the game state for saving.
	- Could use GSon, so long as I'm using it anyway.
	- GSon plus Base64?

_Created on 2021-11-27._