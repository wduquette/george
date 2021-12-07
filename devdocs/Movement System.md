# Movement System

The Movement System is defined in `Movement.doMovement`, and handles the planned actions of [[Mobile]] entities.

- Each `Mobile` has a `steps` queue.  Each `step` is an instance of the `Step` interface, a sealed interface; see `Step.java` for the defined kinds of step.
- In `Movement.doMovement`, the system:
	- Identifies the "active" mobiles, those with planned steps.
	- For each, executes steps until there are no more steps or a step explicitly returns.

_Created on 2021-12-06._