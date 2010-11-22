package org.aimas.craftingquest.user;

import org.aimas.craftingquest.core.Logger2;
import org.aimas.craftingquest.state.PlayerState;

public abstract class AIThread implements Runnable {
	// sync stuff
	private Object roundSync;
	private long roundDuration;
	private long roundStartTime;

	// communication
	private IPlayerActions cmd;

	public AIThread() {
	}

	void init(Object roundSync, IPlayerActions cmd) {
		this.roundSync = roundSync;
		this.cmd = cmd;
		this.roundDuration = cmd.getPlayerState().round.roundDuration;
	}

	@Override
	public final void run() {
		while (true) {
			// wait for new round
			synchronized (roundSync) {
				try {
					roundSync.wait();
					roundStartTime = System.currentTimeMillis();
				} catch (InterruptedException e) {
					e.printStackTrace();
					break;
				}
			}
			
			// do actions
			actIntelligently();
		}
	}

	protected void log(String where, String what) {
		Logger2.log("AI", where, what);
	}

	protected IPlayerActions getCmd() {
		return cmd;
	}

	protected PlayerState getPlayerState() {
		return cmd.getPlayerState();
	}

	protected final long getRemainingRoundTime() {
		return roundStartTime + roundDuration - System.currentTimeMillis();
	}

	protected abstract void initPlayer();

	protected abstract void actIntelligently();

}