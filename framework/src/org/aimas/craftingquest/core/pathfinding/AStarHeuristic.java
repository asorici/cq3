package org.aimas.craftingquest.core.pathfinding;

import org.aimas.craftingquest.state.Point2i;

public class AStarHeuristic implements IHeuristic
{
	public float getCost(Point2i from, Point2i to)
	{
		float dx = Math.abs(to.x - from.x);
		float dy = Math.abs(to.y - from.y);
		
		float result = (float) (Math.max(dx, dy));
		
		return result;
	}
}
