package org.aimas.craftingquest.core.actions;

import java.util.Iterator;

import org.aimas.craftingquest.core.GamePolicy;
import org.aimas.craftingquest.state.Blueprint;
import org.aimas.craftingquest.state.GameState;
import org.aimas.craftingquest.state.PlayerState;
import org.aimas.craftingquest.state.Transition;
import org.aimas.craftingquest.state.Transition.ActionType;
import org.aimas.craftingquest.state.TransitionResult;
import org.aimas.craftingquest.state.UnitState;
import org.aimas.craftingquest.state.objects.ICrafted;
import org.aimas.craftingquest.state.resources.ResourceType;

public class CraftObjectAction extends Action {

	public CraftObjectAction(ActionType type) {
		super(type);
	}

	@Override
	protected TransitionResult handle(GameState game, PlayerState player, Transition transition) {
		Blueprint blueprint = (Blueprint) transition.operands[1];
		
		// check for enough energy points
		if (playerUnit.energy < GamePolicy.buildCost) {
			TransitionResult res = new TransitionResult(transition.id);
			res.errorType = TransitionResult.TransitionError.NoEnergyError;
			res.errorReason = "Not enough energy points left for building an object";
			return res;
		}

		// check that player holds corresponding blueprint
		boolean foundBlueprint = false;
		for (Blueprint bp : player.availableBlueprints) {
			if (bp.equals(blueprint)) {
				foundBlueprint = true;
			}
		}

		if (!foundBlueprint) {
			TransitionResult res = new TransitionResult(transition.id);
			res.errorType = TransitionResult.TransitionError.CraftingError;
			res.errorReason = "Object crafting requirements are not met. Missing required blueprint.";
			return res;
		}

		// check that the unit has the required resources/objects required for
		// making the object
		if (!checkCraftingRequirements(playerUnit, blueprint)) {
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

	private boolean checkCraftingRequirements(UnitState playerUnit,
			Blueprint blueprint) {

		/**
		 *  Check if resources are available
		 */
		Iterator<ResourceType> resIt = blueprint.getResourcesNeeded().keySet().iterator();
		while (resIt.hasNext()) {
			ResourceType rt = resIt.next(); 
			Integer required = blueprint.getResourcesNeeded().get(rt);
			Integer available = playerUnit.carriedResources.get(rt);
			if (available == null || available < required) return false;
		}
		/**
		 *  Everythin ok, Consume resources
		 */
		resIt = blueprint.getResourcesNeeded().keySet().iterator();
		while (resIt.hasNext()) {
			ResourceType rt = resIt.next(); 
			Integer required = blueprint.getResourcesNeeded().get(rt);
			Integer available = playerUnit.carriedResources.get(rt);
			playerUnit.carriedResources.put(rt, available-required);
		}
		
		return true;
		
		/*
		for (HashMap<Resource, Integer> resourceOption : target
				.getRequiredResources()) {
			boolean alternativeOk = true;

			Iterator<Resource> resIt = resourceOption.keySet()
					.iterator();
			while (resIt.hasNext()) {
				Resource res = resIt.next();
				Integer required = resourceOption.get(res);
				Integer available = usedResources.get(res);
				Integer carried = carriedResources.get(res);

				if (available == null || carried == null
						|| required > available || required > carried
						|| available > carried) {
					alternativeOk = false;
					break;
				}
			}

			if (alternativeOk) {
				requirementsMet = true;
				break;
			}
		}
		
		if (requirementsMet) { // if requirements met update carriedResources
								// with the quantity that remains
			Iterator<Resource> it = usedResources.keySet().iterator();
			while (it.hasNext()) {
				Resource res = it.next();
				Integer used = usedResources.get(res);
				Integer existing = carriedResources.get(res);

				carriedResources.put(res, existing - used);
			}

			return true;
		}
		// }

		return false;
		*/
	}
}
