package org.aimas.craftingquest.core.pathfinding;

public class AStarHeuristic
{
	public float getCost(int x, int y, int tx, int ty) {		
		float dx = Math.abs(tx - x);
		float dy = Math.abs(ty - y);
		
		float result = (float) (Math.max(dx, dy));
		
		return result;
	}
}
