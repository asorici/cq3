package org.aimas.craftingquest.core.actions;

import java.util.HashMap;

import org.aimas.craftingquest.core.GamePolicy;
import org.aimas.craftingquest.state.CraftedObject;
import org.aimas.craftingquest.state.EquippableObject;
import org.aimas.craftingquest.state.GameState;
import org.aimas.craftingquest.state.PlayerState;
import org.aimas.craftingquest.state.Transition;
import org.aimas.craftingquest.state.TransitionResult;
import org.aimas.craftingquest.state.TrapObject;
import org.aimas.craftingquest.state.Transition.ActionType;

public class PlaceTrapAction extends Action {
	
	public PlaceTrapAction(ActionType type) {
		super(type);
	}

	@Override
	protected TransitionResult handle(GameState game, PlayerState player,
			Transition transition) {
		TrapObject trapObject = (TrapObject)transition.operands[1];
		
		// check for enough energy points
		if (playerUnit.energy < GamePolicy.placeTrapCost) {
			TransitionResult res = new TransitionResult(transition.id);
			res.errorType = TransitionResult.TransitionError.NoEnergyError;
			res.errorReason = "Not enough energy points left for placing the trap";
			return res;
		}
		
		HashMap<CraftedObject, Integer> cellObjects = game.map.cells[playerUnit.pos.y][playerUnit.pos.x].craftedObjects;
		HashMap<CraftedObject, Integer> carriedObjects = playerUnit.carriedObjects;
		
		Integer existing = cellObjects.get(trapObject);
		Integer carried = carriedObjects.get(trapObject);

		if (carried != null && 1 <= carried) {
			if (existing == null) {
				cellObjects.put(trapObject, 1);
			} else {
				cellObjects.put(trapObject, existing + 1);
			}

			carriedObjects.put(trapObject, carried - 1);
		} else {
			TransitionResult res = new TransitionResult(transition.id);
			res.errorType = TransitionResult.TransitionError.TrapMisssingError;
			res.errorReason = "Player tried to place a trap he does not have.";
			return res;
		}
		
		playerUnit.energy -= GamePolicy.placeTrapCost; // update energy levels
		
		return null;
	}

	@Override
	protected boolean validOperands(Transition transition) {
		TrapObject target = null;

		try {
			target = (TrapObject)transition.operands[1];
			// target may not be null
			if (target == null) {
				return false;
			}
		}
		catch(ClassCastException ex) {
				return false;
		}
		
		return true;
	}

}
