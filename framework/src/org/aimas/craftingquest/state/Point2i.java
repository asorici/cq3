package org.aimas.craftingquest.state;

import java.io.Serializable;

/**
 * 
 */
@SuppressWarnings("serial")
public class Point2i implements Serializable {

	public int x;
	public int y;

	public Point2i(){
	}
	
	public Point2i(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	@Override
	public String toString() {
		String info = "(" + x + ", " + y + ")";
		return info;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
	
	public boolean isEqual(Point2i point) {
		if (point.x == x && point.y == y) {
			return true;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return y * 512 + x;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		
		if (!(obj instanceof Point2i)) {
			return false;
		}
		
		final Point2i other = (Point2i)obj;
		
		if (this.x != other.x || this.y != other.y) {
			return false;
		}
		
		return true;
	}
}
