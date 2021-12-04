package com.wjduquette.george.util;

import java.util.*;

/**
 * This static class implements the A* algorithm
 */
public class AStar {
    private AStar() {} // Not instantiable

    /**
     * A Point is the index of a map cell (tile, hex, etc.) on a map.
     */
    public interface Point<P extends Point<P>> {
        /** Computes the nominal (e.g., cartesian) distance between this point
         * and some other point.
         * @param other The other point.
         * @return the distance.
         */
        double distance(P other);

        /**
         * Returns the points adjacent to this point according to whatever
         * indexing scheme is used.  The points need not be in-bounds on
         * the map in question; the Assessor will handle that by marking
         * out-of-bounds points impassable.
         * @return The list of candidate neighbors.
         */
        List<P> getAdjacent();
    }

    /**
     * The Assessor is the algorithm's view of the map, <b>for the purposes
     * of the current route.</b>  Many factors can determine whether a cell
     * is impassable:
     *
     * <ul>
     *     <li>The point is out-of-bounds</li>
     *     <li>Terrain features</li>
     *     <li>Presence of enemies</li>
     *     <li>Capabilities of the "mover"</li>
     * </ul>
     */
    public interface Assessor<P extends Point<P>> {
        boolean isPassable(P point);
    }

    /**
     * The A* algorithm, as described at Wikipedia.  Finds an efficient route
     * from the starting point to the goal, if one exists. The computed route
     * does not include the starting point.
     *
     * <p>TODO: Consider replacing isPassable with a cost function.</p>
     *
     * @param start	The starting point (usually "here")
     * @param goal The point to go to.
     * @param assessor The terrain assessor function
     * @return The route from start to goal, or the empty list
     */
    public static <P extends Point<P>> List<P> findRoute(
        P start,
        P goal,
        Assessor<P> assessor)
    {
        // The set of nodes already evaluated
        Set<P> closedSet = new HashSet<>();

        // The set of tentative nodes to be evaluated.
        Set<P> openSet = new HashSet<>();
        openSet.add(start);

        // The map of navigated nodes
        Map<P,P> cameFrom = new HashMap<>();

        // Scores for the positions
        Map<P,Double> gScore = new HashMap<>();
        Map<P,Double> fScore = new HashMap<>();

        // Cost from start along best known path.
        gScore.put(start, 0.0);

        // Estimated total cost from start to goal through y
        fScore.put(start, 0.0 + start.distance(goal));

        while (openSet.size() > 0) {
            // FIRST, find the node with the best fScore in the open set.
            P current = null;

            for (P pos : openSet) {
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

            List<P> neighbors = current.getAdjacent().stream()
                .filter(p -> p.equals(goal) || assessor.isPassable(p))
                .toList();

            for (P neighbor : neighbors) {
                if (closedSet.contains(neighbor))
                    continue;

                double tentativeGScore =
                    gScore.get(current) + current.distance(neighbor);

                if (!openSet.contains(neighbor) ||
                    tentativeGScore <= gScore.get(neighbor))
                {
                    cameFrom.put(neighbor, current);
                    gScore.put(neighbor, tentativeGScore);
                    fScore.put(neighbor,
                        gScore.get(neighbor) + neighbor.distance(goal));

                    openSet.add(neighbor);
                }
            }
        }

        return new ArrayList<>();
    }

    /**
     * Builds the actual route from the tree of routes computed by A*.
     *
     * @param cameFrom A* route tree
     * @param endPoint The last node in the route (i.e, the goal)
     * @return A list of positions leading from the start point to the goal.
     */
    private static <P extends Point<P>> List<P> reconstructRoute(
        Map<P,P> cameFrom,
        P endPoint)
    {
        List<P> route = new ArrayList<>(0);

        while (cameFrom.containsKey(endPoint)) {
            route.add(endPoint);
            endPoint = cameFrom.get(endPoint);
        }

        Collections.reverse(route);

        return route;
    }
}
