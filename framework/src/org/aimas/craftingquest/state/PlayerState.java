package org.aimas.craftingquest.state;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 
 * @author Razvan
 */
public class PlayerState implements Serializable {

	/* state */
	public Integer id;
	
	public List<UnitState> units;
	public List<Blueprint> availableBlueprints;		// list of all available blueprints in the game - set at game start 
	public List<Blueprint> boughtBlueprints;		// blueprints that have been bought by the player
	public HashMap<Tower, Boolean> availableTowers; // stores the state of all the towers built by the player
	public int totalScore;
	public int credit;
	
	/* map dimensions */
	public int mapWidth;
	public int mapHeight;
	
	/* transition */
	public List<TransitionResult> responces;

	/* game mechanics time */
	public RoundState round;
	// long turnTimeRestart; /* future next start */
	
	public boolean isNewRound;

	public long currentTime() {
		return System.currentTimeMillis();
	}

	public PlayerState() {
		units = new ArrayList<UnitState>();
		responces = new ArrayList<TransitionResult>();
		boughtBlueprints = new ArrayList<Blueprint>();
		availableBlueprints = new ArrayList<Blueprint>();
		availableTowers = new HashMap<Tower, Boolean>();
		
		round = new RoundState();
	}

	@Override
	public String toString() {
		String info = "";
		info += "PlayerState: \n";
		info += "\t id: " + id + "\n";
		info += "\t Units: \n" + units;
		info += "\n";
		return info;
	}

	public int getTotalScore() {
		return totalScore;
	}

	public void setTotalScore(int totalScore) {
		this.totalScore = totalScore;
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
		if (!responces.isEmpty()) {
			TransitionResult tr = responces.get(responces.size() - 1);	// there should always be only one
			return tr.valid();											// pertaining to the last transition
		}
		
		return true;
	}
	
}
