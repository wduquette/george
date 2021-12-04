# Game Loop
#design, #pending

George is a turn-based game; but each move needs to be animated.  We get a basic ECS-style game loop, where periodically it pauses to wait for user input.

## Basics

In an Entity/Component/System architecture, an Entity is a data construct composed of one or more Components.  The components are data records with very little behavior of their own (basically, getters and computations based on their data).

The primary game logic is in the "systems", functions that operate on specific components of the entities.  A 3D real-time video game would have systems for acquiring user input (e.g., button state), physics updates, collision detection, damage assessment, and rendering.

These "systems" are called in sequence by the game loop:

```java
GameState state

while (true) {
    acquirePlayerInput(state);
	doMovement(state);
	detectCollisions(state);
	assessDamage(state);
	render(state);
}
```

Play is turn-based in George, not real time; but we can still animate movement, damage effects, and so forth.  (JavaFX is good at this.)

## Basics Design

- We define a game update method.
- Use a JavaFX Animation to call the game update method periodically.
- Each time through the loop, it decides what to do, updates the entity table, re-renders, etc.
- The update method can decide to pause the loop, waiting for more user input.
- The entity taking an action is the "Mover".
- Actions can be animated in steps.
	- Any non-movement action is a single step, e.g, casting a spell, quaffing a potion, shooting an error.
	- When a mobile moves from point A to point B along a route, each step on the route is one step.
	- The step itself can be animated over multiple calls to the update method.
- If the "Mover" is not controlled by the player, user-clicks are ignored for the duration of the move.
- If the "Mover" is controlled by the player, user-clicks during a step take effect at the end of the step.

The entity taking its turn is the "Mover".  It has a "Goal", which might or might not be saved in the entity table.  Each turn it executes its Goal, and updates it or removes it.

## Location

Where does the Game Loop reside?

_Created on 2021-11-28._