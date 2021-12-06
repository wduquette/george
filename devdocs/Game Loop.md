# Game Loop
#design, #pending

George is a turn-based game; but each move needs to be animated.  We get a basic ECS-style game loop, where periodically it pauses to wait for user input.

## The Game Loop

The game loop is always running.  Each time through the loop, it does something like the following:

- Call the [[Movement System]] to execute any events.
	- If a [[Mobile]]'s event queue has events in it, and the [[Mobile]] has no animation, execute events until there are no more or it has an animation
	- Executing an event can update any state: it can damage or kill a monster, start an  animation, change a cell location.
- If need be, call the [[Planning System]] to let the next mover plan their move.
	- Needed when the current mover has no events and no active animation.
	- If the mover is AI-controlled, compute its next move, i.e., plan and populate its event queue.
	- If the mover is player-controlled, set the flag that allows user-clicks to take effect.
		- On user-click, we'll compute the user's move and populate player character event queues.
- Call the [[Rendering System]] to repaint the screen.
	- This will render all [[Animation|Animations]], stationary mobiles, features, and the underlying terrain.
	- [[Mobile|Mobiles]] and [[VisualEffect|Visual Effects]] can have animations.
- Call the [[Purging System]] to delete dead entities.
	- E.g., mobiles that have been killed; visual effects whose animations have stopped.

## User Clicks

If the game loop is stopped, any user click will restart it.  If a player-controlled mobile is the mover, it may also affect the mover's behavior.

## Notes and Open Questions

- As an optimization, we can pause the game loop if there are no active animations and we are waiting for player input.
- How should we handle the jump to a different [[Region]]?
	- Presumably:
		- Prune the existing event queues and animations
		- Set the app's `currentRegion` to the new region
		- Position the player character(s) in the new region.
		- Repaint.
		- Wait for user input.

_Created on 2021-11-28._