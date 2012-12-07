package org.aimas.craftingquest.core.actions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.aimas.craftingquest.core.GamePolicy;
import org.aimas.craftingquest.state.CellState;
import org.aimas.craftingquest.state.CraftedObject.BasicResourceType;
import org.aimas.craftingquest.state.GameState;
import org.aimas.craftingquest.state.PlayerState;
import org.aimas.craftingquest.state.Point2i;
import org.aimas.craftingquest.state.ResourceAttributes;
import org.aimas.craftingquest.state.Transition;
import org.aimas.craftingquest.state.Transition.ActionType;
import org.aimas.craftingquest.state.TransitionResult;

public class ScanLandAction extends Action {
	
	public ScanLandAction(ActionType type) {
		super(type);
	}
	
	@Override
	protected TransitionResult handle(GameState game, PlayerState player,
			Transition transition) {
		
		// check enough energy points
		if (playerUnit.energy < GamePolicy.scanCost) {
			TransitionResult res = new TransitionResult(transition.id);
			res.errorType = TransitionResult.TransitionError.NoEnergyError;
			res.errorReason = "Not enough energy points left for scanning";
			return res;
		}

		// all ok - scan - return attributes of surrounding cells and subtract
		// energy points
		CellState scanCell = game.map.cells[playerUnit.pos.y][playerUnit.pos.x];
		playerUnit.scannedResourceAttributes = getScannedResourceAttributes(game, scanCell.pos);
		playerUnit.energy -= GamePolicy.scanCost;
		TransitionResult scanres = new TransitionResult(transition.id);
		scanres.errorType = TransitionResult.TransitionError.NoError;
		return scanres;
	}

	@Override
	protected boolean validOperands(Transition transition) {
		return true;
	}
	
	@SuppressWarnings("unchecked")
	private List<int[]>[][] getScannedResourceAttributes(GameState game, Point2i pos) {
		int radius = GamePolicy.scanRadius;
		int dim = radius * 2 + 1;
		
		List<int[]>[][] resAttr = new List[dim][dim];
		for (int y = pos.y - radius, i = 0; y <= pos.y + radius; y++, i++) {
			for (int x = pos.x - radius, j = 0; x <= pos.x + radius; x++, j++) {
				resAttr[i][j] = null;
				if (x >= 0 && x < GamePolicy.mapsize.x && y >= 0 && y < GamePolicy.mapsize.y) {
					resAttr[i][j] = new ArrayList<int[]>();
					
					Iterator<BasicResourceType> resIt = game.map.cells[y][x].scanAttributes.keySet().iterator();
					while (resIt.hasNext()) {
						BasicResourceType resType = resIt.next();
						Integer existing = game.map.cells[y][x].resources.get(resType);
						if (existing != null && existing > 0) {
							ResourceAttributes ra = game.map.cells[y][x].scanAttributes.get(resType);
							resAttr[i][j].add(ra.attributeValues);
						}
					}
				}
			}
		}
		
		return resAttr;
	}
}
