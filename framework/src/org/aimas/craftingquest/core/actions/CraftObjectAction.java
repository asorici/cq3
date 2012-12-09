package org.aimas.craftingquest.core.actions;

import org.aimas.craftingquest.core.GamePolicy;
import org.aimas.craftingquest.state.Blueprint;
import org.aimas.craftingquest.state.GameState;
import org.aimas.craftingquest.state.PlayerState;
import org.aimas.craftingquest.state.Transition;
import org.aimas.craftingquest.state.Transition.ActionType;
import org.aimas.craftingquest.state.TransitionResult;
import org.aimas.craftingquest.state.objects.CraftedObjectType;
import org.aimas.craftingquest.state.objects.ICrafted;

public class CraftObjectAction extends Action {

	public CraftObjectAction(ActionType type) {
		super(type);
	}

	@Override
	protected TransitionResult handle(GameState game, PlayerState player, Transition transition) {
		// check for enough energy points
		if (playerUnit.energy < GamePolicy.buildCost) {
			TransitionResult res = new TransitionResult(transition.id);
			res.errorType = TransitionResult.TransitionError.NoEnergyError;
			res.errorReason = "Not enough energy points left for building an object";
			return res;
		}

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
		
		// Check if blueprint is for a sword or an armour
		if (blueprint.getType() != CraftedObjectType.ARMOUR &&
				blueprint.getType() != CraftedObjectType.SWORD) {
			TransitionResult res = new TransitionResult(transition.id);
			res.errorType = TransitionResult.TransitionError.BlueprintError;
			res.errorReason = "Wrong blueprint for Equipment.";
			return res;
		}

		// check that the unit has the required resources/objects required for
		// making the object
		if (!ActionUtils.checkCraftingRequirements(playerUnit, blueprint)) {
			TransitionResult res = new TransitionResult(transition.id);
			res.errorType = TransitionResult.TransitionError.CraftingError;
			res.errorReason = "Object crafting requirements are not met.";
			return res;
		}

		playerUnit.energy -= GamePolicy.buildCost; // update energy levels
		
		ICrafted target = blueprint.craft(playerUnit.playerID, playerUnit.pos);
		
		Integer targetObjectCount = playerUnit.carriedObjects.get(target);
		if (targetObjectCount == null) { // add new crafted object to the list
			playerUnit.carriedObjects.put(target, 1);
		} else {
			playerUnit.carriedObjects.put(target, targetObjectCount + 1);
		}

		TransitionResult craftres = new TransitionResult(transition.id);
		craftres.errorType = TransitionResult.TransitionError.NoError;
		return craftres;
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
