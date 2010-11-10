package org.aimas.craftingquest.state;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Razvan
 */
public class PlayerState implements Serializable {

	/* state */
	public Integer id;
	public int testCounter;
	
	public List<UnitState> units;
	public List<Blueprint> knownBlueprints;
	public int totalScore;
	public int credit;

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
		knownBlueprints = new ArrayList<Blueprint>();
	
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
		TransitionResult tr = responces.get(responces.size() - 1);
		return tr.valid();
	}
	
}
