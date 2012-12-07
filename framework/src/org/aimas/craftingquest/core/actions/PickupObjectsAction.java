package org.aimas.craftingquest.core.actions;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.aimas.craftingquest.core.GamePolicy;
import org.aimas.craftingquest.state.CellState;
import org.aimas.craftingquest.state.CraftedObject;
import org.aimas.craftingquest.state.GameState;
import org.aimas.craftingquest.state.PlayerState;
import org.aimas.craftingquest.state.Tower;
import org.aimas.craftingquest.state.Transition;
import org.aimas.craftingquest.state.Transition.ActionType;
import org.aimas.craftingquest.state.TransitionResult;

public class PickupObjectsAction extends Action {
	public PickupObjectsAction(ActionType type) {
		super(type);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected TransitionResult handle(GameState game, PlayerState player, Transition transition) {
		// get objects
		HashMap<CraftedObject, Integer> requiredObjects = 
				(HashMap<CraftedObject, Integer>)transition.operands[1];
		
		// check that enough energy points are available for this operation
		if (playerUnit.energy < GamePolicy.pickupCost) {
			TransitionResult res = new TransitionResult(transition.id);
			res.errorType = TransitionResult.TransitionError.NoEnergyError;
			res.errorReason = "Not enough energy points left for picking up resources";
			return res;
		}

		// check that no opponent tower is guarding the resources
		boolean objectsGuarded = false;
		for (Integer pId : game.getPlayerIds()) {
			if (pId != player.id) {
				List<Tower> opponentTowers = game.playerTowers.get(pId);
				for (Tower t : opponentTowers) {
					if (Math.abs(t.getPosition().x - playerUnit.pos.x) <= GamePolicy.towerCutoffRadius
							&& Math.abs(t.getPosition().y - playerUnit.pos.y) <= GamePolicy.towerCutoffRadius) {
						objectsGuarded = true;
						break;
					}
				}
			}

			if (objectsGuarded) {
				break;
			}
		}

		if (objectsGuarded) {
			TransitionResult res = new TransitionResult(transition.id);
			res.errorType = TransitionResult.TransitionError.GuardError;
			res.errorReason = "Cannot pickup objects from guarded area.";
			return res;
		}

		// check that the desired (res, quantity) pairs can be satisfied by the
		// current cell
		// and do the pickup where conditions are met
		CellState miningCell = game.map.cells[playerUnit.pos.y][playerUnit.pos.x];
		HashMap<CraftedObject, Integer> cellObjects = miningCell.craftedObjects;
		HashMap<CraftedObject, Integer> carriedObjects = playerUnit.carriedObjects;

		Iterator<CraftedObject> it = requiredObjects.keySet().iterator();
		while (it.hasNext()) {
			CraftedObject res = it.next();
			Integer required = requiredObjects.get(res);
			Integer available = cellObjects.get(res);

			if (available != null && required <= available) {
				Integer carried = carriedObjects.get(res);
				if (carried == null) {
					carriedObjects.put(res, required);
				} else {
					carriedObjects.put(res, carried + required);
				}

				carriedObjects.put(res, available - required);
			}
		}

		playerUnit.energy -= GamePolicy.pickupCost; // update energy levels
		TransitionResult pickupobjs = new TransitionResult(transition.id);
		pickupobjs.errorType = TransitionResult.TransitionError.NoError;
		return pickupobjs;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected boolean validOperands(Transition transition) {
		HashMap<CraftedObject, Integer> requiredObjects = null;
		
		try{
			requiredObjects = (HashMap<CraftedObject, Integer>)transition.operands[1];
			
			// check for valid operand
			if (requiredObjects == null) {
				return false;
			}
		}
		catch(ClassCastException ex) {
			return false;
		}
		
		return true;
	}

}
