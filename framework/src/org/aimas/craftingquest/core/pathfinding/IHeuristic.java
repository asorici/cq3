package org.aimas.craftingquest.core.pathfinding;

import org.aimas.craftingquest.state.Point2i;

public interface IHeuristic
{
	public float getCost(Point2i from, Point2i to);
}
