package org.aimas.craftingquest.user;

import java.security.AccessControlException;

import org.aimas.craftingquest.core.Client0;
import org.aimas.craftingquest.state.PlayerState;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 */
public class MainAI implements IPlayerHooks {

	/* game */
	private IPlayerActions cmd;
	private Object roundSync = new Object();
	private Thread thread;
	private AIThread aiThread;
	private String playerClassName;

	/* logging */
	private static Logger logger = Logger.getLogger(MainAI.class);
	

	public MainAI(String playerClassName, String host, int port, String serverName, long secret) throws Exception {
		this.playerClassName = playerClassName;
		this.cmd = new Client0(host, port, serverName, secret);
		cmd.addArtificialIntelligence(this);
	}

	@Override
	public void initGame() {
		logger.info("[AI][initGame]");
		
		try {
			aiThread = (AIThread) Class.forName(playerClassName).newInstance();
			aiThread.init(roundSync, cmd);
		} catch (Exception e) {
			logger.fatal("[AI][initGame] Error initializing client", e);
			System.exit(-1);
		}
	}

	@Override
	public void initPlayer() {
		logger.info("[AI][initPlayer][begin]");
		if (aiThread != null) {
			try {
				aiThread.initPlayer();
			} 
			catch(AccessControlException s) {
				logger.fatal("CQ POLICY VIOLATION. SOLUTION WILL BE DISQUALIFIED.", s);
				System.exit(-1);
			}
			catch(SecurityException s) {
				logger.fatal("CQ POLICY VIOLATION. SOLUTION WILL BE DISQUALIFIED.", s);
				System.exit(-1);
			}
			catch(Exception e) {
				logger.fatal("[AI][initPlayer] Error initializing client player", e);
				System.exit(-1);
			}
			
			thread = new Thread(aiThread);
			thread.start();
		}

		logger.info("[AI][initPlayer][end]");
	}

	@Override
	public void beginRound() {
		// signal beginning of new round
		synchronized (roundSync) {
			roundSync.notifyAll();
		}
	}

	public void finishGame() {
		PlayerState pState = cmd.getPlayerState();
		if (pState != null) {
			logger.info("[AI][finishGame] score: " + pState.getScore());
		} else {
			logger.info("[AI][finishGame]");
		}

		System.exit(0);
	}

	public static void main(String[] args) throws Exception {
		if (args == null || args.length < 5) {
			System.out.println("Insufficient arguments given. Ending client!");
			System.exit(1);
		}
		
		String playerClassName = args[0];
		String host = args[1];
		int port = Integer.parseInt(args[2]);
		String serverName = args[3];
		long secret = Long.parseLong(args[4]);

		if (args.length == 6) {
			// install a security manager
			if (System.getSecurityManager() == null) {
				System.setProperty("java.security.policy", args[5]);
				System.setSecurityManager( new SecurityManager() );
			}
		}
		
		if (secret == -1) {
			throw new Exception("client cannot retrieve secret");
		}

		// configure loggin
		PropertyConfigurator.configure("logging.properties");
		
		// MainAI ai = new MainAI(playerClassName, host, port, serverName, secret);
		try {
			new MainAI(playerClassName, host, port, serverName, secret);
		} catch (Exception e) {
			logger.fatal("[MainAI][Connectivity or instantiation error]");
			e.printStackTrace();
			System.exit(2);
		}

	}

}
