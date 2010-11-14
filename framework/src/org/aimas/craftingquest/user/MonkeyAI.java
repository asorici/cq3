package org.aimas.craftingquest.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.aimas.craftingquest.core.Client0;
import org.aimas.craftingquest.core.GamePolicy;
import org.aimas.craftingquest.core.Logger2;
import org.aimas.craftingquest.state.CellState;
import org.aimas.craftingquest.state.PlayerState;
import org.aimas.craftingquest.state.Point2i;
import org.aimas.craftingquest.state.UnitState;

public class MonkeyAI implements IPlayerHooks {

	/* agent */
	int id;
	Random rnd;

	/* game */
	// Scenario scenario;
	IPlayerActions cmd;

	public MonkeyAI(IPlayerActions cmd) {
		this.cmd = cmd;
	}

	/*
	public void start() {
		cmd.addArtificialIntelligence(this);
	}
	*/
	
	@Override
	public void initGame() {
		log("initGame", "");
		rnd = new Random(System.currentTimeMillis());
		//scenario = cmd.getScenario();
		//displayer.beginDisplay(cmd);
	}

	public void finishGame() {
		log("finishGame", "");
	}

	@Override
	public void beginRound() {
		log("beginRound", "state  + cmd.getPlayerState()");
		PlayerState state = cmd.getPlayerState();

		for (int i = 0; i < state.units.size(); i++) {
			UnitState unit = state.units.get(i);
			log("unit state", unit.toString());
			state = cmd.move(unit, smartChoiceDst(getValidPoints(unit)));
		}
	}

	void log(String where, String what) {
		Logger2.log("AI", where, what);
	}

	boolean allowedTerrain(UnitState unit, CellState cell) {
		return true;// cell==null || cell.type != CellState.CellType.DeepWater;
	}

	List<Point2i> getValidPoints(UnitState unit) {
		List<Point2i> cells = new ArrayList<Point2i>();
		Point2i minP = new Point2i(Math.max(0, unit.pos.x - 1), Math.max(0,
				unit.pos.y - 1));
		Point2i maxP = new Point2i(Math.min(GamePolicy.mapsize.x - 1,
				unit.pos.x + 1), Math.min(GamePolicy.mapsize.y - 1,
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

	Point2i smartChoiceDst(List<Point2i> cells) {
		if (cells.size() == 0) {
			return null;
		}
		return cells.get(rnd.nextInt(cells.size()));
	}

	public static void main(String[] args) throws Exception {
		Client0 client = new Client0(Long.parseLong(args[0]));
		MonkeyAI ai = new MonkeyAI(client);
		client.addArtificialIntelligence(ai);
	}
}
