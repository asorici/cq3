package org.aimas.craftingquest.core.actions;

import java.util.Iterator;

import org.aimas.craftingquest.core.GamePolicy;
import org.aimas.craftingquest.state.CellState;
import org.aimas.craftingquest.state.GameState;
import org.aimas.craftingquest.state.PlayerState;
import org.aimas.craftingquest.state.Transition;
import org.aimas.craftingquest.state.Transition.ActionType;
import org.aimas.craftingquest.state.TransitionResult;
import org.aimas.craftingquest.state.resources.ResourceType;

public class DigAction extends Action {
	public DigAction(ActionType type) {
		super(type);
	}
	
	@Override
	protected TransitionResult handle(GameState game, PlayerState player, Transition transition) {
		// check enough energy points
		if (playerUnit.energy < GamePolicy.digCost) {
			TransitionResult res = new TransitionResult(transition.id);
			res.errorType = TransitionResult.TransitionError.NoEnergyError;
			res.errorReason = "Not enough energy points left for digging";
			return res;
		}

		// all ok - dig - return list of existing resources in this cell and
		// subtract energy points
		playerUnit.currentCellResources.clear();
		CellState currentCell = game.map.cells[playerUnit.pos.y][playerUnit.pos.x];
		Iterator<ResourceType> resIterator = currentCell.resources.keySet().iterator();
		
		while (resIterator.hasNext()) {
			ResourceType res = resIterator.next();
			if (currentCell.resources.get(res) > 0) {
				playerUnit.currentCellResources.put(res, currentCell.resources.get(res));
			}
		}
		playerUnit.energy -= GamePolicy.digCost;

		TransitionResult digres = new TransitionResult(transition.id);
		digres.errorType = TransitionResult.TransitionError.NoError;
		return digres;
	}

	@Override
	protected boolean validOperands(Transition transition) {
		return true;
	}

}
