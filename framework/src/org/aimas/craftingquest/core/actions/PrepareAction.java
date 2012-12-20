package org.aimas.craftingquest.core.actions;

import org.aimas.craftingquest.state.GameState;
import org.aimas.craftingquest.state.PlayerState;
import org.aimas.craftingquest.state.Transition;
import org.aimas.craftingquest.state.Transition.ActionType;
import org.aimas.craftingquest.state.TransitionResult;
import org.aimas.craftingquest.state.UnitState;

public class PrepareAction extends Action {
	public PrepareAction(ActionType type) {
		super(type);
	}

	@Override
	protected TransitionResult handle(GameState game, PlayerState player, Transition transition) {
		int energy = (Integer)transition.operands[1];
		int threshold = (Integer)transition.operands[2];

		playerUnit.retaliateEnergy = energy;
		playerUnit.retaliateThreshold = threshold;

		TransitionResult res = new TransitionResult(transition.id);
		res.errorType = TransitionResult.TransitionError.NoError;
		return res;
	}

	@Override
	protected boolean validOperands(Transition transition) {
		return true;
	}
	
	@Override
	public void printToGuiLog(GameState game, PlayerState player, Transition transition) {
		if (playerUnit != null) {
			int energy = (Integer)transition.operands[1];
			
			gui_logger.info(game.round.currentRound + " " + transition.operator.name() + " " + player.id + " " 
				+ playerUnit.id + " " + playerUnit.pos.x + " " + playerUnit.pos.y + " " + player.gold + " " 
				+ playerUnit.energy + " " + energy);
		}
	}
}
