# CellMap

A `CellMap` is a data structure that lists entities by cell, e.g., `Map<Cell,List<Entity>>`.  The idea is to convert a stream of entities into a structure that is cell-addressable.  This form is useful when looking at a subset of the map cells.

An alternative would be an `ArrayList<ArrayList<Entity>` where the `Cell` is converted into an index into the outer array list.  This form would work best over the entire map.


_Created on 2021-11-28._