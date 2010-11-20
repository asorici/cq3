package org.aimas.craftingquest.user;

import org.aimas.craftingquest.core.Logger2;

public abstract class AIThread implements Runnable {
	// sync stuff
	private Object roundSync;
	private Boolean gameEnd;
	private long roundDuration;
	private long roundStartTime;
	
	// communication
	private IPlayerActions cmd;
	
	public AIThread(Object roundSync, Boolean gameEnd, IPlayerActions cmd) {
		this.roundSync = roundSync;
		this.gameEnd = gameEnd;
		this.cmd = cmd;
		this.roundDuration = cmd.getPlayerState().round.roundDuration; 
	}
	
	@Override
	public final void run() {
		while(true) {
			// first check for gameEnd
			synchronized(gameEnd) {
				if (gameEnd) {
					break;
				}
			}
			
			// do actions
			actIntelligently();
			
			// wait for new round
			synchronized(roundSync) {
				try {
					roundSync.wait();
					roundStartTime = System.currentTimeMillis();
				} catch (InterruptedException e) {
					e.printStackTrace();
					break;
				}
			}
		}
	}
	
	protected void log(String where, String what) {
		Logger2.log("AI", where, what);
	}

	protected IPlayerActions getCmd() {
		return cmd;
	}
	
	protected final long getRemainingRoundTime() {
		return roundStartTime + roundDuration - System.currentTimeMillis();
	}
	
	protected abstract void actIntelligently();
	
}