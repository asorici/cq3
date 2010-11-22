package org.aimas.craftingquest.state;

/**
 * Describes the characteristics of the defense Tower type of strategic resources.
 * Towers belong to a player and have an indication of their remaining strength. 
 * Defense towers will protect cells falling within their radius from being exploited by the adversary.
 */
@SuppressWarnings("serial")
public class Tower extends StrategicResource {
	/**
	 * the id of the player that this tower belongs to
	 */
	private int playerID;
	
	/**
	 * the remaining strength (expressed in energy points) of this tower
	 */
	private int remainingStrength;
	
	public Tower(int playerID, Point2i pos) {
		super(StrategicResourceType.Tower, pos);
		this.playerID = playerID;
		this.remainingStrength = 300;
	}

	public int getPlayerID() {
		return playerID;
	}
	
	public int getRemainingStrength() {
		return remainingStrength;
	}
	
	public void weakenTower(int amount) {
		remainingStrength -= amount;
	}
	
	@Override
	public String toString() {
		String info = "";
		
		info += "Tower of player(" + playerID + ") - strength:" + remainingStrength + "\n";
		
		return info;
	}
}
