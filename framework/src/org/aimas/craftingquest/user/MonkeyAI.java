package org.aimas.craftingquest.user;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.aimas.craftingquest.core.Client0;
import org.aimas.craftingquest.core.Logger2;
import org.aimas.craftingquest.state.CellState;
import org.aimas.craftingquest.state.PlayerState;
import org.aimas.craftingquest.state.Point2i;
import org.aimas.craftingquest.state.UnitState;

public class MonkeyAI implements IPlayerHooks {

	/* agent */
	int id;

	/* game */
	IPlayerActions cmd;
	Object roundSync = new Object();
	Boolean gameEnd = new Boolean(false);
	AIThread aithread;
	
	public MonkeyAI(IPlayerActions cmd) {
		this.cmd = cmd;
		aithread = new AIThread(roundSync, gameEnd, cmd);
		cmd.addArtificialIntelligence(this);
	}
	
	@Override
	public void initGame() {
		log("initGame", "");
		aithread.start();
	}

	public void finishGame() {
		log("finishGame", "");
		
		// signal end of game
		synchronized(gameEnd) {
			gameEnd = true;
		}
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
		if (args == null || args.length != 4) {
			System.out.println("Insufficient arguments given. Ending client!");
			System.exit(1);
		}
		
		//int clientNum = Integer.parseInt(args[0]);
		//long secret = readSecret("secrets.txt", clientNum);
		
		String host = args[0];
		int port = Integer.parseInt(args[1]);
		String serverName = args[2];
		long secret = Long.parseLong(args[3]); 
		
		if (secret == -1) {
			throw new Exception("client cannot retrieve secret");
		}
		
		Client0 client = new Client0(host, port, serverName, secret);
		MonkeyAI ai = new MonkeyAI(client);
	}
}

class AIThread extends Thread {
	// sync stuff
	private Object roundSync;
	private Boolean gameEnd;
	
	// communication
	IPlayerActions cmd;
	
	// player data
	PlayerState state;
	Random rnd;
	
	public AIThread(Object roundSync, Boolean gameEnd, IPlayerActions cmd) {
		this.roundSync = roundSync;
		this.gameEnd = gameEnd;
		this.cmd = cmd;
		this.rnd = new Random(System.currentTimeMillis());
	}
	
	@Override
	public void run() {
		while(true) {
			// first check for gameEnd
			synchronized(gameEnd) {
				if (gameEnd) {
					break;
				}
			}
			
			// do actions
			doAI();
			
			// wait for new round
			synchronized(roundSync) {
				try {
					roundSync.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
					break;
				}
			}
		}
	}
	
	private void doAI() {
		log("beginRound", "state  + cmd.getPlayerState()");
		state = cmd.getPlayerState();
		
		int nrUnits = state.units.size();
		
		for (int i = 0; i < nrUnits; i++) {
			UnitState unit = state.units.get(i);
			log("unit state", unit.toString());
			state = cmd.move(unit, smartChoiceDst(getValidPoints(unit)));
		}
	}
	
	private void log(String where, String what) {
		Logger2.log("AI", where, what);
	}

	private boolean allowedTerrain(UnitState unit, CellState cell) {
		return true;// cell==null || cell.type != CellState.CellType.DeepWater;
	}

	private List<Point2i> getValidPoints(UnitState unit) {
		List<Point2i> cells = new ArrayList<Point2i>();
		Point2i minP = new Point2i(Math.max(0, unit.pos.x - 1), Math.max(0,
				unit.pos.y - 1));
		Point2i maxP = new Point2i(Math.min(state.mapWidth - 1,
				unit.pos.x + 1), Math.min(state.mapHeight - 1,
				unit.pos.y + 1));

		for (int i = minP.x; i <= maxP.x; i++) {
			for (int j = minP.y; j <= maxP.y; j++) {
				Point2i p = new Point2i(i, j);
				if (allowedTerrain(unit, null) && !unit.pos.equals(p)) {
					cells.add(p);
				}
			}
		}
		return cells;
	}

	private Point2i smartChoiceDst(List<Point2i> cells) {
		if (cells.size() == 0) {
			return null;
		}
		
		return cells.get(rnd.nextInt(cells.size()));
	}
}