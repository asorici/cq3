package org.aimas.craftingquest.state;

import java.io.Serializable;

/**
 * Data about game rounds.
 *
 */
@SuppressWarnings("serial")
public class RoundState implements Serializable {

	/**
	 * the current game round
	 */
	public int currentRound;
	
	/**
	 * the total number of game rounds
	 */
	public int noRounds;
	
	/**
	 * the duration of a round in milliseconds
	 */
	public long roundDuration;		// number of milliseconds per round
	
	/**
	 * the startTime of the current round - relative to the start of the game
	 */
	public long startTime; 			// of the currentRound
	
	/**
	 * how much time is still left from the current round
	 */
	public long remainingTime;		// of the currentRound

	public RoundState() {
	}
	
	public RoundState(int currentRound, int noRounds, int roundDuration, int startTime) {
		this.currentRound = currentRound;
		this.noRounds = noRounds;
		this.startTime = startTime;
		this.roundDuration = roundDuration;
	}
	
	public long currentTime() {
		return System.currentTimeMillis();
	}
	
	@Override
	public String toString() {
		String info = "RoundState:\n";
		info += "currentRound: " + currentRound + "\n";
		info += "noRounds: " + noRounds + "\n";
		info += "duration: " + roundDuration + "\n";
		info += "startTime: " + startTime + "\n";
		
		return info;
	}
}
