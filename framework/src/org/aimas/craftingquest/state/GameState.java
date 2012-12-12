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
	public HashMap<ResourceType, Integer> resourceAmountsByType;
	
	public int lastKiller;
	public int consecutiveKills;
	public boolean somebodyDied;
	
	public GameState() {
		playerStates = new HashMap<Integer, PlayerState>();
		
		lastKiller = -1;
		somebodyDied = false;
		
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
		Iterator<Blueprint> bpit = GamePolicy.blueprints.iterator();
		while (bpit.hasNext()) {
			Blueprint nextbp = bpit.next();
			if (nextbp.getType() == bp.getType() && nextbp.getLevel() == bp.getLevel() + 1) {
				return nextbp;
			}
		}
		return null;
	}
	
	public void killOne(int playerID) {
		if (somebodyDied == false) {
			somebodyDied = true;
			playerStates.get(playerID).setFirstBlood(1);
		}
		if (playerID == lastKiller) {
			consecutiveKills++;
			if (consecutiveKills == GamePolicy.killingSpreeThreshold) {
				playerStates.get(playerID).addKillingSpree();
				consecutiveKills = 0;
			}
		} else {
			lastKiller = playerID;
			consecutiveKills = 0;
		}
	}
}