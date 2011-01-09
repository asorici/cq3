package org.aimas.craftingquest.user;

import java.io.FileWriter;
import java.io.IOException;
import java.security.AccessControlException;

import org.aimas.craftingquest.state.PlayerState;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public abstract class AIThread implements Runnable {
	// sync stuff
	private Object roundSync;
	private long roundDuration;
	private long roundStartTime;

	// communication
	private IPlayerActions cmd;

	/* logging */
	private static Logger logger = Logger.getLogger(AIThread.class);
	
	public AIThread() {
		PropertyConfigurator.configure("logging.properties");
	}

	void init(Object roundSync, IPlayerActions cmd) {
		this.roundSync = roundSync;
		this.cmd = cmd;
		this.roundDuration = cmd.getPlayerState().round.roundDuration;
	}

	@Override
	public final void run() {
		try {
			FileWriter fw = new FileWriter("someFile.txt");
			fw.write("policy test succeded.");
			fw.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
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
			try {
				actIntelligently();
			} 
			catch(AccessControlException s) {
				log("CQ POLICY VIOLATION. SOLUTION WILL BE DISQUALIFIED.", Level.FATAL);
				s.printStackTrace();
				System.exit(-1);
			}
			catch(SecurityException s) {
				log("CQ POLICY VIOLATION. SOLUTION WILL BE DISQUALIFIED.", Level.FATAL);
				s.printStackTrace();
				System.exit(-1);
			}
		}
	}

	protected void log(String message, Level level) {
		logger.log(level, "[AI]: " + message);
	}

	protected static Logger getLogger() {
		return logger;
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