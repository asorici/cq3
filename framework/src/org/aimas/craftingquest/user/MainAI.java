package org.aimas.craftingquest.user;

import org.aimas.craftingquest.core.Client0;
import org.aimas.craftingquest.core.Logger2;

public class MainAI implements IPlayerHooks {

	/* game */
	private IPlayerActions cmd;
	private Object roundSync = new Object();
	private Boolean gameEnd = new Boolean(false);
	private Thread thread;
	private String playerClassName;
	
	public MainAI(IPlayerActions cmd) {
		this.cmd = cmd;
		cmd.addArtificialIntelligence(this);
	}
	
	@Override
	public void initGame() {
		log("initGame", "");
		
		try {
			AIThread aiThread = (AIThread)Class.forName(playerClassName).getConstructor(new Class[] {Object.class, Boolean.class, IPlayerActions.class}).newInstance(roundSync, gameEnd, cmd);
			thread = new Thread(aiThread);
			thread.start();
		} catch (Exception e) {
			e.printStackTrace();
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

	@Override
	public void beginRound() {
		// signal beginning of new round
		synchronized(roundSync) {
			roundSync.notify();
		}
	}

	void log(String where, String what) {
		Logger2.log("AI", where, what);
	}
	
	public static void main(String[] args) throws Exception {
		if (args == null || args.length != 5) {
			System.out.println("Insufficient arguments given. Ending client!");
			System.exit(1);
		}
		
		String host = args[0];
		int port = Integer.parseInt(args[1]);
		String serverName = args[2];
		long secret = Long.parseLong(args[3]); 
		
		if (secret == -1) {
			throw new Exception("client cannot retrieve secret");
		}
		
		Client0 client = new Client0(host, port, serverName, secret);
		MainAI ai = new MainAI(client);
	}
}

