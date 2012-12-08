package org.aimas.craftingquest.state;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.aimas.craftingquest.state.objects.Tower;

/**
 * The central client state class. It contains information about the player's progress in the game.
 */
@SuppressWarnings("serial")
public class PlayerState implements Serializable {

	/* state */
	/**
	 * the unique player identifier 
	 */
	public Integer id;
	
	/**
	 * the list of <code>UnitStates</code> - the player's units
	 */
	public List<UnitState> units;
	
	/**
	 * the list of game wide available blueprints. 
	 * <p>This structure will be initialized automatically at start up.</p>
	 * <p>It contains all the blueprints available for purchase in the game. </p>
	 * <p>Players know this list from the very beginning, but cannot construct an object 
	 * unless they <b>buy</b> the necessary blueprint</p>
	 */
	public List<Blueprint> availableBlueprints;		// list of all available blueprints in the game - set at game start 
	
	/**
	 * the list of blueprints bought by this player
	 */
	public List<Blueprint> boughtBlueprints;		// blueprints that have been bought by the player
	
	/**
	 * A HashMap that contains the status for each tower constructed by the player
	 */
	public HashMap<Tower, Boolean> availableTowers; // stores the state of all the towers built by the player
	public int credit;
	
	/* map dimensions */
	public int mapWidth;
	public int mapHeight;
	
	/* transition */
	// public List<TransitionResult> responces;
	/**
	 * the TransitionResult response for the last transition. It will be null if no transition was made.
	 */
	public TransitionResult response;				// last transition response

	/* game mechanics time */
	/**
	 * the round information
	 */
	public RoundState round;

	public long currentTime() {
		return System.currentTimeMillis();
	}

	public PlayerState() {
		units = new ArrayList<UnitState>();
		//responces = new ArrayList<TransitionResult>();
		boughtBlueprints = new ArrayList<Blueprint>();
		availableBlueprints = new ArrayList<Blueprint>();
		availableTowers = new HashMap<Tower, Boolean>();
		
		round = new RoundState();
	}

	@Override
	public String toString() {
		String info = "";
		info += "PlayerState: \n";
		info += "    id:" + id + "\n";
		info += "    credit:" + credit + "\n";
		info += "    round:" + round.currentRound + " of " + round.noRounds + "\n";
		info += "    Blueprints: \n";
		for (Blueprint bp: boughtBlueprints) {
			info += "        " + bp + "\n";
		}
		
		info += "    Units:" + units.size() + "\n";
		for (UnitState unit : units) {
			info += "        " + unit + "\n";
		}
		
		return info;
	}

	public int getCredit() {
		return credit;
	}

	public void setCredit(int credit) {
		this.credit = credit;
	}

	public int getCurentTurn() {
		return round.currentRound;
	}

	public void setCurentTurn(int curentTurn) {
		round.currentRound = curentTurn;
	}
	
	public boolean validLastTransition() {
		/*
		if (!responces.isEmpty()) {
			TransitionResult tr = responces.get(responces.size() - 1);	// there should always be only one
			return tr.valid();											// pertaining to the last transition
		}
		*/
		
		if (response != null) {
			return response.valid();
		}
		
		return true;
	}
	
	public String getLastTransitionError() {
		/*
		if (!responces.isEmpty()) {
			TransitionResult tr = responces.get(responces.size() - 1);	// there should always be only one
			return tr.toString();										// pertaining to the last transition
		}
		*/
		
		if (response != null) {
			return response.toString();
		}
		
		return "";
	}
	
}
