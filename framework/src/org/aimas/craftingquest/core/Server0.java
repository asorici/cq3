package org.aimas.craftingquest.core;

import gnu.cajo.invoke.Remote;
import gnu.cajo.utils.ItemServer;

import java.util.LinkedList;
import java.util.List;
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
	private List<Object> clients;
	//DisplayerFrame displayer;
	private GraphicInterface displayer;
	
	/* game */
	private GameState state;
	// private Scenario scenario;

	private Configuration cfg;
	private ActionEngine actionEngine;
	private Timer timer;
	private long[] secrets = new long[] { 1, 2, 3 };

	public Server0() {
		clients = new LinkedList<Object>();
		cfg = GameGenerator.readConfigFromFile();
		state = GameGenerator.setupGame();
		actionEngine = new ActionEngine(state);
		timer = new Timer();
		
		//displayer = new DisplayerFrame();
		displayer = new GraphicInterface(state);
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				displayer.setVisible(true);
			}
		});
		
		//displayer.beginDisplay(state);
	}

	public static void main(String[] args) throws Exception {
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
		for (int i = 0; i < clients.size(); i++) {
			final int clientID = i;
			timer.scheduleAtFixedRate(new TimerTask() {

				Object client = clients.get(clientID);

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

	private boolean allowedSecret(long secret) {
		for (int i = 0; i < secrets.length; i++) {
			if (secret == secrets[i]) {
				return true;
			}
		}
		return false;
	}

	private int secretToOrder(long secret) {
		return (int) secret / 100;
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
		Logger2.log("srv", "register", "newclient_" + secret);
		if (!clients.contains(client)) {
			clients.add(client);
		}
		int size = clients.size();

		return size - 1;
	}

	/*
	public Scenario getScenario() {
		return this.scenario;
	}
	*/
	
	public synchronized PlayerState process(Transition action) {
		Logger2.log("srv", "process", "");
		int playerID = secretToOrder(action.secret);
		PlayerState player = state.playerStates.get(playerID);
		actionEngine.process(player, action);
		player.round.currentRound++;
		return player;
	}
}
