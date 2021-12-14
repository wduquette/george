# Executor System

The Executor system is defined in `Executor.doMovement`, and handles the planned actions of [[Mobile]] entities.

- Each `Mobile` has a `plan`, which is a `Deque<Step>`.  Each step is an instance of the `Step` interface, a sealed interface; see `Step.java` for the defined kinds of step.
- When invoked, the system:
	- Identifies the "active" mobiles, those with planned steps.
	- For each, executes steps until there are no more steps or a step explicitly returns.
	- Any step may return an `Interrupt` for handling by the application.

_Created on 2021-12-06._