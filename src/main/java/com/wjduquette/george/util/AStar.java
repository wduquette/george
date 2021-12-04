package com.wjduquette.george.util;

import com.wjduquette.george.ecs.Point;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * This static class implements the A* algorithm
 * @param <Point> The point value
 */
public class AStar<Point> {
    public interface IsPassable<Point> {
        boolean test(Point point);
    }

    public interface NeighborFunc<Point> {
        List<Point> get(Point point);
    }

    public interface DistanceFunc<Point> {
        double distance(Point a, Point b);
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
     * @param isPassable a function to determine the passability of a cell.
     * @param distFunc a function to compute the nominal distance between
     *        two cells
     * @param neighborsOf a function to return candicates for the next step
     * @return The route from start to goal, or the empty list
     */
    public List<Point> findRoute(
        Point start,
        Point goal,
        IsPassable isPassable,
        DistanceFunc distFunc,
        NeighborFunc neighborsOf)
    {
        // The set of nodes already evaluated
        Set<Point> closedSet = new HashSet<Point>();

        // The set of tentative nodes to be evaluated.
        Set<Point> openSet = new HashSet<Point>();
        openSet.add(start);

        // The map of navigated nodes
        Map<Point,Point> cameFrom = new HashMap<>();

        // Scores for the positions
        Map<Point,Double> gScore = new HashMap<>();
        Map<Point,Double> fScore = new HashMap<>();

        // Cost from start along best known path.
        gScore.put(start, 0.0);

        // Estimated total cost from start to goal through y
        fScore.put(start, 0.0 + distFunc.distance(start, goal));

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

            List<Point> neighbors = neighborsOf.get(current).stream()
                .filter(p -> p.equals(goal) || isPassable.test(p))
                .toList();

            for (Point neighbor : neighbors) {
                if (closedSet.contains(neighbor))
                    continue;

                double tentativeGScore =
                    gScore.get(current) + distFunc.distance(current, neighbor);

                if (!openSet.contains(neighbor) ||
                    tentativeGScore <= gScore.get(neighbor))
                {
                    cameFrom.put(neighbor, current);
                    gScore.put(neighbor, tentativeGScore);
                    fScore.put(neighbor, gScore.get(neighbor) +
                        distFunc.distance(neighbor, goal));

                    if (!openSet.contains(neighbor)) {
                        openSet.add(neighbor);
                    }
                }
            }
        }

        return null;
    }

    /**
     * Builds the actual route from the tree of routes computed by A*.
     *
     * @param cameFrom A* route tree
     * @param node The last node in the route (i.e, the goal)
     * @return A list of positions leading from the start point to the goal.
     */
    private List<Point> reconstructRoute(Map<Point,Point> cameFrom, Point node) {
        List<Point> route = new ArrayList<>(0);

        while (cameFrom.containsKey(node)) {
            route.add(node);
            node = cameFrom.get(node);
        }

        Collections.reverse(route);

        return route;
    }
}
