package org.aimas.craftingquest.state;

import org.aimas.craftingquest.core.GamePolicy;

@SuppressWarnings("serial")
public class Tower extends StrategicResource {
	private int playerID;
	private int remainingStrength;
	
	public Tower(int playerID, Point2i pos) {
		super(GamePolicy.StrategicResourceType.Tower, pos);
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
