package com.wjduquette.george.util;

import java.util.*;

/**
 * This static class implements the A* algorithm
 */
public record AStar2<Point>(
    Assessor<Point> assessor,
    NeighborFunc<Point> neighbors,
    DistanceFunc<Point> distFunc)
{
    public interface Assessor<Point>     { boolean isPassable(Point point); }
    public interface NeighborFunc<Point> { List<Point> get(Point point); }
    public interface DistanceFunc<Point> { double get(Point a, Point b); }

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
     * @return The route from start to goal, or the empty list
     */
    public List<Point> findRoute(Point start, Point goal) {
        // The set of nodes already evaluated
        Set<Point> closedSet = new HashSet<>();

        // The set of tentative nodes to be evaluated.
        Set<Point> openSet = new HashSet<>();
        openSet.add(start);

        // The map of navigated nodes
        Map<Point,Point> cameFrom = new HashMap<>();

        // Scores for the positions
        Map<Point,Double> gScore = new HashMap<>();
        Map<Point,Double> fScore = new HashMap<>();

        // Cost from start along best known path.
        gScore.put(start, 0.0);

        // Estimated total cost from start to goal through y
        fScore.put(start, 0.0 + distFunc.get(start, goal));

        while (openSet.size() > 0) {
            // FIRST, find the node with the best fScore in the open set.
            Point current = null;

            for (Point pos : openSet) {
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

            List<Point> candidates = neighbors.get(current).stream()
                .filter(p -> p.equals(goal) || assessor.isPassable(p))
                .toList();

            for (Point neighbor : candidates) {
                if (closedSet.contains(neighbor))
                    continue;

                double tentativeGScore =
                    gScore.get(current) + distFunc.get(current, neighbor);

                if (!openSet.contains(neighbor) ||
                    tentativeGScore <= gScore.get(neighbor))
                {
                    cameFrom.put(neighbor, current);
                    gScore.put(neighbor, tentativeGScore);
                    fScore.put(neighbor, gScore.get(neighbor) +
                        distFunc.get(neighbor, goal));

                    openSet.add(neighbor);
                }
            }
        }

        return null;
    }

    /**
     * Builds the actual route from the tree of routes computed by A*.
     *
     * @param cameFrom A* route tree
     * @param endPoint The last node in the route (i.e, the goal)
     * @return A list of positions leading from the start point to the goal.
     */
    private List<Point> reconstructRoute(
        Map<Point,Point> cameFrom,
        Point endPoint)
    {
        List<Point> route = new ArrayList<>(0);

        while (cameFrom.containsKey(endPoint)) {
            route.add(endPoint);
            endPoint = cameFrom.get(endPoint);
        }

        Collections.reverse(route);

        return route;
    }
}
