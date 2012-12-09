package org.aimas.craftingquest.state.objects;

import java.io.Serializable;

import org.aimas.craftingquest.state.Blueprint;
import org.aimas.craftingquest.state.CellState;
import org.aimas.craftingquest.state.Point2i;

/**
 * Describes the characteristics of the defense Tower type of strategic resources.
 * Towers belong to a player and have an indication of their remaining strength. 
 * Defense towers will protect cells falling within their radius from being exploited by the adversary.
 */
@SuppressWarnings("serial")
public class Tower implements IStrategic, ICrafted, Serializable {
	
	private static final CraftedObjectType craftedObjectType = CraftedObjectType.TOWER;
	
	/**
	 * the remaining strength (expressed in energy points) of this tower
	 */
	private int remainingStrength;
	
	/**
	 * the blueprint that was used to create this tower; it contains the resources required to build the
	 * tower and the amount of gold nuggets required for an upgrade to the next level
	 */
	Blueprint blueprint;
	
	/**
	 * the id of the player that built the tower
	 */
	int playerID;
	
	/**
	 * the tower's position on the map
	 */
	Point2i position;
	
	/**
	 * <p>The tower sight. It is a fixed (2 * towerRadius + 1) x (2 * towerRadius + 1) 
	 * 	  array of {@link CellState} objects.</p>
	 * <p>The tower is positioned in the middle of the visibility array.</p>
	 * <p>If the tower is near the margins of the map, cells falling outside the boundaries will be null.</p>  
	 */
	public CellState[][] sight;
	
	public Tower(int playerID, Point2i position, Blueprint blueprint) {
		this.remainingStrength = blueprint.getInitialStrength();
		this.playerID = playerID;
		this.position = position;
		this.blueprint = blueprint;
	}


	public int getRemainingStrength() {
		return remainingStrength;
	}
	
	public void weakenTower(int amount) {
		remainingStrength -= amount;
	}
	
	@Override
	public String toString() {
		return "Tower of player(" + playerID + ") - strength:" + remainingStrength + "\n";
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Tower)) {
			return false;
		}
		
		final Tower other = (Tower)obj;
		
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
	
	public CellState[][] getSight() {
		return sight;
	}
}
