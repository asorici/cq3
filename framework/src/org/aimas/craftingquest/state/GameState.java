package org.aimas.craftingquest.state;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.aimas.craftingquest.core.GamePolicy;
import org.aimas.craftingquest.state.objects.Tower;
import org.aimas.craftingquest.state.objects.TrapObject;
import org.aimas.craftingquest.state.resources.ResourceType;

/**
 * 
 * @author Razvan
 */
@SuppressWarnings("serial")
public class GameState implements Serializable {

	/* state */
	public MapState map;
	public RoundState round;
	
	public HashMap<Integer, PlayerState> playerStates;
	public HashMap<Integer, List<Tower>> playerTowers;
	public HashMap<Integer, List<TrapObject>> playerTraps;
	public List<Blueprint> blueprints;
	public HashMap<ResourceType, Integer> resourceAmountsByType;
	
	public GameState() {
		//playerStates = new ArrayList<PlayerState>();
		playerStates = new HashMap<Integer, PlayerState>();
		blueprints = new ArrayList<Blueprint>();
		
		round = new RoundState();
		round.currentRound = 0;
		round.roundDuration = GamePolicy.playerActionTime;
		round.noRounds = GamePolicy.lastTurn;
	}

	/**
	 * @return the gameStartTime
	 */
	public synchronized long getGameRoundStartTime() {
		return round.startTime;
	}

	/**
	 * @param gameStartTime the gameStartTime to set
	 */
	public synchronized void setGameRoundStartTime(long gameRoundStartTime) {
		round.startTime = gameRoundStartTime;
	}

	public int getTurn() {
		return round.currentRound;
	}

	public void setTurn(int turn) {
		round.currentRound = turn;
	}
	
	public void initializeTowerTrapLists(){
		List<Integer> playerIds = getPlayerIds();
		playerTowers = new HashMap<Integer, List<Tower>>();
		playerTraps = new HashMap<Integer, List<TrapObject>>();
		for (Integer pId : playerIds) {
			playerTowers.put(pId, new ArrayList<Tower>());
			playerTraps.put(pId, new ArrayList<TrapObject>());
		}
	}
	
	public List<Integer> getPlayerIds() {
		List<Integer> playerIds = new ArrayList<Integer>();
		for (PlayerState ps : playerStates.values()) {
			playerIds.add(ps.id);
		}
		
		return playerIds;
	}
	
	public List<Tower> getOpponentTowers(int playerID) {
		List<Tower> opponentTowers = new ArrayList<Tower>();
		List<Integer> playerIds = getPlayerIds();
		
		for (Integer pId : playerIds) {
			if (pId != playerID) {
				List<Tower> pTowers = playerStates.get(pId).availableTowers;
				opponentTowers.addAll(pTowers);
			}
		}
		
		return opponentTowers;
	}
	
	public Blueprint getNextLevel(Blueprint bp) {
		Iterator<Blueprint> bpit = blueprints.iterator();
		while(bpit.hasNext()) {
			Blueprint nextbp = bpit.next();
			if (nextbp.getType() == bp.getType() && nextbp.getLevel() == bp.getLevel()+1) {
				return bp;
			}
		}
		return null;
	}
}