package org.aimas.craftingquest.core.actions;

import java.util.Iterator;

import org.aimas.craftingquest.core.GamePolicy;
import org.aimas.craftingquest.state.BasicUnit;
import org.aimas.craftingquest.state.CellState;
import org.aimas.craftingquest.state.GameState;
import org.aimas.craftingquest.state.PlayerState;
import org.aimas.craftingquest.state.Point2i;
import org.aimas.craftingquest.state.Transition;
import org.aimas.craftingquest.state.UnitState;
//import org.aimas.craftingquest.state.CellState.CellType;
import org.aimas.craftingquest.state.Transition.ActionType;
import org.aimas.craftingquest.state.objects.Tower;
import org.aimas.craftingquest.state.objects.TrapObject;
import org.aimas.craftingquest.state.TransitionResult;

public class MoveAction extends Action {

	public MoveAction(ActionType type) {
		super(type);
	}

	@Override
	protected TransitionResult handle(GameState game, PlayerState player,
			Transition transition) {
		
		// check if frozen
		if (playerUnit.isFrozen()) {
			TransitionResult res = new TransitionResult(transition.id);
			res.errorType = TransitionResult.TransitionError.Frozen;
			res.errorReason = "Unit is frozen";
			return res;
		}
		
		
		// check allowed distance
		Point2i toPos = (Point2i) transition.operands[1];
		Point2i fromPos = playerUnit.pos;
		if (Math.abs(toPos.x - fromPos.x) > 1
				|| Math.abs(toPos.y - fromPos.y) > 1) {
			TransitionResult res = new TransitionResult(transition.id);
			res.errorType = TransitionResult.TransitionError.MoveError;
			res.errorReason = "Move allowed only to neighboring cells";
			return res;
		}

		// check position bounds
		if (toPos.x < 0 || toPos.y < 0 || toPos.x >= game.map.cells.length
				|| toPos.y >= game.map.cells.length) {
			TransitionResult res = new TransitionResult(transition.id);
			res.errorType = TransitionResult.TransitionError.MoveError;
			res.errorReason = "Move allowed only within map bounds";
			return res;
		}

		// check no object is there
		if (game.map.cells[toPos.y][toPos.x].strategicObject != null
				&& game.map.cells[toPos.y][toPos.x].strategicObject instanceof Tower
				) {
			TransitionResult res = new TransitionResult(transition.id);
			res.errorType = TransitionResult.TransitionError.ObstacleError;
			res.errorReason = "Move not allowed to cells containing strategic structures.";
			return res;
		}
		
		
		// check enough energy for action
		int carriedResourcesAmount = 0;
		for (Integer quant : playerUnit.carriedResources.values()) {
			carriedResourcesAmount += quant;
		}
		for (Integer quant : playerUnit.carriedObjects.values()) {
			carriedResourcesAmount += quant;
		}
		int requiredEnergy = (int) (GamePolicy.moveBase + GamePolicy.resourceMoveCost
				* carriedResourcesAmount / 2);

		if (playerUnit.energy < requiredEnergy) {
			TransitionResult res = new TransitionResult(transition.id);
			res.errorType = TransitionResult.TransitionError.NoEnergyError;
			res.errorReason = "Not enough energy points left for move";
			return res;
		}

		// all ok..then move - update unit position, sightMatrix and energy
		// points
		playerUnit.pos = toPos;
		playerUnit.energy -= requiredEnergy;
		updateUnitSight(game, playerUnit, toPos);

		// update cells with new unit position
		Iterator<BasicUnit> it = game.map.cells[fromPos.y][fromPos.x].cellUnits
				.iterator();
		while (it.hasNext()) {
			BasicUnit u = it.next();
			if (u.playerID == playerUnit.playerID) {
				it.remove();
				break;
			}
		}
		game.map.cells[toPos.y][toPos.x].cellUnits.add(playerUnit
				.getOpponentPerspective());
		
		
		// check no object is there
		if (game.map.cells[toPos.y][toPos.x].strategicObject != null
				&& game.map.cells[toPos.y][toPos.x].strategicObject instanceof TrapObject
				) {
			TrapObject trap = (TrapObject) game.map.cells[toPos.y][toPos.x].strategicObject;
			playerUnit.energy = 0;
			playerUnit.freeze(trap.getLevel());
		}
		
		TransitionResult moveres = new TransitionResult(transition.id);
		moveres.errorType = TransitionResult.TransitionError.NoError;
		return moveres;
	}

	@Override
	protected boolean validOperands(Transition transition) {
		return true;
	}

	private void updateUnitSight(GameState game, UnitState playerUnit, Point2i pos) {
		int sightRadius = GamePolicy.sightRadius;

		CellState[][] unitSight = playerUnit.sight;
		for (int i = 0, y = pos.y - sightRadius; y <= pos.y + sightRadius; y++, i++) {
			for (int j = 0, x = pos.x - sightRadius; x <= pos.x + sightRadius; x++, j++) {
				unitSight[i][j] = null;
				if (x >= 0 && x < GamePolicy.mapsize.x && y >= 0 && y < GamePolicy.mapsize.y) {
					unitSight[i][j] = game.map.cells[y][x];
				}
			}
		}
	}
}
