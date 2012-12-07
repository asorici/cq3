package org.aimas.craftingquest.core.actions;

import org.aimas.craftingquest.state.Blueprint;
import org.aimas.craftingquest.state.GameState;
import org.aimas.craftingquest.state.Merchant;
import org.aimas.craftingquest.state.PlayerState;
import org.aimas.craftingquest.state.Transition;
import org.aimas.craftingquest.state.TransitionResult;
import org.aimas.craftingquest.state.Transition.ActionType;

public class BuyBlueprintAction extends Action {
	
	public BuyBlueprintAction(ActionType type) {
		super(type);
	}
	
	
	@Override
	protected TransitionResult handle(GameState game, PlayerState player, Transition transition) {
		Blueprint blueprint = (Blueprint)transition.operands[1];
		
		// check if the unit is near a merchant
		Merchant nearMerchant = null;
		for (Merchant m : game.merchantList) {
			if (Math.abs(m.getPosition().x - playerUnit.pos.x) <= 1
					&& Math.abs(m.getPosition().y - playerUnit.pos.y) <= 1) {
				nearMerchant = m;
				break;
			}
		}

		if (nearMerchant == null) {
			TransitionResult res = new TransitionResult(transition.id);
			res.errorType = TransitionResult.TransitionError.BuyRequestError;
			res.errorReason = "Not near a merchant camp. Cannot request to buy a blueprint.";
			return res;
		}

		// check that the merchant holds the desired blueprint
		boolean hasBlueprint = false;
		for (Blueprint bp : nearMerchant.getBlueprints()) {
			if (bp.getDescribedObject().getType() == blueprint
					.getDescribedObject().getType()) {
				hasBlueprint = true;
			}
		}

		if (!hasBlueprint) {
			TransitionResult res = new TransitionResult(transition.id);
			res.errorType = TransitionResult.TransitionError.BuyRequestError;
			res.errorReason = "Merchant does not hold requested blueprint.";
			return res;
		}

		// check for enough credit
		if (player.credit < blueprint.getValue()) {
			TransitionResult res = new TransitionResult(transition.id);
			res.errorType = TransitionResult.TransitionError.NoCreditError;
			res.errorReason = "Not enough credit to buy the requested blueprint.";
			return res;
		}

		// all is ok - subtract credit, add blueprint to knownBlueprints
		player.boughtBlueprints.add(blueprint);
		player.credit -= blueprint.getValue();

		TransitionResult blueprintres = new TransitionResult(transition.id);
		blueprintres.errorType = TransitionResult.TransitionError.NoError;
		return blueprintres;
	}

	@Override
	protected boolean validOperands(Transition transition) {
		Blueprint blueprint = null;
		
		try {
			blueprint = (Blueprint)transition.operands[1];
			if (blueprint == null) {
				return false;
			}
		}
		catch (ClassCastException ex) {
			return false;
		}
		
		return true;
	}

}
