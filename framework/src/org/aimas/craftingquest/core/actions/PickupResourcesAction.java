package org.aimas.craftingquest.core.actions;

import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;

import org.aimas.craftingquest.core.GamePolicy;
import org.aimas.craftingquest.state.CellState;
import org.aimas.craftingquest.state.GameState;
import org.aimas.craftingquest.state.PlayerState;
import org.aimas.craftingquest.state.Transition;
import org.aimas.craftingquest.state.Transition.ActionType;
import org.aimas.craftingquest.state.TransitionResult;
import org.aimas.craftingquest.state.resources.ResourceType;

public class PickupResourcesAction extends Action {
	public PickupResourcesAction(ActionType type) {
		super(type);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected TransitionResult handle(GameState game, PlayerState player, Transition transition) {
		// get resources
		HashMap<ResourceType, Integer> requiredResources = 
				(HashMap<ResourceType, Integer>)transition.operands[1];
		
		// check that enough energy points are available for this operation
		if (playerUnit.energy < GamePolicy.pickupCost) {
			TransitionResult res = new TransitionResult(transition.id);
			res.errorType = TransitionResult.TransitionError.NoEnergyError;
			res.errorReason = "Not enough energy points left for picking up resources";
			return res;
		}
		
		/**
		 * Am renuntat la asta

		// check that no opponent tower is guarding the resources
		boolean resourcesGuarded = false;
		for (Integer pId : game.getPlayerIds()) {
			if (pId != player.id) {
				List<Tower> opponentTowers = game.playerTowers.get(pId);
				for (Tower t : opponentTowers) {
					if (Math.abs(t.getPosition().x - playerUnit.pos.x) <= GamePolicy.towerCutoffRadius
							&& Math.abs(t.getPosition().y - playerUnit.pos.y) <= GamePolicy.towerCutoffRadius) {
						resourcesGuarded = true;
						break;
					}
				}
			}

			if (resourcesGuarded) {
				break;
			}
		}

		if (resourcesGuarded) {
			TransitionResult res = new TransitionResult(transition.id);
			res.errorType = TransitionResult.TransitionError.GuardError;
			res.errorReason = "Cannot pickup resources from guarded area.";
			return res;
		}
		 */
		// check that the desired (res, quantity) pairs can be satisfied by the
		// current cell
		// and do the pickup where conditions are met
		CellState miningCell = game.map.cells[playerUnit.pos.y][playerUnit.pos.x];

		HashMap<ResourceType, Integer> cellResources = miningCell.resources;
		TreeSet<ResourceType> cellResourceTypes = miningCell.resourceTypes;
		HashMap<ResourceType, Integer> visibleCellResources = miningCell.visibleResources;
		HashMap<ResourceType, Integer> carriedResources = playerUnit.carriedResources;

		Iterator<ResourceType> it = requiredResources.keySet().iterator();
		while (it.hasNext()) {
			ResourceType res = it.next();
			Integer required = requiredResources.get(res);
			
			Integer available = cellResources.get(res);
			Integer availableVisible = visibleCellResources.get(res);
			Integer total = new Integer(0);
			if (available != null) {
				total += available;
			}

			if (availableVisible != null) {
				total += availableVisible;
			}

			if (required <= total) {
				if (res == ResourceType.GOLD) {		// gold gets added to the player
					player.gold += required;
				}
				else {								// all other resources will be carried by the unit
					Integer carried = carriedResources.get(res);
					if (carried == null) {
						carriedResources.put(res, required);
					} else {
						carriedResources.put(res, carried + required);
					}
				}
				
				if (availableVisible != null) { // first try and take all resources from the visible ones
					if (required <= availableVisible) { 
						visibleCellResources.put(res, availableVisible - required);
					} else {
						visibleCellResources.put(res, 0); // consume all visible resources
						cellResources.put(res, available - (required - availableVisible));
					}
				} 
				else { // otherwise take them from the ones in the soil
					cellResources.put(res, available - required);
				}
			}
			
			if (available != null && available == 0) {
				cellResources.remove(res);
				cellResourceTypes.remove(res);
			}
		}

		playerUnit.energy -= GamePolicy.pickupCost; // update energy levels
		TransitionResult pickupres = new TransitionResult(transition.id);
		pickupres.errorType = TransitionResult.TransitionError.NoError;
		return pickupres;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected boolean validOperands(Transition transition) {
		HashMap<ResourceType, Integer> requiredResources = null;
		try{
			requiredResources = (HashMap<ResourceType, Integer>)transition.operands[1];
			// check for valid operand
			if (requiredResources == null) {
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
