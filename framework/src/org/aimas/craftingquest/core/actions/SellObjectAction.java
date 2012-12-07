package org.aimas.craftingquest.core.actions;

import java.util.HashMap;

import org.aimas.craftingquest.state.CraftedObject;
import org.aimas.craftingquest.state.GameState;
import org.aimas.craftingquest.state.PlayerState;
import org.aimas.craftingquest.state.Transition;
import org.aimas.craftingquest.state.TransitionResult;
import org.aimas.craftingquest.state.Transition.ActionType;

public class SellObjectAction extends Action {
	
	public SellObjectAction(ActionType type) {
		super(type);
	}
	
	@Override
	protected TransitionResult handle(GameState game, PlayerState player, Transition transition) {
		CraftedObject obj = (CraftedObject)transition.operands[1];
		Integer quantity = (Integer)transition.operands[2];
		
		HashMap<CraftedObject, Integer> carriedObjects = playerUnit.carriedObjects;
		
		Integer carried = carriedObjects.get(obj);
		if (carried != null && quantity <= carried) {
			player.credit += obj.getValue() * quantity;				// update team score
			carriedObjects.put(obj, carried - quantity);			// update amount of carried objects
		}
		else {
			TransitionResult res = new TransitionResult(transition.id);
			res.errorType = TransitionResult.TransitionError.SellRequestError;
			res.errorReason = "No (or not enough) crafted objects of given type available to sell";
			return res;
		}
		
		TransitionResult sellres = new TransitionResult(transition.id);
		sellres.errorType = TransitionResult.TransitionError.NoError;
		return sellres;
	}

	@Override
	protected boolean validOperands(Transition transition) {
		CraftedObject obj = null;
		Integer quantity = null;
		
		try {
			obj = (CraftedObject)transition.operands[1];
			quantity = (Integer)transition.operands[2];
			
			if (obj == null || quantity == null) {
				return false;
			}
		}
		catch(ClassCastException ex) {
			return false;
		}
		
		return true;
	}

}
