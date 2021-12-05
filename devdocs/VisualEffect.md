# VisualEffect
#entity, #component

## Entity

A visual effect entity is an entity with a `VisualEffect` component.  It will also have:

- A [[Sprite]] component, to display
- An [[Animation]] component, to update its sprite.
- Possibly, a [[Cell]] component

The visual effect plays out over time, and is pruned when the [[Animation]] completes.  It has no effect on other game state.

## Component

A `VisualEffect` component is a simple component meant to tag an entity as a visual effect for searches.  It has the following data:

- A `name`, mostly as a debugging aid.

## Open Question

Should visual effects have a [[Cell]], or should they use [[Sprites]] with absolute pixel coordinates?

_Created on 2021-12-05._