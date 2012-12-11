package org.aimas.craftingquest.core.actions;

import org.aimas.craftingquest.state.GameState;
import org.aimas.craftingquest.state.PlayerState;
import org.aimas.craftingquest.state.Transition;
import org.aimas.craftingquest.state.Transition.ActionType;
import org.aimas.craftingquest.state.TransitionResult;
import org.aimas.craftingquest.state.UnitState;

public class AttackAction extends Action {
	public AttackAction(ActionType type) {
		super(type);
	}

	@Override
	protected TransitionResult handle(GameState game, PlayerState player, Transition transition) {
		int attackedPlayerID = (Integer)transition.operands[1];
		int attackedUnitID = (Integer)transition.operands[2];
		int energy = (Integer)transition.operands[3];

		// check for enough energy points
		if (playerUnit.energy < energy) {
			TransitionResult res = new TransitionResult(transition.id);
			res.errorType = TransitionResult.TransitionError.NoEnergyError;
			res.errorReason = "Not enough energy points left to do this attack";
			return res;
		}

		// check if the attacked player is real
		PlayerState attackedPlayer = game.playerStates.get(attackedPlayerID);
		if (attackedPlayer == null) {
			TransitionResult res = new TransitionResult(transition.id);
			res.errorType = TransitionResult.TransitionError.PlayerIdError;
			res.errorReason = "Cannot attack non-existing player";
			return res;
		}

		// check if the attacked unit is real
		UnitState attackedUnit = null;
		for (UnitState u : attackedPlayer.units) {
			if (u.id == attackedUnitID && u.playerID == attackedPlayerID) {
				attackedUnit = u;
				break;
			}
		}

		if (attackedUnit == null) {
			TransitionResult res = new TransitionResult(transition.id);
			res.errorType = TransitionResult.TransitionError.UnitIdError;
			res.errorReason = "Cannot attack non-existing unit";
			return res;
		}

		// check neighbouring conditions
		if (Math.abs(attackedUnit.pos.x - playerUnit.pos.x) > 1 ||
				Math.abs(attackedUnit.pos.y - playerUnit.pos.y) > 1) {
			TransitionResult res = new TransitionResult(transition.id);
			res.errorType = TransitionResult.TransitionError.ProximityError;
			res.errorReason = "Cannot attack non-neighbouring unit";
			return res;
		}

		// do attack
		int attackValue = computeAttackPower(energy, playerUnit, attackedUnit);
		attackedUnit.life -= attackValue;
		playerUnit.energy -= energy;

		// do retaliate
		if (attackedUnit.life > 0 && attackedUnit.retaliateEnergy >= 0 &&
				attackedUnit.retaliateEnergy >= attackedUnit.retaliateThreshold &&
				attackedUnit.retaliateEnergy <= attackedUnit.energy) {
			int retaliateValue = computeAttackPower(
					attackedUnit.retaliateEnergy,
					attackedUnit, playerUnit);
			playerUnit.life -= retaliateValue;
			attackedUnit.energy -= attackedUnit.retaliateEnergy;
		}

		// check for dead units and update scores
		if (attackedUnit.life < 0)
			player.killOne(false);

		if (playerUnit.life < 0) {
			attackedPlayer.killOne(true);
			player.die();
		}

		TransitionResult res = new TransitionResult(transition.id);
		res.errorType = TransitionResult.TransitionError.NoError;
		return res;
	}

	@Override
	protected boolean validOperands(Transition transition) {
		return true;
	}

	private int computeAttackPower(int baseAttack, UnitState attacker, UnitState defender) {
		float swordMod = 1, shieldMod = 1;

		if (attacker.equipedSword != null)
			//swordMod += attacker.equipedSword.getDefence() / 100.0;
			swordMod += attacker.equipedSword.getAttack() / 100.0;

		if (defender.equipedArmour != null)
			shieldMod -= defender.equipedArmour.getDefence() / 100.0;

		return (int)Math.round(baseAttack * swordMod * shieldMod);
	}
}
