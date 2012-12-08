package org.aimas.craftingquest.state.objects;

import java.io.Serializable;

import org.aimas.craftingquest.state.Blueprint;
import org.aimas.craftingquest.state.Point2i;

@SuppressWarnings("serial")
public class TrapObject implements IStrategic, ICrafted, Serializable {
	
	private static final CraftedObjectType craftedObjectType = CraftedObjectType.TRAP;
	
	/**
	 * the remaining strength (expressed in energy points) of this tower
	 */
	Blueprint blueprint;
	int playerID;
	Point2i position;
	
	public TrapObject(int playerID, Point2i position, Blueprint blueprint) {
		this.playerID = playerID;
		this.position = position;
		this.blueprint = blueprint;
	}


	@Override
	public String toString() {
		return "Trap placed by player(" + playerID + ")\n";
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof TrapObject)) {
			return false;
		}
		
		final TrapObject other = (TrapObject)obj;
		
		if (playerID != other.playerID) {
			return false;
		}
	
		if (position.x != other.position.x || position.y != other.position.y) {
			return false;
		}
		
		return true;
	}


	@Override
	public int getLevel() {
		return blueprint.getLevel();
	}


	@Override
	public CraftedObjectType getType() {
		return craftedObjectType;
	}

	@Override
	public Blueprint getBlueprint() {
		return blueprint;
	}

	@Override
	public Point2i getPosition() {
		return position;
	}

	@Override
	public int getPlayerID() {
		return playerID;
	}
	
	@Override
	public int hashCode() {
		return blueprint.hashCode();
	}
}
