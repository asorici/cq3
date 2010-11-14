package org.aimas.craftingquest.state;

import java.io.Serializable;

import org.aimas.craftingquest.state.UnitState.UnitType;

public class BasicUnit implements Serializable {
	public int id;
	public UnitType type;
	public int energy;
	public int playerID;
	
	public BasicUnit (){
	}
	
	public void set(UnitType type, int playerID, int energy) {
		this.type = type;
		this.playerID = playerID;
		this.energy = energy;
	}
}
