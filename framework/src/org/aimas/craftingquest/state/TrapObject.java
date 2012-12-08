package org.aimas.craftingquest.state;

public class TrapObject extends StrategicObject {
	
	public TrapObject(int playerID, Point2i pos) {
		super(playerID,pos);
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

}
