# Mobile
#entity, #component

## Entity

A mobile entity is an entity that can move on its own.  It is tagged with a `Mobile` component.  It will also have:

- A [[Cell]] component, its current location.
- A [[Sprite]] component, to render it visually.
- An [[Animation]] component, to animate its movement.

## Component

The `Mobile` component has the following data:

- A `name`, for debugging and/or display
- An `eventQueue`, to contain the mobile's events.

_Created on 2021-12-05._