package org.aimas.craftingquest.core.actions;

import java.util.HashMap;
import java.util.Iterator;

import org.aimas.craftingquest.core.GamePolicy;
import org.aimas.craftingquest.state.GameState;
import org.aimas.craftingquest.state.PlayerState;
import org.aimas.craftingquest.state.Transition;
import org.aimas.craftingquest.state.Transition.ActionType;
import org.aimas.craftingquest.state.TransitionResult;
import org.aimas.craftingquest.state.objects.ICrafted;

public class DropObjectAction extends Action {
	
	public DropObjectAction(ActionType type) {
		super(type);
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	protected TransitionResult handle(GameState game, PlayerState player, Transition transition) {
		HashMap<ICrafted, Integer> unwantedObjects = (HashMap<ICrafted, Integer>)transition.operands[1];
		// check for enough energy points
		if (playerUnit.energy < GamePolicy.dropCost) {
			TransitionResult res = new TransitionResult(transition.id);
			res.errorType = TransitionResult.TransitionError.NoEnergyError;
			res.errorReason = "Not enough energy points left for dropping resources";
			return res;
		}

		HashMap<ICrafted, Integer> cellObjects = game.map.cells[playerUnit.pos.y][playerUnit.pos.x].craftedObjects;
		HashMap<ICrafted, Integer> carriedObjects = playerUnit.carriedObjects;

		Iterator<ICrafted> it = unwantedObjects.keySet().iterator();
		while (it.hasNext()) {
			ICrafted res = it.next();
			Integer dropped = unwantedObjects.get(res);
			Integer existing = cellObjects.get(res);
			Integer carried = carriedObjects.get(res);

			if (carried != null) {
				if(dropped < carried) {
					// if the unit drops some, but not all objects
					if (existing == null) {
						cellObjects.put(res, dropped);
					} 
					else {
						cellObjects.put(res, existing + dropped);
					}
					carriedObjects.put(res, carried - dropped);
				} else {
					// the unit drops all such objects
					if (existing == null) {
						cellObjects.put(res, carried);
					} 
					else {
						cellObjects.put(res, existing + carried);
					}
					carriedObjects.remove(res);
				}
			}
		}
		
		// after having dropped the objects, check that equipped objects still have a correspondent
		// in the carried ones, otherwise cancel the equipment
		if (playerUnit.equipedSword != null) {
			if (!carriedObjects.containsKey(playerUnit.equipedSword)) {
				playerUnit.equipedSword = null;
			}
		}
		
		if (playerUnit.equipedArmour != null) {
			if (!carriedObjects.containsKey(playerUnit.equipedArmour)) {
				playerUnit.equipedArmour = null;
			}
		}
		
		
		// update energy levels and return result
		playerUnit.energy -= GamePolicy.dropCost; 
		TransitionResult dropres = new TransitionResult(transition.id);
		dropres.errorType = TransitionResult.TransitionError.NoError;
		return dropres;
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	protected boolean validOperands(Transition transition) {
		HashMap<ICrafted, Integer> unwantedObjects = null; 
		try {
			unwantedObjects = (HashMap<ICrafted, Integer>)transition.operands[1];
			if (unwantedObjects == null) {
				return false;
			}
		}
		catch (ClassCastException ex) {
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
