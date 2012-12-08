package org.aimas.craftingquest.core.actions;

import java.util.ArrayList;
import java.util.List;

import org.aimas.craftingquest.core.GamePolicy;
import org.aimas.craftingquest.state.CellState;
import org.aimas.craftingquest.state.GameState;
import org.aimas.craftingquest.state.PlayerState;
import org.aimas.craftingquest.state.Transition;
import org.aimas.craftingquest.state.TransitionResult;
import org.aimas.craftingquest.state.Transition.ActionType;
import org.aimas.craftingquest.state.objects.CraftedObject;
import org.aimas.craftingquest.state.objects.Tower;
import org.aimas.craftingquest.state.objects.CraftedObject.BasicResourceType;

public class PlaceTowerAction extends Action {
	
	public PlaceTowerAction(ActionType type) {
		super(type);
	}
	
	@Override
	protected TransitionResult handle(GameState game, PlayerState player, Transition transition) {
		// check for enough energy points
		if (playerUnit.energy < GamePolicy.buildCost) {
			TransitionResult res = new TransitionResult(transition.id);
			res.errorType = TransitionResult.TransitionError.NoEnergyError;
			res.errorReason = "Not enough energy points left for constructing a tower";
			return res;
		}

		// check for enough credit
		if (player.credit < GamePolicy.towerBuildCost) {
			TransitionResult res = new TransitionResult(transition.id);
			res.errorType = TransitionResult.TransitionError.NoCreditError;
			res.errorReason = "Not enough credit left for constructing a tower";
			return res;
		}

		// check to see if any towers or traps are already in the cell
		CellState unitCell = game.map.cells[playerUnit.pos.y][playerUnit.pos.x];
		if (unitCell.strategicObject != null) {
			TransitionResult res = new TransitionResult(transition.id);
			res.errorType = TransitionResult.TransitionError.BuildError;
			res.errorReason = "Cannot build a tower in a cell that already contains a strategic resource";
			return res;
		}

		// check to see if any resources are left in the cell
		boolean emptyCell = true;
		for (Resource restype : unitCell.resources.keySet()) { // first soil resources
			if (unitCell.resources.get(restype) > 0) {
				emptyCell = false;
				break;
			}
		}

		if (emptyCell) { 	// if still empty then check for visible resources
			for (Resource restype : unitCell.visibleResources.keySet()) {
				if (unitCell.visibleResources.get(restype) > 0) {
					emptyCell = false;
					break;
				}
			}
		}

		if (emptyCell) { 	// if still empty then check for crafted objects
			for (CraftedObject obj : unitCell.craftedObjects.keySet()) {
				if (unitCell.craftedObjects.get(obj) > 0) {
					emptyCell = false;
					break;
				}
			}
		}

		if (!emptyCell) {
			TransitionResult res = new TransitionResult(transition.id);
			res.errorType = TransitionResult.TransitionError.BuildError;
			res.errorReason = "Cannot build a tower in a cell that still contains unmined resources";
			return res;
		}

		// all is ok - build tower and update energy and credit levels
		Tower tower = new Tower(player.id, playerUnit.pos);
		unitCell.strategicObject = tower; // place tower in cell

		List<Tower> playerTowers = game.playerTowers.get(player.id); // add in global list of towers
		if (playerTowers == null) {
			playerTowers = new ArrayList<Tower>();
			playerTowers.add(tower);
			game.playerTowers.put(player.id, playerTowers);
			player.availableTowers.put(tower, true); // this tower is newly
														// available
		} else {
			playerTowers.add(tower);
		}

		player.credit -= GamePolicy.towerBuildCost; // subtract cost from player credit

		TransitionResult towerres = new TransitionResult(transition.id);
		towerres.errorType = TransitionResult.TransitionError.NoError;
		return towerres;
	}

	@Override
	protected boolean validOperands(Transition transition) {
		return true;
	}

}
