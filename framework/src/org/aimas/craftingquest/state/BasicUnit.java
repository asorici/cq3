package org.aimas.craftingquest.state;

import java.io.Serializable;

import org.aimas.craftingquest.state.objects.ICrafted;
import org.aimas.craftingquest.state.resources.ResourceType;

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
	
	public void updateStats(int energy, int life, int attackLevel, int defenceLevel) {
		this.energy = energy;
		this.life = life;
		this.attackLevel = attackLevel;
		this.defenceLevel = defenceLevel;
	}
	
	@Override
	public String toString() {
		String info = "";
		info += "playerID=" + playerID + " unitId=" + unitId + " energy=" + energy + " life=" + life + 
				" attackLevel=" + attackLevel + " defenceLevel=" + defenceLevel;
		
		

		return info;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof BasicUnit)) {
			return false;
		}
		
		final BasicUnit other = (BasicUnit)obj;
		
		if (playerID != other.playerID) {
			return false;
		}
		
		if (unitId != other.unitId) {
			return false;
		}
		
		return true;
	}
	
	
	@Override
	public int hashCode() {
		return (playerID + "_" + unitId).hashCode();
	}
}
