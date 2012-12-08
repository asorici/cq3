package org.aimas.craftingquest.core.actions;

import java.util.HashMap;
import java.util.Iterator;

import org.aimas.craftingquest.core.GamePolicy;
import org.aimas.craftingquest.state.GameState;
import org.aimas.craftingquest.state.PlayerState;
import org.aimas.craftingquest.state.Transition;
import org.aimas.craftingquest.state.TransitionResult;
import org.aimas.craftingquest.state.Transition.ActionType;
import org.aimas.craftingquest.state.objects.CraftedObject.BasicResourceType;

public class DropResourcesAction extends Action {
	
	public DropResourcesAction(ActionType type) {
		super(type);
	}
	
	@Override
	protected TransitionResult handle(GameState game, PlayerState player, Transition transition) {
		HashMap<Resource, Integer> unwantedResources = 
				(HashMap<Resource, Integer>)transition.operands[1];
		
		// check for enough energy points
		if (playerUnit.energy < GamePolicy.dropCost) {
			TransitionResult res = new TransitionResult(transition.id);
			res.errorType = TransitionResult.TransitionError.NoEnergyError;
			res.errorReason = "Not enough energy points left for dropping resources";
			return res;
		}

		HashMap<Resource, Integer> visibleCellResources = game.map.cells[playerUnit.pos.y][playerUnit.pos.x].visibleResources;
		HashMap<Resource, Integer> carriedResources = playerUnit.carriedResources;

		Iterator<Resource> it = unwantedResources.keySet().iterator();
		while (it.hasNext()) {
			Resource res = it.next();
			Integer dropped = unwantedResources.get(res);
			Integer existing = visibleCellResources.get(res);
			Integer carried = carriedResources.get(res);

			if (carried != null && dropped <= carried) {
				if (existing == null) {
					visibleCellResources.put(res, dropped);
				} else {
					visibleCellResources.put(res, existing + dropped);
				}

				carriedResources.put(res, carried - dropped);
			}
		}

		playerUnit.energy -= GamePolicy.dropCost; // update energy levels
		TransitionResult dropres = new TransitionResult(transition.id);
		dropres.errorType = TransitionResult.TransitionError.NoError;
		return dropres;
	}
	
	
	@Override
	protected boolean validOperands(Transition transition) {
		HashMap<Resource, Integer> unwantedResources = null;
		
		try{
			unwantedResources = (HashMap<Resource, Integer>)transition.operands[1];
			if (unwantedResources == null) {
				return false;
			}
		} 
		catch(ClassCastException ex) {
			return false;
		}
		
		return true;
	}

}
