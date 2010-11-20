package org.aimas.craftingquest.user;

import org.aimas.craftingquest.core.Client0;
import org.aimas.craftingquest.core.Logger2;

/**
 *
 */
public class MainAI implements IPlayerHooks {

	/* game */
	private IPlayerActions cmd;
	private Object roundSync = new Object();
	private Boolean gameEnd = new Boolean(false);
	private Thread thread;
	private AIThread aiThread;
	private String playerClassName;
	
	public MainAI(String playerClassName, IPlayerActions cmd) {
		this.playerClassName = playerClassName;
		this.cmd = cmd;
		cmd.addArtificialIntelligence(this);
	}
	
	@Override
	public void initGame() {
		log("initGame", "");

		try {
			aiThread = (AIThread) Class.forName(playerClassName).newInstance();
			aiThread.init(roundSync, gameEnd, cmd);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void initPlayer() {
		log("initPlayer", "begin");
		if (aiThread != null) {
			aiThread.initPlayer();
			thread = new Thread(aiThread);
			thread.start();
		}
		
		log("initPlayer", "end");
	}
	
	@Override
	public void beginRound() {
		// signal beginning of new round
		synchronized(roundSync) {
			roundSync.notify();
		}
	}
	
	public void finishGame() {
		log("finishGame", "");
		
		// signal end of game
		synchronized(gameEnd) {
			gameEnd = true;
		}
		
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.exit(0);
	}

	void log(String where, String what) {
		Logger2.log("AI", where, what);
	}
	
	public static void main(String[] args) throws Exception {
		if (args == null || args.length != 5) {
			System.out.println("Insufficient arguments given. Ending client!");
			System.exit(1);
		}
		
		String playerClassName = args[0];
		String host = args[1];
		int port = Integer.parseInt(args[2]);
		String serverName = args[3];
		long secret = Long.parseLong(args[4]); 
		
		if (secret == -1) {
			throw new Exception("client cannot retrieve secret");
		}
		
		Client0 client = new Client0(host, port, serverName, secret);
		MainAI ai = new MainAI(playerClassName, client);
	}

}