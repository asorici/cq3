package org.aimas.craftingquest.core;

import gnu.cajo.invoke.Remote;
import gnu.cajo.utils.ItemServer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import org.aimas.craftingquest.gui.GraphicInterface;
import org.aimas.craftingquest.state.GameState;
import org.aimas.craftingquest.state.PlayerState;
import org.aimas.craftingquest.state.Transition;

/**
 * 
 * @author Razvan
 */
public class Server0 implements IServer {

	/* communication */
	//private List<Object> clients;
	private Object[] clients;
	private GraphicInterface displayer;
	
	/* game */
	private GameState state;
	private Configuration cfg;
	private ActionEngine actionEngine;
	private Timer timer;
	private long[] secrets;

	public Server0() throws Exception {
		cfg = GameGenerator.readConfigFromFile();
		state = GameGenerator.setupGame();
		actionEngine = new ActionEngine(state);
		timer = new Timer();
		secrets = readSecrets("secrets.txt");
		
		if (secrets == null) {
			throw new Exception("could not retrieve secrets");
		}
		
		if(state.playerStates.size() != secrets.length){
		    throw new Exception("each client must have a secret");
		}
		
		clients = new Object[secrets.length];
		
		displayer = new GraphicInterface(state);
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				displayer.setVisible(true);
			}
		});
		
	}

	private long[] readSecrets(String fileName) {
		long[] secrets = null;
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			int len = Integer.parseInt(br.readLine());
			secrets = new long[len];
			
			for (int i = 0; i < len; i++) {
				secrets[i] = Long.parseLong(br.readLine());
			}
			
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return null;
		}
		
		return secrets;
	}

	public static void main(String[] args) throws Exception {
		// treat args as secrets
		/*
		if(args.length < 1) {
		    throw new Exception("not enough secrets");
		}
		
		long[] secrets = new long[args.length];
		for (int i=0; i< args.length; i++) {
		    secrets[i] = Long.parseLong(args[i]);
		}
		*/
		
		Logger2.start();
		Server0 server = new Server0();

		Remote.config(null, 1198, null, 0);
		ItemServer.bind(server, "CraftingQuest");

		server.gameLoop();
	}

	void sendEvent(Object client, Event event) {
		try {
			Remote.invoke(client, "onEvent", event);
		} catch (Exception ex) {
			Logger.getLogger(Server0.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	void updateGame() {
		// increment turn number
		state.round.currentRound++;
		
		// update graphics
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				displayer.updateState();
			}
		});
	}

	void endGame() {
		for (Object client : clients) {
			sendEvent(client, new Event(Event.EventType.GameEnd));
		}
		// Remote.shutdown();
		System.exit(1234);
	}

	private void gameLoop() {
		long delay = 1000;// 4000 - (System.currentTimeMillis() % 1000);
		long delayPlayer = GamePolicy.playerTotalTime;
		long period = GamePolicy.roundTime;

		try {
			Thread.sleep(2000 - ((System.currentTimeMillis() - Logger2.t0) % 1000));
		} catch (InterruptedException ex) {
			Logger.getLogger(Server0.class.getName()).log(Level.SEVERE, null, ex);
		}

		// wait
		// if !both clients end
		// else...
		// player time
		// end
		// declare winner
		for (int i = 0; i < clients.length; i++) {
			final int clientID = i;
			timer.scheduleAtFixedRate(new TimerTask() {

				//Object client = clients.get(clientID);
				Object client = clients[clientID];

				@Override
				public void run() {
					// replenish player's units with their energy supplies
					actionEngine.replenishEnergy();
					
					// then subtract specific energy amount if the player is near some towers
					
					actionEngine.doTowerDrain(state, state.getPlayerIds().get(clientID));
					
					// send the NEW ROUND event to client
					sendEvent(client, new Event(Event.EventType.NewRound));
				}
			}, delay + delayPlayer * i, period);
		}

		// update time
		timer.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				updateGame();
			}
		}, delay + period - GamePolicy.updateTime, period);

		// close
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				timer.cancel();
				endGame();
			}
		}, GamePolicy.roundTime * GamePolicy.lastTurn);

	}

	private int allowedSecret(long secret) {
		for (int i = 0; i < secrets.length; i++) {
			if (secret == secrets[i]) {
				return i;
			}
		}
		return -1;
	}

	
	/* communication */
	public synchronized int addRemoteClient(Object client) {
		Long secret = null;
		try {
			secret = (Long) Remote.invoke(client, "getSecret", 0);
		} catch (Exception ex) {
			Logger.getLogger(Server0.class.getName()).log(Level.SEVERE, null,
					ex);
		}
		
		int clientNo = allowedSecret(secret);
		
		if(clientNo == -1){
		    Logger2.log("srv", "register", "newclient not authorized: secret = " + secret);
		    return -1;
		}
		
		Logger2.log("srv", "register", "newclient_" + secret);
		if (clients[clientNo] == null) {
		    clients[clientNo] = client;
		}
		
		return clientNo;	
	}
	
	public synchronized PlayerState process(Transition action) {
		Logger2.log("srv", "process", "");
		int clientID = allowedSecret(action.secret);
		if (clientID == -1) {
			return null;
		}
		
		Integer playerID = state.getPlayerIds().get(clientID);
		
		PlayerState player = state.playerStates.get(playerID);
		if (player.round.currentRound < state.round.currentRound) {
			player.round.currentRound = state.round.currentRound;
		}
		
		actionEngine.process(player, action);
		return player;
	}
}
