package org.aimas.craftingquest.core.actions;

import java.util.List;
import org.aimas.craftingquest.core.GamePolicy;
import org.aimas.craftingquest.state.Blueprint;
import org.aimas.craftingquest.state.CellState;
import org.aimas.craftingquest.state.GameState;
import org.aimas.craftingquest.state.PlayerState;
import org.aimas.craftingquest.state.Transition;
import org.aimas.craftingquest.state.Transition.ActionType;
import org.aimas.craftingquest.state.TransitionResult;
import org.aimas.craftingquest.state.objects.CraftedObjectType;
import org.aimas.craftingquest.state.objects.TrapObject;

public class PlaceTrapAction extends Action {
	
	public PlaceTrapAction(ActionType type) {
		super(type);
	}

	@Override
	protected TransitionResult handle(GameState game, PlayerState player, Transition transition) {
		// check for enough energy points
		if (playerUnit.energy < GamePolicy.placeTrapCost) {
			TransitionResult res = new TransitionResult(transition.id);
			res.errorType = TransitionResult.TransitionError.NoEnergyError;
			res.errorReason = "Not enough energy points left to place the trap";
			return res;
		}

		// check to see if any towers or traps are already in the cell
		CellState unitCell = game.map.cells[playerUnit.pos.y][playerUnit.pos.x];
		if (unitCell.strategicObject != null) {
			TransitionResult res = new TransitionResult(transition.id);
			res.errorType = TransitionResult.TransitionError.BuildError;
			res.errorReason = "Cannot place a trap in a cell that already contains a strategic object";
			return res;
		}

		/**
		 * Traps can be placed in cells with resources
		 
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
		*/
				
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
		if (blueprint.getType() != CraftedObjectType.TRAP) {
			TransitionResult res = new TransitionResult(transition.id);
			res.errorType = TransitionResult.TransitionError.BlueprintError;
			res.errorReason = "Wrong blueprint for trap.";
			return res;
		}
		
		//Check if resources are available
		if (!ActionUtils.checkCraftingRequirements(playerUnit, blueprint)) {
			TransitionResult res = new TransitionResult(transition.id);
			res.errorType = TransitionResult.TransitionError.CraftingError;
			res.errorReason = "Object crafting requirements are not met.";
			return res;
		}
		
		TrapObject trap = (TrapObject) blueprint.craft(playerUnit.playerID, playerUnit.pos);
		
		unitCell.strategicObject = trap; // place trap in cell

		// consume energy to build the tower
		playerUnit.energy -= GamePolicy.placeTrapCost; 

		List<TrapObject> playerTraps = game.playerStates.get(player.id).availableTraps; // add in global list of towers
		playerTraps.add(trap);
		
		game.playerStates.get(player.id).placeTrap();
		
		TransitionResult trapres = new TransitionResult(transition.id);
		trapres.errorType = TransitionResult.TransitionError.NoError;
		return trapres;
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
	
	
	@Override
	public void printToGuiLog(GameState game, PlayerState player, Transition transition) {
		if (playerUnit != null) {
			Blueprint playerBlueprint = (Blueprint) transition.operands[1];
			
			gui_logger.info(game.round.currentRound + " " + transition.operator.name() + " " + player.id + " " 
					+ playerUnit.id + " " + playerUnit.pos.x + " " + playerUnit.pos.y + " " + player.getScore() + " " 
					+ playerUnit.energy + " " + playerBlueprint.getType().name() + " " + playerBlueprint.getLevel());
		}
	}
}
