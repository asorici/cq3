package org.aimas.craftingquest.state;

import org.aimas.craftingquest.core.GamePolicy;

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
	public boolean equals(Object obj) {
		if (!(obj instanceof Tower)) {
			return false;
		}
		
		final Tower other = (Tower)obj;
		
		if (type != other.type) {
			return false;
		}
		
		if (position.x != other.position.x || position.y != other.position.y) {
			return false;
		}
		
		return true;
	}
}
