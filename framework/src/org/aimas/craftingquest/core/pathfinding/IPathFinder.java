package org.aimas.craftingquest.core.pathfinding;

import org.aimas.craftingquest.state.Point2i;
import org.aimas.craftingquest.state.UnitState;

/*
 * Path finding interface
 * Will return a path between the specified points, assuming it will be travelled by a unit of specified type.
 * Returned path is based on the current map knowledge of the player, unknown cells are considered as passable.
 */

public interface IPathFinder
{
	IPath computePath(Point2i from, Point2i to, IPathMap map, UnitState unit);
}
