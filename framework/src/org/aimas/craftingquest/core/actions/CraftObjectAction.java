package org.aimas.craftingquest.core.actions;

import java.util.HashMap;
import java.util.Iterator;

import org.aimas.craftingquest.core.GamePolicy;
import org.aimas.craftingquest.state.Blueprint;
import org.aimas.craftingquest.state.GameState;
import org.aimas.craftingquest.state.PlayerState;
import org.aimas.craftingquest.state.Transition;
import org.aimas.craftingquest.state.TransitionResult;
import org.aimas.craftingquest.state.UnitState;
import org.aimas.craftingquest.state.Transition.ActionType;
import org.aimas.craftingquest.state.objects.CraftedObject;
import org.aimas.craftingquest.state.objects.CraftedObject.BasicResourceType;
import org.aimas.craftingquest.state.objects.ICrafted;

public class CraftObjectAction extends Action {

	public CraftObjectAction(ActionType type) {
		super(type);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected TransitionResult handle(GameState game, PlayerState player,
			Transition transition) {
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

	@SuppressWarnings("unchecked")
	@Override
	protected boolean validOperands(Transition transition) {
		Blueprint bp = null;

		try {
			bp = (Blueprint) transition.operands[1];
			// target may not be null
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

		// HashMap<CraftedObject, Integer> carriedObjects =
		// playerUnit.carriedObjects;
		HashMap<Resource, Integer> carriedResources = playerUnit.carriedResources;

		/*
		 * if (target.getRequiredObjects() != null) { // it is an object made
		 * out of sub-objects if (usedObjects == null) { return false; }
		 * 
		 * boolean requirementsMet = false;
		 * 
		 * for (HashMap<CraftedObject, Integer> craftingOption :
		 * target.getRequiredObjects()) { boolean alternativeOk = true;
		 * 
		 * Iterator<CraftedObject> objIt = craftingOption.keySet().iterator();
		 * while(objIt.hasNext()) { CraftedObject obj = objIt.next(); Integer
		 * required = craftingOption.get(obj); Integer available =
		 * usedObjects.get(obj); Integer carried = carriedObjects.get(obj);
		 * 
		 * if(available == null || carried == null || required > available ||
		 * required > carried || available > carried) { alternativeOk = false;
		 * break; } }
		 * 
		 * if(alternativeOk) { requirementsMet = true; break; } }
		 * 
		 * if (requirementsMet) { // if requirements met update carriedObjects
		 * with the quantity that remains Iterator<CraftedObject> it =
		 * usedObjects.keySet().iterator(); while (it.hasNext()) { CraftedObject
		 * obj = it.next(); Integer used = usedObjects.get(obj); Integer
		 * existing = carriedObjects.get(obj);
		 * 
		 * carriedObjects.put(obj, existing - used); }
		 * 
		 * return true; } }
		 */
		// else { // it is an object made only out of basic resources
		if (usedResources == null) {
			return false;
		}

		boolean requirementsMet = false;

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
	}
}
