package org.aimas.craftingquest.core.pathfinding;

import org.aimas.craftingquest.state.Point2i;

public interface IPath
{
	public int getLength();
	
	public Point2i getStepAt(int index);
}
