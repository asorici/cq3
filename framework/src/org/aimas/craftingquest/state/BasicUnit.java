package org.aimas.craftingquest.state;

import java.io.Serializable;

import org.aimas.craftingquest.state.UnitState.UnitType;
/**
 * The class describes the way in which a player's unit is seen by an opponent
 *
 */
@SuppressWarnings("serial")
public class BasicUnit implements Serializable {
	public int id;
	
	/**
	 * the unit type
	 */
	public UnitType type;
	
	/**
	 * the units energy reserves
	 */
	public int energy;
	
	/**
	 * the player that it belongs to
	 */
	public int playerID;
	
	public BasicUnit (){
	}
	
	public void set(UnitType type, int playerID, int energy) {
		this.type = type;
		this.playerID = playerID;
		this.energy = energy;
	}
}
