package org.aimas.craftingquest.core.actions;

import java.util.Iterator;

import org.aimas.craftingquest.state.Blueprint;
import org.aimas.craftingquest.state.GameState;
import org.aimas.craftingquest.state.PlayerState;
import org.aimas.craftingquest.state.Transition;
import org.aimas.craftingquest.state.Transition.ActionType;
import org.aimas.craftingquest.state.objects.CraftedObjectType;
import org.aimas.craftingquest.state.resources.ResourceType;
import org.aimas.craftingquest.state.TransitionResult;

public class UpgradeAction extends Action {

	protected UpgradeAction(ActionType type) {
		super(type);
	}

	@Override
	protected TransitionResult handle(GameState game, PlayerState player,
			Transition transition) {
		
		Blueprint playerBlueprint = (Blueprint) transition.operands[1];
		Blueprint blueprint; // the real one
		if (game.blueprints.contains(playerBlueprint)) {
			blueprint = game.blueprints.get(game.blueprints.indexOf(playerBlueprint));
		} else {
			TransitionResult res = new TransitionResult(transition.id);
			res.errorType = TransitionResult.TransitionError.BlueprintError;
			res.errorReason = "Wrong blueprint for trap.";
			return res;
		}
		
		if (!player.availableBlueprints.contains(blueprint)) {
			TransitionResult res = new TransitionResult(transition.id);
			res.errorType = TransitionResult.TransitionError.MissingBlueprintError;
			res.errorReason = "Player attempted to use a blueprint he does not have.";
			return res;
		}
		
		Blueprint upbp = game.getNextLevel(blueprint);
		
		//check if he has goldeanu
		
		Integer required = blueprint.getUpgradeCost();
		Integer available = player.getGold();
		if (available < required) {
			TransitionResult res = new TransitionResult(transition.id);
			res.errorType = TransitionResult.TransitionError.NoCreditError;
			res.errorReason = "Player attempted to use a blueprint with no money in his pockets.";
			return res;			
		}
		
		player.setGold(available-required);
		
		player.availableBlueprints.add(upbp);
		
		TransitionResult upgraderes = new TransitionResult(transition.id);
		upgraderes.errorType = TransitionResult.TransitionError.NoError;
		return upgraderes;
	}

	@Override
	protected boolean validOperands(Transition transition) {
		Blueprint bp = null;
		int a;
		try {
			bp = (Blueprint) transition.operands[1];
			a = (Integer) transition.operands[2];
			if (bp == null) {
				return false;
			}
		} catch (ClassCastException ex) {
			return false;
		}

		return true;
	}

}
