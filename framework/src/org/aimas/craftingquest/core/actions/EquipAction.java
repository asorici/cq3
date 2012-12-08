package org.aimas.craftingquest.core.actions;

import java.util.HashMap;
import java.util.Iterator;

import org.aimas.craftingquest.core.GamePolicy;
import org.aimas.craftingquest.state.Blueprint;
import org.aimas.craftingquest.state.GameState;
import org.aimas.craftingquest.state.PlayerState;
import org.aimas.craftingquest.state.Transition;
import org.aimas.craftingquest.state.TransitionResult;
import org.aimas.craftingquest.state.UnitState;
import org.aimas.craftingquest.state.Transition.ActionType;
import org.aimas.craftingquest.state.objects.ArmourObject;
import org.aimas.craftingquest.state.objects.CraftedObject;
import org.aimas.craftingquest.state.objects.EquippableObject;
import org.aimas.craftingquest.state.objects.SwordObject;
import org.aimas.craftingquest.state.objects.CraftedObject.BasicResourceType;

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
			res.errorType = TransitionResult.TransitionError.OperandError;
			res.errorReason = "Player tried to equip something that is not a sword nor a armour.";
			return res;
		}

		TransitionResult craftres = new TransitionResult(transition.id);
		craftres.errorType = TransitionResult.TransitionError.NoError;
		return craftres;
	}

	@Override
	protected boolean validOperands(Transition transition) {
		EquippableObject target = null;

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