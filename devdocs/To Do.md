# To Do

My current To Do list.

- [x] Look at the Old George "World" map code and see if there's anything special I missed.
- [x] Add Exits to the entities table
- [ ] Add another region, e.g., Floobham, Bugland
- [ ] Support moving to another region, e.g., Floobham, Bugland
- [ ] Create global tile set solution
	- [ ] Tiles loaded from any region or tileset should be globally accessible by name.
	- [ ] Normal tiles and terrain tiles should share an interface. 
- [ ] Implement movement limits (the player can only move so far)
- [ ] Implement Mover list
	- Probably a `Mover` sealed type.  
	- `Mover.Leader(playerId)`
	- `Mover.Monsters()` (if all monsters move at the same time)
- [ ] Allow the Tiled map object for Signs to include a tile name for display. 
- [ ] Render log messages on the map as a heads up display. (?)
- [ ] Render quest status on the map as a heads-up display
- [ ] Script to copy asset files to `resources`
- [ ] Implement Old George's Line-of-Sight algorithm generically.
- [ ] Ponder [[Saving the Game]]
	

_Created on 2021-11-27._