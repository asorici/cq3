package org.aimas.craftingquest.core.actions;

import org.aimas.craftingquest.core.GamePolicy;
import org.aimas.craftingquest.state.Blueprint;
import org.aimas.craftingquest.state.GameState;
import org.aimas.craftingquest.state.PlayerState;
import org.aimas.craftingquest.state.Transition;
import org.aimas.craftingquest.state.Transition.ActionType;
import org.aimas.craftingquest.state.TransitionResult;

public class UpgradeAction extends Action {

	protected UpgradeAction(ActionType type) {
		super(type);
	}

	@Override
	protected TransitionResult handle(GameState game, PlayerState player, Transition transition) {
		
		Blueprint playerBlueprint = (Blueprint) transition.operands[1];
		Blueprint blueprint; // the real one
		
		if (GamePolicy.blueprints.contains(playerBlueprint)) {
			blueprint = GamePolicy.blueprints.get(GamePolicy.blueprints.indexOf(playerBlueprint));
		} else {
			TransitionResult res = new TransitionResult(transition.id);
			res.errorType = TransitionResult.TransitionError.BlueprintError;
			res.errorReason = "Wrong blueprint for object.";
			return res;
		}
		
		if (!player.availableBlueprints.contains(blueprint)) {
			TransitionResult res = new TransitionResult(transition.id);
			res.errorType = TransitionResult.TransitionError.MissingBlueprintError;
			res.errorReason = "Player attempted to use a blueprint he does not have.";
			return res;
		}
		
		if (blueprint.getLevel() == GamePolicy.maxLevels) {
			TransitionResult res = new TransitionResult(transition.id);
			res.errorType = TransitionResult.TransitionError.NoUpgradeError;
			res.errorReason = "No additional upgrade available for this blueprint.";
			return res;
		}
		
		Blueprint upbp = game.getNextLevel(blueprint);
		
		//check if he has gold
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
		try {
			bp = (Blueprint) transition.operands[1];
			if (bp == null) {
				return false;
			}
		} catch (ClassCastException ex) {
			return false;
		}

		return true;
	}
	
	
	@Override
	public void printToGuiLog(GameState game, PlayerState player, Transition transition) {
		if (playerUnit != null) {
			Blueprint playerBlueprint = (Blueprint) transition.operands[1];
			
			gui_logger.info(game.round.currentRound + " " + transition.operator.name() + " " + player.id + " " 
					+ playerUnit.id + " " + playerUnit.pos.x + " " + playerUnit.pos.y + " " + player.getScore() + " " 
					+ playerUnit.energy + " " + playerBlueprint.getType().name() + " " + (playerBlueprint.getLevel() + 1));
		}
	}
}
