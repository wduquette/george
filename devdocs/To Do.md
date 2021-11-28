# To Do

My current To Do list.

- [x] Ponder [[Supporting Geographic Algorithms]].
- [x] Revise `TiledMapReader` to read the current Tiled `.json` format.
- [ ] Script to copy asset files to `resources`
- [ ] Revise `MapViewer` to only draw the "visible" area.
	- In terms of rectangular radius, not line-of-sight.
	- Did Old George to line-of-sight?
- [ ] Define a basic `Board`, with `MapViewer` in the middle, allowing resize but with maximum map size shown.
- [ ] Allow George to be moved about the region by clicking, using A* for route finding, taking terrain type into account.
	- Requires defining the basic [[Game Loop]], goals, movers, efficient terrain access.
- [ ] Add some objects, e.g., signs and other triggers, and allow George to interact with them.
- [ ] Ponder how to structure the game state for saving.
	- Could use GSon, so long as I'm using it anyway.
	- GSon plus Base64?

_Created on 2021-11-27._