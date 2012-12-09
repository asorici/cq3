package org.aimas.craftingquest.core;

import java.util.List;

import org.aimas.craftingquest.core.actions.Action;
import org.aimas.craftingquest.core.energyreplenishmodels.EnergyReplenishModel;
import org.aimas.craftingquest.state.CellState;
import org.aimas.craftingquest.state.GameState;
import org.aimas.craftingquest.state.PlayerState;
import org.aimas.craftingquest.state.Point2i;
import org.aimas.craftingquest.state.Transition;
import org.aimas.craftingquest.state.Transition.ActionType;
import org.aimas.craftingquest.state.TransitionResult;
import org.aimas.craftingquest.state.TransitionResult.TransitionError;
import org.aimas.craftingquest.state.UnitState;
import org.aimas.craftingquest.state.objects.Tower;
import org.apache.log4j.Logger;

/**
 * 
 * @author alex
 */
public class ActionEngine {

	private static Logger gui_logger = Logger.getLogger("org.aimas.craftingquest.core.guilogger");
	
	GameState game;

	public ActionEngine(GameState gameState) {
		game = gameState;
	}

	
	public TransitionResult process(PlayerState player, Transition transition) {
		refresh(player);
		
		// first check if the operator is of Nothing or RequestState type
		// these are just for filling up and synchronization - so return the OK
		if(transition.operator == ActionType.Nothing || transition.operator == ActionType.RequestState) {
			TransitionResult res = new TransitionResult(transition.id);
			res.errorType = TransitionResult.TransitionError.NoError;
			return res;
		}
		else {
			// if not it must be a game action
			Action playerAction = Action.getInstance(transition.operator);
			if (playerAction == null) {
				return new TransitionResult(transition.id, TransitionError.UnknownActionError, "Unknown Action");
			}
			else {
				return playerAction.doAction(game, player, transition);
			}
		}
		
	}
	
	
	/* =========================================================================================== */
	
	
	// clears transient fields (currentCellResources, scannedAttributes) of each of the players units
	private void refresh(PlayerState player) {
		player.response = null;							// reset player transition response
		
		for (UnitState unit : player.units) {			// reset each unit's dig and scan results
			unit.currentCellResources.clear();
			unit.scannedResourceAttributes = null;
		}
	}
	
	protected void unfreeze(GameState state, Integer playerID) {
		PlayerState playerState = state.playerStates.get(playerID);
		for (UnitState unit : playerState.units) {
			unit.unfreeze();
		}
	}
	
	protected void doTowerDrain(GameState state, Integer playerID) {
		PlayerState playerState = state.playerStates.get(playerID);
		
		for (UnitState unit : playerState.units) {
			List<Tower> opponentTowers = state.getOpponentTowers(playerID);
			
			for (Tower oppTower : opponentTowers) {
				if (Math.abs(oppTower.getPosition().x - unit.pos.x) <= GamePolicy.towerCutoffRadius && 
					Math.abs(oppTower.getPosition().y - unit.pos.y) <= GamePolicy.towerCutoffRadius) {
					
					int distance = Math.min( Math.abs(oppTower.getPosition().x - unit.pos.x), Math.abs(oppTower.getPosition().y - unit.pos.y) );
					if (distance == 0) {	// can happen if a player constructs a tower in a cell
						distance = 1;		// that contains an opponents unit
					}
					int drainAmount = GamePolicy.towerDrainBase / distance;
					
					unit.energy -= drainAmount;						// drain unit energy
					oppTower.weakenTower(drainAmount);				// and also weaken tower with the same amount
					
					if (oppTower.getRemainingStrength() <= 0) {		// if the tower has been weakened enough => destroy it
						List<Integer> playerIds = state.getPlayerIds();
						for (Integer pId : playerIds) {
							boolean foundTower = false;
							
							if (pId != playerID) {
								//List<Tower> pTowers = state.playerTowers.get(pId);	// get opponent tower list
								List<Tower> pTowers = state.playerStates.get(pId).availableTowers;	// get opponent tower list
								
								for (Tower t : pTowers) {			// see if it contains 
									if (t.getPosition().isEqual(oppTower.getPosition())) {
										PlayerState opponentState = state.playerStates.get(pId);
										opponentState.availableTowers.remove(t); // tower is no longer available
										
//										opponentState.availableTowers.put(t, false);	
//										pTowers.remove(t);			// the weakened tower and remove it
										foundTower = true;
										
										// log tower destruction
										gui_logger.info(state.round.currentRound + " RemoveTower " + t.getPosition().x + " " + t.getPosition().y);
										break;
									}
								}
							}
							
							if (foundTower) {		// there can't be more than one tower in that position
								break;
							}
						}
					}
					
				}
			}
				
		}
	}
	
	public void updateTowerSight(GameState state, Integer playerID) {
		PlayerState playerState = state.playerStates.get(playerID);
		
		for (Tower tower : playerState.availableTowers) {
			int towerLevel = tower.getLevel();
			int sightRadius = (int) (GamePolicy.towerCutoffRadius * 
					(1 + GamePolicy.levelIncrease[towerLevel - 1] / 100.0));
			
			CellState[][] towerSight = tower.sight;
			Point2i towerPos = tower.getPosition();
			
			for (int i = 0, y = towerPos.y - sightRadius; y <= towerPos.y + sightRadius; y++, i++) {
				for (int j = 0, x = towerPos.x - sightRadius; x <= towerPos.x + sightRadius; x++, j++) {
					towerSight[i][j] = null;
					if (x >= 0 && x < GamePolicy.mapsize.x && y >= 0 && y < GamePolicy.mapsize.y) {
						towerSight[i][j] = game.map.cells[y][x];
					}
				}
			}
		}
	}
	
	
	protected void replenishEnergy() {
		for (PlayerState pState : game.playerStates.values()) {
			for (UnitState unit : pState.units) {
				unit.energy = EnergyReplenishModel
					.getInstance(GamePolicy.energyReplenishModel)
					.replenishEnergy(unit.energy, GamePolicy.unitEnergy);
			}
		}
	}
}
