package org.aimas.craftingquest.core.actions;

import org.aimas.craftingquest.state.Blueprint;
import org.aimas.craftingquest.state.GameState;
import org.aimas.craftingquest.state.PlayerState;
import org.aimas.craftingquest.state.Transition;
import org.aimas.craftingquest.state.Transition.ActionType;
import org.aimas.craftingquest.state.TransitionResult;
import org.aimas.craftingquest.state.objects.ArmourObject;
import org.aimas.craftingquest.state.objects.ICrafted;
import org.aimas.craftingquest.state.objects.IEquippable;
import org.aimas.craftingquest.state.objects.SwordObject;

public class EquipAction extends Action {
	
	public EquipAction(ActionType type) {
		super(type);
	}

	@Override
	protected TransitionResult handle(GameState game, PlayerState player,
			Transition transition) {
		ICrafted target = (ICrafted)transition.operands[1];
		
		// check that the unit has the required resources/objects required for
		// making the object
		boolean isCarried = (playerUnit.carriedObjects.containsKey(target) &&
				playerUnit.carriedObjects.get(target) > 0);
		if (!isCarried) {
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
		IEquippable target = null;
		try {
			target = (IEquippable)transition.operands[1];
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
	
	@Override
	public void printToGuiLog(GameState game, PlayerState player, Transition transition) {
		if (playerUnit != null) {
			ICrafted target = (ICrafted)transition.operands[1];
			
			gui_logger.info(game.round.currentRound + " " + transition.operator.name() + " " + player.id + " " 
					+ playerUnit.id + " " + playerUnit.pos.x + " " + playerUnit.pos.y + " " + player.getScore() + " " 
					+ playerUnit.energy + " " + target.getType().name() + " " + target.getLevel());
		}
	}
}
