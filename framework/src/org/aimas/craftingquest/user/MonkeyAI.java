package org.aimas.craftingquest.user;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.aimas.craftingquest.state.PlayerState;
import org.aimas.craftingquest.state.UnitState;
import org.apache.log4j.Level;

public class MonkeyAI extends AIThread {
	// player data
	PlayerState state;
	Random rnd;
	
	Map<Integer, Behavior> unitBehaviors;
	
	public MonkeyAI() {
	}
	
	@Override
	protected void initPlayer() {
		unitBehaviors = new HashMap<Integer, Behavior>();
		
		for (UnitState unit : getPlayerState().units) {
			unitBehaviors.put(unit.id, new Behavior(unit.id, getCmd()));
		}
	}
	
	@Override
	protected void actIntelligently() {
		// TODO Auto-generated method stub
		log("[AI][beginRound]", Level.INFO);
		state = getPlayerState();
		
		int nrUnits = state.units.size();
		
		for (int i = 0; i < nrUnits; i++) {
			UnitState unit = state.units.get(i);
			log("[AI]unit state: " + unit.toString(), Level.INFO);
			
			Behavior unitBehavior = unitBehaviors.get(unit.id);
			state = unitBehavior.nextAction(state);
			
			if (state.validLastTransition()) {
				System.out.println(" ==== Last transition valid ==== ");
			}
			else {
				System.out.println(" ==== Last transition NOT valid ==== ");
				System.out.println(state.getLastTransitionError());
			}
		}
	}
	
}

