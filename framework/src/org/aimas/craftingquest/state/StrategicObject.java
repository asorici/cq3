package org.aimas.craftingquest.state;

import org.aimas.craftingquest.state.StrategicResource.StrategicResourceType;

public abstract class StrategicObject extends CraftedObject {

	
	/**
	 * the id of the player that this tower belongs to
	 */
	protected int playerID;
	
	/**
	 * the position of this strategic resource
	 */
	Point2i position;
	
	/**
	 * the type of this strategic resource
	 */
	
	public StrategicObject(int playerID, Point2i position) {
		this.position = position;
		this.playerID = playerID;
	}

	public Point2i getPosition() {
		return position;
	}
	
	public int getPlayerID() {
		return playerID;
	}
	
	
	
}
