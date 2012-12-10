package org.aimas.craftingquest.state;

import java.io.Serializable;

/**
 * The class describes the way in which a player's unit is seen by an opponent
 *
 */
@SuppressWarnings("serial")
public class BasicUnit implements Serializable {
	public int unitId;
	
	/**
	 * the units energy reserves
	 */
	public int energy;

	/**
	 * the unit's life (maximum energy).
	 */
	public int life;
	
	/**
	 * the player that it belongs to
	 */
	public int playerID;

	/**
	 * the level of the equiped attack item.
	 */
	public int attackLevel;

	/**
	 * the level of the equiped defence item.
	 */
	public int defenceLevel;
	
	public BasicUnit (){
	}
	
	public void set(int unitId, int playerID, int energy, int life, int
			attackLevel, int defenceLevel) {
		this.unitId = unitId;
		this.playerID = playerID;
		this.energy = energy;
		this.life = life;
		this.attackLevel = attackLevel;
		this.defenceLevel = defenceLevel;
	}
}
