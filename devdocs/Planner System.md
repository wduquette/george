# Planner System

The `Planner` is the system that plans moves for the current "mover".  

- For player-controlled characters, it responds to the current `UserInput`.  
- For other mobiles, it will eventually respond to the mobile's action function.
- It operates by adding `Steps` to the mobile's `plan`.
- Plans are executed by the [[Executor System]].

_Created on 2021-12-13._