package org.aimas.craftingquest.state;

import java.io.Serializable;

import org.aimas.craftingquest.core.GamePolicy.StrategicResourceType;


public class StrategicResource implements Serializable {
	Point2i position;
	StrategicResourceType type;
	
	public StrategicResource(StrategicResourceType type, Point2i position) {
		this.type = type;
		this.position = position;
	}

	public Point2i getPosition() {
		return position;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof StrategicResource)) {
			return false;
		}
		
		final StrategicResource other = (StrategicResource)obj;
		
		if (type != other.type) {
			return false;
		}
		
		if (position.x != other.position.x || position.y != other.position.y) {
			return false;
		}
		
		return true;
	}

	public StrategicResourceType getType() {
		return type;
	}
}
