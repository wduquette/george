package com.wjduquette.george.util;

import com.wjduquette.george.ecs.Cell;

import java.util.*;

/**
 * This static class implements the A* algorithm
 */
public class AStar {
    private AStar() {} // Not instantiable

    public interface Assessor {
        boolean isPassable(Cell point);
    }

    /**
     * The A* algorithm, as described at Wikipedia.  Finds an efficient route
     * from the starting point to the goal, if one exists. The algorithm
     * is agnostic as to the relationship between the points. The computed route
     * does not include the starting point.
     *
     * <p>TODO: Consider replacing isPassable with a cost function.</p>
     *
     * @param start	The starting point (usually "here")
     * @param goal The point to go to.
     * @param assessor a function to determine the passability of a cell.
     * @return The route from start to goal, or the empty list
     */
    public List<Cell> findRoute(
        Cell start,
        Cell goal,
        Assessor assessor)
    {
        // The set of nodes already evaluated
        Set<Cell> closedSet = new HashSet<>();

        // The set of tentative nodes to be evaluated.
        Set<Cell> openSet = new HashSet<>();
        openSet.add(start);

        // The map of navigated nodes
        Map<Cell,Cell> cameFrom = new HashMap<>();

        // Scores for the positions
        Map<Cell,Double> gScore = new HashMap<>();
        Map<Cell,Double> fScore = new HashMap<>();

        // Cost from start along best known path.
        gScore.put(start, 0.0);

        // Estimated total cost from start to goal through y
        fScore.put(start, 0.0 + start.cartesian(goal));

        while (openSet.size() > 0) {
            // FIRST, find the node with the best fScore in the open set.
            Cell current = null;

            for (Cell pos : openSet) {
                if (current == null || fScore.get(pos) < fScore.get(current)) {
                    current = pos;
                }
            }

            // NEXT, if it's the goal, then we are there.
            if (current.equals(goal)) {
                return reconstructRoute(cameFrom, goal);
            }

            openSet.remove(current);
            closedSet.add(current);

            List<Cell> neighbors = getAdjacent(current).stream()
                .filter(p -> p.equals(goal) || assessor.isPassable(p))
                .toList();

            for (Cell neighbor : neighbors) {
                if (closedSet.contains(neighbor))
                    continue;

                double tentativeGScore =
                    gScore.get(current) + neighbor.cartesian(current);

                if (!openSet.contains(neighbor) ||
                    tentativeGScore <= gScore.get(neighbor))
                {
                    cameFrom.put(neighbor, current);
                    gScore.put(neighbor, tentativeGScore);
                    fScore.put(neighbor, gScore.get(neighbor) +
                        neighbor.cartesian(goal));

                    openSet.add(neighbor);
                }
            }
        }

        return null;
    }

    // Get the adjacent cells.  Don't worry about map boundaries;
    // the Assessor will handle that.
    private List<Cell> getAdjacent(Cell cell) {
        List<Cell> result = new ArrayList<>();

        for (int r = cell.row() - 1; r <= cell.row() + 1; r++) {
            for (int c = cell.col() - 1; c <= cell.col() + 1; c++) {
                result.add(new Cell(r, c));
            }
        }
        result.remove(cell);

        return result;
    }

    /**
     * Builds the actual route from the tree of routes computed by A*.
     *
     * @param cameFrom A* route tree
     * @param endPoint The last node in the route (i.e, the goal)
     * @return A list of positions leading from the start point to the goal.
     */
    private static List<Cell> reconstructRoute(
        Map<Cell,Cell> cameFrom,
        Cell endPoint)
    {
        List<Cell> route = new ArrayList<>(0);

        while (cameFrom.containsKey(endPoint)) {
            route.add(endPoint);
            endPoint = cameFrom.get(endPoint);
        }

        Collections.reverse(route);

        return route;
    }
}
