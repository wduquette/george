# To Do

My current To Do list.

- [x] Ponder [[Supporting Geographic Algorithms]].
- [x] Revise `TiledMapReader` to read the current Tiled `.json` format.
- [x] Refactor `TiledMapReader` to be clean. 
- [x] `RegionMap` should save `TerrainTypes` to an array, not to `Terrain` components.
- [x] Add some objects, e.g., signs and other triggers.
- [x] Revise `MapViewer` to only draw the "visible" area.
- [x] Allow George to be moved by cursor keys, taking terrain type into account.
- [x] Implement generic A* algorithm
- [x] Draw A* route (to verify that we're doing it right)
- [x] Implement Looper using JavaFX Timeline.
	- Is running or stopped.
	- Has an update interval in milliseconds
	- Calls the game update function.
	- NOTE: Might be able to use Transitions for transient effects.
- [ ] Allow George to be moved about the region by clicking, using A* for route finding, taking terrain type into account.
	- Requires defining the basic [[Game Loop]], goals, movers, efficient terrain access.
- [ ] Render player status on the map as a heads-up display
- [ ] Render quest status on the map as a heads-up display
- [ ] Render log messages on the map as a heads up display.
- [ ] Render signs as a blocking popup...drawn on the map until the user clicks.
- [ ] Remove cursor key code.
- [ ] Script to copy asset files to `resources`
- [ ] Implement Old George's Line-of-Sight algorithm generically.
- [ ] Define a basic `Board`, with `MapViewer` in the middle, allowing resize but with maximum map size shown.
- [ ] Allow George to interact with signs.
- [ ] Ponder how to structure the game state for saving.
	- Could use GSon, so long as I'm using it anyway.
	- GSon plus Base64?

_Created on 2021-11-27._