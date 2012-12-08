package org.aimas.craftingquest.state;

/**
 * Describes the characteristics of the defense Tower type of strategic resources.
 * Towers belong to a player and have an indication of their remaining strength. 
 * Defense towers will protect cells falling within their radius from being exploited by the adversary.
 */
@SuppressWarnings("serial")
public class Tower extends StrategicObject {
	
	
	/**
	 * the remaining strength (expressed in energy points) of this tower
	 */
	private int remainingStrength;
	
	public Tower(int playerID, Point2i pos) {
		super(playerID,pos);
		this.remainingStrength = 300;
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
}
