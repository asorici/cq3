package org.aimas.craftingquest.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.aimas.craftingquest.state.CellState;
import org.aimas.craftingquest.state.PlayerState;
import org.aimas.craftingquest.state.Point2i;
import org.aimas.craftingquest.state.UnitState;

public class DummyAI extends AIThread {
	Random rnd;
	
	public DummyAI() {
		rnd = new Random();
	}

	@Override
	protected void initPlayer() {
		
	}
	
	@Override
	protected void actIntelligently() {
		log("beginRound", "state  + cmd.getPlayerState()");
		PlayerState player = getPlayerState();
		
		//boolean doStuff = true;
		//while(doStuff);
		
		int nrUnits = player.units.size();
		
		for (int i = 0; i < nrUnits; i++) {
			UnitState unit = player.units.get(i);
			log("unit state", unit.toString());
			
			player = getCmd().move(unit, smartChoiceDst(getValidPoints(unit)));
			if (player.validLastTransition()) {
				System.out.println(" ==== Last transition valid ==== ");
			}
			else {
				System.out.println(" ==== Last transition NOT valid ==== ");
				System.out.println(player.getLastTransitionError());
			}
		}

	}

	boolean allowedTerrain(UnitState unit, CellState cell) {
		return true;		// very optimistically :)
	}

	List<Point2i> getValidPoints(UnitState unit) {
		List<Point2i> cells = new ArrayList<Point2i>();
		Point2i minP = new Point2i(Math.max(0, unit.pos.x - 1), Math.max(0,
				unit.pos.y - 1));
		Point2i maxP = new Point2i(Math.min(getPlayerState().mapWidth - 1,
				unit.pos.x + 1), Math.min(getPlayerState().mapHeight - 1,
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
	
}
