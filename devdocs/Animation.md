# Animation
#component 

An animation is a component that indicates something for the rendering system to do during its animation phase.  This may involve moving a [[Sprite|Sprites]], displaying a bit of NPC dialog, etc.

The rendering system can pause the game loop as part of displaying an animation (e.g., a bit of NPC dialog).

- An animation can animate multiple sprites.
- The implementation is unclear, but we know the following:
	- It must contain the data needed by the Animation/Rendering system to update the sprites.

_Created on 2021-12-05._