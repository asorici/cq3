package org.aimas.craftingquest.core.actions;

import java.util.HashMap;

import org.aimas.craftingquest.core.GamePolicy;
import org.aimas.craftingquest.state.CellState;
import org.aimas.craftingquest.state.CraftedObject;
import org.aimas.craftingquest.state.EquippableObject;
import org.aimas.craftingquest.state.GameState;
import org.aimas.craftingquest.state.PlayerState;
import org.aimas.craftingquest.state.StrategicObject;
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
		
		// check to see if any towers or traps are already in the cell
		CellState unitCell = game.map.cells[playerUnit.pos.y][playerUnit.pos.x];
		if (unitCell.strategicObject != null) {
			TransitionResult res = new TransitionResult(transition.id);
			res.errorType = TransitionResult.TransitionError.BuildError;
			res.errorReason = "Cannot place a trap in a cell that already contains a strategic resource";
			return res;
		}
		
		HashMap<CraftedObject, Integer> carriedObjects = playerUnit.carriedObjects;
		Integer carried = carriedObjects.get(trapObject);

		if (carried != null && 1 <= carried) {
			unitCell.strategicObject = (StrategicObject) trapObject;
			if (carried > 1) {
				carriedObjects.put(trapObject, carried - 1);
			} else {
				carriedObjects.remove(trapObject);
			}

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
