# Game Loop
#design, #pending

George is a turn-based game; but each move needs to be animated.  We get a basic ECS-style game loop, where periodically it pauses to wait for user input.

## Design

- Use a JavaFX Animation to call the game loop periodically.
- The loop function can decide to pause the loop, waiting for more user input.
- User clicks should be disabled while the current turn is executing.
- Each time through the loop, it decides what to do, updates the entity table, re-renders, etc.

The entity taking its turn is the "Mover".  It has a "Goal", which might or might not be saved in the entity table.  Each turn it executes its Goal, and updates it or removes it.

_Created on 2021-11-28._