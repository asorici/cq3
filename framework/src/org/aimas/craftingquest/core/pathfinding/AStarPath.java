package org.aimas.craftingquest.core.pathfinding;

import java.util.*;
import org.aimas.craftingquest.state.Point2i;

public class AStarPath implements IPath
{
	/** The list of steps building up this path */
	private ArrayList<Step> steps;
	
	/**
	 * Create an empty path
	 */
	public AStarPath()
	{
		steps  = new ArrayList<Step>();
	}
	
	public int getLength()
	{
		return steps.size();
	}
	
	public Point2i getStepAt(int index)
	{
		Step st = steps.get(index);
		Point2i p2iStep = new Point2i(st.getX(), st.getY());
		return p2iStep;
	}
	
	public Step getStep(int index)
	{
		return steps.get(index);
	}
	
	public int getX(int index)
	{
		return getStep(index).x;
	}
	
	public int getY(int index)
	{
		return getStep(index).y;
	}
	
	public void appendStep(int x, int y)
	{
		steps.add(new Step(x,y));
	}

	public void prependStep(int x, int y)
	{
		steps.add(0, new Step(x, y));
	}
	
	public boolean contains(int x, int y)
	{
		return steps.contains(new Step(x,y));
	}
	
	public boolean removeFirstStep()
	{
		if (steps.size() > 0)
		{
			steps.remove(0);
			return true;
		}
		return false;
	}
	
	public Step getLastStep()
	{
		if (steps.size() > 0)
		{
			return steps.get(steps.size()-1);
		}
		return null;
	}
	
	public String toString()
	{
		String str = "";
		for (int i=0; i<steps.size(); ++i)
		{
			str += "["+steps.get(i).getX()+","+steps.get(i).getY()+"] ";
		}
		return str;
	}
	
	
	/* Step class */
	public class Step
	{
		private int x;
		private int y;
		
		public Step(int x, int y)
		{
			this.x = x;
			this.y = y;
		}
		
		public int getX()
		{
			return x;
		}

		public int getY()
		{
			return y;
		}
		
		public int hashCode()
		{
			return x*y;
		}

		public boolean equals(Object other)
		{
			if (other instanceof Step)
			{
				Step o = (Step) other;
				
				return (o.x == x) && (o.y == y);
			}
			
			return false;
		}
	}
}
