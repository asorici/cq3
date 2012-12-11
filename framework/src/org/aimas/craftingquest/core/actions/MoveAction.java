package org.aimas.craftingquest.core.actions;

import java.util.Iterator;
import java.util.List;

import org.aimas.craftingquest.core.GamePolicy;
import org.aimas.craftingquest.state.BasicUnit;
import org.aimas.craftingquest.state.CellState;
import org.aimas.craftingquest.state.GameState;
import org.aimas.craftingquest.state.ICarriable;
import org.aimas.craftingquest.state.PlayerState;
import org.aimas.craftingquest.state.Point2i;
import org.aimas.craftingquest.state.Transition;
import org.aimas.craftingquest.state.UnitState;
//import org.aimas.craftingquest.state.CellState.CellType;
import org.aimas.craftingquest.state.CellState.CellType;
import org.aimas.craftingquest.state.Transition.ActionType;
import org.aimas.craftingquest.state.objects.ICrafted;
import org.aimas.craftingquest.state.objects.Tower;
import org.aimas.craftingquest.state.objects.TrapObject;
import org.aimas.craftingquest.state.resources.ResourceType;
import org.aimas.craftingquest.state.TransitionResult;

public class MoveAction extends Action {

	public MoveAction(ActionType type) {
		super(type);
	}

	@Override
	protected TransitionResult handle(GameState game, PlayerState player,
			Transition transition) {
		// check allowed distance
		Point2i toPos = (Point2i) transition.operands[1];
		Point2i fromPos = playerUnit.pos;
		if (Math.abs(toPos.x - fromPos.x) > 1 || Math.abs(toPos.y - fromPos.y) > 1) {
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
		
		// check terrain type
		if(game.map.cells[toPos.y][toPos.x].type == CellType.Rock) {
			TransitionResult res = new TransitionResult(transition.id);
			res.errorType = TransitionResult.TransitionError.TerrainError;
			res.errorReason = "Move not allowed to cells with rocky terrain.";
			return res;
		}
		
		// check no object is there
		if (game.map.cells[toPos.y][toPos.x].strategicObject != null
				&& game.map.cells[toPos.y][toPos.x].strategicObject instanceof Tower) {
			TransitionResult res = new TransitionResult(transition.id);
			res.errorType = TransitionResult.TransitionError.ObstacleError;
			res.errorReason = "Move not allowed to cells containing strategic structures.";
			return res;
		}
		
		
		// check enough energy for action
		int carriedResourcesWeight = 0;
		Iterator<ICrafted> coit = (Iterator<ICrafted>) playerUnit.carriedObjects.keySet().iterator();
		while (coit.hasNext()) {
			ICarriable co = (ICarriable) coit.next();
			carriedResourcesWeight += co.getWeight() * playerUnit.carriedObjects.get(co);
		}
		Iterator<ResourceType> crit = (Iterator<ResourceType>) playerUnit.carriedResources.keySet().iterator();
		while(crit.hasNext()) {
			ResourceType rt = crit.next();
			carriedResourcesWeight += rt.getWeight() * playerUnit.carriedResources.get(rt);
		}
		
		int requiredEnergy = (int) (GamePolicy.moveBase * (1 + GamePolicy.movePenaltyWeight/100));

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
		Iterator<BasicUnit> it = game.map.cells[fromPos.y][fromPos.x].cellUnits.iterator();
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
			player.freeze(playerUnit, trap.getLevel()+1);

			List<Tower> pTraps = game.playerTowers.get(trap.getPlayerID());
			PlayerState opponentState = game.playerStates.get(trap.getPlayerID());
			opponentState.availableTraps.add(trap);	// trap is no longer available
			pTraps.remove(trap);			// the weakened tower and remove it
			//game.gui_logger.info(state.round.currentRound + " RemoveTrap " + trap.getPosition().x + " " + trap.getPosition().y);
			game.playerStates.get(trap.getPlayerID()).triggerTrap();
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
