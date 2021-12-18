# Doors

Doors are tricky.

At base a door is a barrier that blocks passage unless open.  There are several kinds.

**Quest Barrier:** a cell that is impassable until the user has completed some particular quest.

**Obstacle:** a quest barrier that vanishes when the quest is completed.  For example, the castle gate might open, and forever stay open; the landslide might be blown away exploded and so be gone.  Obstacles are always special cases, and could be handled by changing or removing a named feature.

**Keyed Door:** is a door that can only be unlocked by the player when in possession of a _key item_.  Once unlocked it remains unlocked, and effectively becomes a normal door.  However, the user must open it explicitly.

**Normal Locked Door:** is a door with a lock that the player might be able to pick given the necessary skills, if I should happen to implement that.  Once picked it becomes a normal door.

**Normal Door:** an unlocked door that can be open or closed, and can be opened or closed by the player.

## Observations

Every door can be open or closed.

A closed door can be locked.

Every door can have a different sprite in each state, and needs to know what the sprites are so that it can switch them when opened/closed.  (I can't assume that there are only two door sprites.)

A locked door is associated with a [[Condition Variable]].  If "locked", the door is locked; if "unlocked", the door is unlocked.

## Sprite Handling

There are two possibilities: 

- An entity can determine its `imageInfo()` on request, based on its state.
	- I.e., it can just return its `Sprite` component, or compute a `Door` component's sprite base on its state.
- The game rules can change the `Sprite` component when the door is opened or closed.  The `Door` needs to know what they are.
	- This is, oddly, probably simpler.

## User Actions

This is tricky, too.  It's reasonable to open a door as the default action when you click on it.  It's unreasonable to close it again when you click on it, because more likely you just wanted to move to that space.  That means the player needs a way to signal their intentions:

- Right click interacts with target (I kind of like this one) and does alternate action (if any)
- Click tool, then click door
- Aim plus keypress
- Click and select open from pop up (or have a context sensitive toolbar that changes based on location)
- Or simply don't allow open doors to be closed.

Not at all sure what the right answer

## Conclusions

Obstacles are distinct from other doors (since they can't be open/closed).

Doors have:

- An open sprite 
- A closed sprite
- An open/closed state
- A locked sprite, possibly null
- A locked condition name, possibly null

A door is locked based on the value of the named condition.

Opening, closing, or unlocking the door's state will select a different sprite.
