# Game Loop
#design, #pending

George is a turn-based game; but each move needs to be animated.  We get a basic ECS-style game loop, where periodically it pauses to wait for user input.

### The Game Loop

The game loop is always running.  Each time through the loop, it does the following:

- Calls the [[Animation System]] system to update any [[Animation|animations]].
	- [[Mobile|Mobiles]] and [[VisualEffect|Visual Effects]] can have animations.
- Call the [[Movement System]] to execute any events.
	- If a [[Mobile]]'s event queue has events in it, and the [[Mobile]]'s animation is `Stopped`, execute the next event.
		- Or, all of its events with the next time tag.
	- Executing an event can update any state: it can damage or kill a monster, start an  animation, change a cell location.
- If there were no events executed, call the [[Planning System]] to let the next mover plan their move.
	- If the mover is AI-controlled, compute its next move, i.e., plan and populate its event queue.
	- If the mover is player-controlled, set the flag that allows user-clicks to take effect.
		- On user-click, we'll compute the user's move and populate player character event queues.
- Call the [[Rendering System]] to repaint the screen.
- Call the [[Purging System]] to delete dead entities.
	- E.g., mobiles that have been killed; visual effects whose animations have stopped.


As an optimization, we can pause the game loop if there are no active animations and we are waiting for player input.

_Created on 2021-11-28._