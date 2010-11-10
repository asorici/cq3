package org.aimas.craftingquest.state;

import java.io.Serializable;

public class RoundState implements Serializable {

	public int currentRound;
	public int lastRound;
	public long startTime; // of the currentRound
	public long endTime; // of the currentRound

	public RoundState() {
	}
	
	public RoundState(int currentRound, int lastRound, int startTime, int endTime) {
		this.currentRound = currentRound;
		this.lastRound = lastRound;
		this.startTime = startTime;
		this.endTime = endTime;
	}
	
	public long currentTime() {
		return System.currentTimeMillis();
	}
	
	@Override
	public String toString() {
		String info = "RoundState:\n";
		info += "currentRound: " + currentRound + "\n";
		info += "lastRound: " + lastRound + "\n";
		info += "startTime: " + startTime + "\n";
		info += "endTime: " + endTime + "\n";
		return info;
	}
}
