package org.aimas.craftingquest.state;

import java.io.Serializable;

/**
 * The class describes the way in which a player's unit is seen by an opponent
 *
 */
@SuppressWarnings("serial")
public class BasicUnit implements Serializable {
	public int id;
	
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
	
	public void set( int playerID, int energy) {
		this.playerID = playerID;
		this.energy = energy;
	}
}
