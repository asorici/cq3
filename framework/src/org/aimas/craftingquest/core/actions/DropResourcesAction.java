package org.aimas.craftingquest.core.actions;

import java.util.HashMap;
import java.util.Iterator;

import org.aimas.craftingquest.core.GamePolicy;
import org.aimas.craftingquest.state.GameState;
import org.aimas.craftingquest.state.PlayerState;
import org.aimas.craftingquest.state.Transition;
import org.aimas.craftingquest.state.Transition.ActionType;
import org.aimas.craftingquest.state.TransitionResult;
import org.aimas.craftingquest.state.resources.ResourceType;

public class DropResourcesAction extends Action {
	
	public DropResourcesAction(ActionType type) {
		super(type);
	}
	
	@Override
	protected TransitionResult handle(GameState game, PlayerState player, Transition transition) {
		@SuppressWarnings("unchecked")
		HashMap<ResourceType, Integer> unwantedResources = 
				(HashMap<ResourceType, Integer>)transition.operands[1];
		// check for enough energy points
		if (playerUnit.energy < GamePolicy.dropCost) {
			TransitionResult res = new TransitionResult(transition.id);
			res.errorType = TransitionResult.TransitionError.NoEnergyError;
			res.errorReason = "Not enough energy points left for dropping resources";
			return res;
		}

		HashMap<ResourceType, Integer> visibleCellResources = game.map.cells[playerUnit.pos.y][playerUnit.pos.x].visibleResources;
		HashMap<ResourceType, Integer> carriedResources = playerUnit.carriedResources;

		Iterator<ResourceType> it = unwantedResources.keySet().iterator();
		while (it.hasNext()) {
			ResourceType res = it.next();
			Integer dropped = unwantedResources.get(res);
			Integer existing = visibleCellResources.get(res);
			Integer carried = carriedResources.get(res);

			if (carried != null) {
				if (dropped < carried) {
					if (existing == null) {
						visibleCellResources.put(res, dropped);
					} else {
						visibleCellResources.put(res, existing + dropped);
					}
					carriedResources.put(res, carried - dropped);
				} else  {
					if (existing == null) {
						visibleCellResources.put(res, carried);
					} else {
						visibleCellResources.put(res, existing + carried);
					}
					carriedResources.remove(res);
				}
			}
		}

		playerUnit.energy -= GamePolicy.dropCost; // update energy levels
		TransitionResult dropres = new TransitionResult(transition.id);
		dropres.errorType = TransitionResult.TransitionError.NoError;
		return dropres;
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	protected boolean validOperands(Transition transition) {
		HashMap<ResourceType, Integer> unwantedResources = null;
		try{
			unwantedResources = (HashMap<ResourceType, Integer>)transition.operands[1];
			if (unwantedResources == null) {
				return false;
			}
		} 
		catch(ClassCastException ex) {
			return false;
		}
		return true;
	}
	
	
	@Override
	public void printToGuiLog(GameState game, PlayerState player, Transition transition) {
		if (playerUnit != null) {
			gui_logger.info(game.round.currentRound + " " + transition.operator.name() + " " + player.id + " " 
					+ playerUnit.id + " " + playerUnit.pos.x + " " + playerUnit.pos.y + " " + player.getScore() + " " 
					+ playerUnit.energy);
		}
	}
}
