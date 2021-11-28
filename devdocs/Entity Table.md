# Entity Table
#design, #pending

The `EntityTable` is the basic data structure for George's entities.  It is effectively an `ArrayList` of `Entity` objects; each `Entity` contains a `TypeMap` of the entity components.  (As a result, an entity can have only one component of a given class at a time).

Any object can be a component.  By convention, components are usually Java records; components are updated by being replaced with a new component of the same type.

The `Entity` class provides easy access to the standard components.

An `Entity` can mix-and-match the components it needs.  For the standard kinds of entity (terrain, feature, mobile, etc.) there will be a component that identifies the kind; it might have data of its own, or it might simply be a sentinel, to make queries easier.

## Queries

`EntityTable::query` takes one or more component `Class<?>` objects, and returns a stream of all entities that have all of the indicated classes.  This is the fundamental query.

There are other things I need to be able to do:

- Query/filter for all entities within a radius of a given cell.
- Turn a stream of entities into a [[CellMap]].


_Created on 2021-11-28._