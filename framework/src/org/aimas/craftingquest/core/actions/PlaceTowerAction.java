package org.aimas.craftingquest.core.actions;

import org.aimas.craftingquest.core.GamePolicy;
import org.aimas.craftingquest.state.Blueprint;
import org.aimas.craftingquest.state.CellState;
import org.aimas.craftingquest.state.GameState;
import org.aimas.craftingquest.state.PlayerState;
import org.aimas.craftingquest.state.Transition;
import org.aimas.craftingquest.state.Transition.ActionType;
import org.aimas.craftingquest.state.TransitionResult;
import org.aimas.craftingquest.state.objects.CraftedObjectType;
import org.aimas.craftingquest.state.objects.ICrafted;
import org.aimas.craftingquest.state.objects.Tower;
import org.aimas.craftingquest.state.resources.ResourceType;

public class PlaceTowerAction extends Action {
	
	public PlaceTowerAction(ActionType type) {
		super(type);
	}
	
	@Override
	protected TransitionResult handle(GameState game, PlayerState player, Transition transition) {
		// check for enough energy points
		if (playerUnit.energy < GamePolicy.placeTowerCost) {
			TransitionResult res = new TransitionResult(transition.id);
			res.errorType = TransitionResult.TransitionError.NoEnergyError;
			res.errorReason = "Not enough energy points left to build tower";
			return res;
		}

		// check to see if any towers or traps are already in the cell
		CellState unitCell = game.map.cells[playerUnit.pos.y][playerUnit.pos.x];
		if (unitCell.strategicObject != null) {
			TransitionResult res = new TransitionResult(transition.id);
			res.errorType = TransitionResult.TransitionError.BuildError;
			res.errorReason = "Cannot build a tower in a cell that already contains a strategic object";
			return res;
		}

		// check to see if any resources are left in the cell
		boolean emptyCell = true;
		for (ResourceType restype : unitCell.resources.keySet()) { // first soil resources
			if (unitCell.resources.get(restype) > 0) {
				emptyCell = false;
				break;
			}
		}

		if (emptyCell) { 	// if still empty then check for visible resources
			for (ResourceType restype : unitCell.visibleResources.keySet()) {
				if (unitCell.visibleResources.get(restype) > 0) {
					emptyCell = false;
					break;
				}
			}
		}

		if (emptyCell) { 	// if still empty then check for crafted objects
			for (ICrafted obj : unitCell.craftedObjects.keySet()) {
				if (unitCell.craftedObjects.get(obj) > 0) {
					emptyCell = false;
					break;
				}
			}
		}

		if (!emptyCell) {
			TransitionResult res = new TransitionResult(transition.id);
			res.errorType = TransitionResult.TransitionError.BuildError;
			res.errorReason = "Cannot build a tower in a cell that still contains unmined resources or dropped objects";
			return res;
		}
		
		// Map cell is clear ...
		
		Blueprint playerBlueprint = (Blueprint) transition.operands[1];
		Blueprint blueprint; // the real one
		if (GamePolicy.blueprints.contains(playerBlueprint)) {
			blueprint = GamePolicy.blueprints.get(GamePolicy.blueprints.indexOf(playerBlueprint));
		} else {
			TransitionResult res = new TransitionResult(transition.id);
			res.errorType = TransitionResult.TransitionError.BlueprintError;
			res.errorReason = "Wrong blueprint for trap.";
			return res;
		}
		
		if (!player.availableBlueprints.contains(blueprint)) {
			TransitionResult res = new TransitionResult(transition.id);
			res.errorType = TransitionResult.TransitionError.MissingBlueprintError;
			res.errorReason = "Player attempted to use a blueprint he does not have.";
			return res;
		}
		
		// Check if blueprint is ok
		if (blueprint.getType() != CraftedObjectType.TOWER) {
			TransitionResult res = new TransitionResult(transition.id);
			res.errorType = TransitionResult.TransitionError.BlueprintError;
			res.errorReason = "Wrong blueprint for tower.";
			return res;
		}
		
		//Check if resources are available
		if (!ActionUtils.checkCraftingRequirements(playerUnit, blueprint)) {
			TransitionResult res = new TransitionResult(transition.id);
			res.errorType = TransitionResult.TransitionError.CraftingError;
			res.errorReason = "Object crafting requirements are not met.";
			return res;
		}
		
		Tower tower = (Tower) blueprint.craft(playerUnit.playerID, playerUnit.pos);
		
		unitCell.strategicObject = tower; // place tower in cell
		
		player.availableTowers.add(tower);
//		List<Tower> playerTowers = game.playerTowers.get(player.id); // add in global list of towers
//		if (playerTowers == null) {
//			playerTowers = new ArrayList<Tower>();
//			playerTowers.add(tower);
//			game.playerTowers.put(player.id, playerTowers);
//			player.availableTowers.put(tower, true); // this tower is newly
//		} else {
//			playerTowers.add(tower);
//		}

		// consume energy to build the tower
		playerUnit.energy -= GamePolicy.placeTowerCost; 
		
		game.playerStates.get(player.id).placeTower();
		
		TransitionResult towerres = new TransitionResult(transition.id);
		towerres.errorType = TransitionResult.TransitionError.NoError;
		return towerres;
	}

	@Override
	protected boolean validOperands(Transition transition) {
		Blueprint bp = null;
		try {
			bp = (Blueprint) transition.operands[1];
			if (bp == null) {
				return false;
			}
		} catch (ClassCastException ex) {
			return false;
		}

		return true;
	}

}
