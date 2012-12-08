package org.aimas.craftingquest.state;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.aimas.craftingquest.core.GamePolicy;
import org.aimas.craftingquest.state.CraftedObject.BasicResourceType;

/**
 * 
 * @author Razvan
 */
@SuppressWarnings("serial")
public class GameState implements Serializable {

	/* state */
	public MapState map;
	public RoundState round;
	
	//public List<PlayerState> playerStates;
	public HashMap<Integer, PlayerState> playerStates;
	public HashMap<Integer, List<Tower>> playerTowers;
	public List<Merchant> merchantList;
	public List<Blueprint> blueprints;
	public HashMap<BasicResourceType, Integer> resourceAmountsByType;
	
	public GameState() {
		//playerStates = new ArrayList<PlayerState>();
		playerStates = new HashMap<Integer, PlayerState>();
		playerTowers = new HashMap<Integer, List<Tower>>();
		merchantList = new ArrayList<Merchant>();
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
	
	public void initializeTowerLists(){
		List<Integer> playerIds = getPlayerIds();
		for (Integer pId : playerIds) {
			playerTowers.put(pId, new ArrayList<Tower>());
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
				List<Tower> pTowers = playerTowers.get(pId);
				opponentTowers.addAll(pTowers);
			}
		}
		
		return opponentTowers;
	}
}