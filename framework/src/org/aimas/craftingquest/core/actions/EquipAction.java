package org.aimas.craftingquest.core.actions;

import java.util.HashMap;
import java.util.Iterator;

import org.aimas.craftingquest.core.GamePolicy;
import org.aimas.craftingquest.state.ArmourObject;
import org.aimas.craftingquest.state.Blueprint;
import org.aimas.craftingquest.state.CraftedObject;
import org.aimas.craftingquest.state.EquippableObject;
import org.aimas.craftingquest.state.GameState;
import org.aimas.craftingquest.state.PlayerState;
import org.aimas.craftingquest.state.SwordObject;
import org.aimas.craftingquest.state.Transition;
import org.aimas.craftingquest.state.TransitionResult;
import org.aimas.craftingquest.state.UnitState;
import org.aimas.craftingquest.state.CraftedObject.BasicResourceType;
import org.aimas.craftingquest.state.Transition.ActionType;

public class EquipAction extends Action {
	
	public EquipAction(ActionType type) {
		super(type);
	}

	@Override
	protected TransitionResult handle(GameState game, PlayerState player,
			Transition transition) {
		CraftedObject target = (CraftedObject)transition.operands[1];

		// check that the unit has the required resources/objects required for
		// making the object
		boolean carried = (playerUnit.carriedObjects.get(target) != null);
		if (!carried) {
			TransitionResult res = new TransitionResult(transition.id);
			res.errorType = TransitionResult.TransitionError.EquipmentMissingError;
			res.errorReason = "Player does not hold that equipment.";
			return res;
		}

		if (target instanceof SwordObject) {
			playerUnit.equipedSword = (SwordObject) target;
			
		} else if (target instanceof ArmourObject) {
			playerUnit.equipedArmour = (ArmourObject) target;
		} else {
			TransitionResult res = new TransitionResult(transition.id);
			res.errorType = TransitionResult.TransitionError.EquipmentMissingError;
			res.errorReason = "Player does not hold that equipment.";
			return res;
		}
		//playerUnit.energy -= GamePolicy.buildCost; // update energy levels

		TransitionResult craftres = new TransitionResult(transition.id);
		craftres.errorType = TransitionResult.TransitionError.NoError;
		return craftres;
	}

	@Override
	protected boolean validOperands(Transition transition) {
		CraftedObject target = null;
		HashMap<CraftedObject, Integer> usedObjects = null;
		HashMap<BasicResourceType, Integer> usedResources = null;
		
		try {
			target = (EquippableObject)transition.operands[1];
			
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