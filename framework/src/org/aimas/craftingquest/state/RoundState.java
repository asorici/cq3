package org.aimas.craftingquest.state;

import java.io.Serializable;

@SuppressWarnings("serial")
public class RoundState implements Serializable {

	public int currentRound;
	public int noRounds;
	public long roundDuration;		// number of milliseconds per round
	public long startTime; 			// of the currentRound
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
