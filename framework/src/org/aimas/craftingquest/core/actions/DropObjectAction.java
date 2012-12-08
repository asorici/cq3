package org.aimas.craftingquest.core.actions;

import java.util.HashMap;
import java.util.Iterator;

import org.aimas.craftingquest.core.GamePolicy;
import org.aimas.craftingquest.state.GameState;
import org.aimas.craftingquest.state.PlayerState;
import org.aimas.craftingquest.state.Transition;
import org.aimas.craftingquest.state.TransitionResult;
import org.aimas.craftingquest.state.Transition.ActionType;
import org.aimas.craftingquest.state.objects.CraftedObject;

public class DropObjectAction extends Action {
	
	public DropObjectAction(ActionType type) {
		super(type);
	}
	
	
	@Override
	protected TransitionResult handle(GameState game, PlayerState player, Transition transition) {
		HashMap<CraftedObject, Integer> unwantedObjects = (HashMap<CraftedObject, Integer>)transition.operands[1];
		
		// check for enough energy points
		if (playerUnit.energy < GamePolicy.dropCost) {
			TransitionResult res = new TransitionResult(transition.id);
			res.errorType = TransitionResult.TransitionError.NoEnergyError;
			res.errorReason = "Not enough energy points left for dropping resources";
			return res;
		}

		HashMap<CraftedObject, Integer> cellObjects = game.map.cells[playerUnit.pos.y][playerUnit.pos.x].craftedObjects;
		HashMap<CraftedObject, Integer> carriedObjects = playerUnit.carriedObjects;

		Iterator<CraftedObject> it = unwantedObjects.keySet().iterator();
		while (it.hasNext()) {
			CraftedObject res = it.next();
			Integer dropped = unwantedObjects.get(res);
			Integer existing = cellObjects.get(res);
			Integer carried = carriedObjects.get(res);

			if (carried != null && dropped <= carried) {
				if (existing == null) {
					cellObjects.put(res, dropped);
				} else {
					cellObjects.put(res, existing + dropped);
				}

				carriedObjects.put(res, carried - dropped);
			}
		}

		playerUnit.energy -= GamePolicy.dropCost; // update energy levels
		TransitionResult dropres = new TransitionResult(transition.id);
		dropres.errorType = TransitionResult.TransitionError.NoError;
		return dropres;
	}
	
	
	@Override
	protected boolean validOperands(Transition transition) {
		HashMap<CraftedObject, Integer> unwantedObjects = null; 
		
		try {
			unwantedObjects = (HashMap<CraftedObject, Integer>)transition.operands[1];
			if (unwantedObjects == null) {
				return false;
			}
		}
		catch (ClassCastException ex) {
			return false;
		}
		
		return true;
	}

}
