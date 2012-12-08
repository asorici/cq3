package org.aimas.craftingquest.user;
import java.util.HashMap;
import java.util.Random;

import org.aimas.craftingquest.state.CellState;
import org.aimas.craftingquest.state.PlayerState;
import org.aimas.craftingquest.state.Point2i;
import org.aimas.craftingquest.state.UnitState;
import org.aimas.craftingquest.state.resources.ResourceType;

public class Behavior {
	IPlayerActions cmd;
	int unitId;
	
	Random rnd = new Random();
	int lastRound = -1;
	
	HashMap<ResourceType, Integer> dugUpResources;
	
	
	public Behavior (int unitId, IPlayerActions cmd) {
		this.unitId = unitId;
		this.cmd = cmd;
	}
	
	public void setCmd(IPlayerActions cmd) {
		this.cmd = cmd;
	}

	public IPlayerActions getCmd() {
		return cmd;
	}
	
	public void setUnitId(int unitId) {
		this.unitId = unitId;
	}
	
	public int getUnitId() {
		return unitId;
	}
	
	PlayerState nextAction(PlayerState state) {
		if (lastRound != state.round.currentRound) {
			lastRound = state.round.currentRound;
			
			performDig(state);
			performPickUp(state);
			performDrop(state);
			performBuildObject(state);
			performSellObject(state);
			performBuildTower(state);
		}
		
		for(int i = 0; i < 3; i++) {
			UnitState unit = getBehaviorUnit(state);
			Point2i newPos = smartChoiceDst(unit);
			System.out.println("Attempted new position: " + newPos);
			state = cmd.move(unit, newPos);
		}
		return state;
	}
	
	private UnitState getBehaviorUnit(PlayerState state) {
		for (UnitState unit : state.units) {
			if (unit.id == unitId) {
				return unit;
			}
		}
		
		// should never get here
		return null;
	}
	
	private void performDig(PlayerState state) {
		// TODO Auto-generated method stub
		UnitState unit = getBehaviorUnit(state);
		if (unit == null) return;
		
		if (unit.energy > 20) {
			dugUpResources = null;
			
			state = cmd.dig(unit);
			
			System.out.println(" ++++ Dig result ++++ ");
			dugUpResources = getBehaviorUnit(state).currentCellResources;
			if (dugUpResources != null) {
				System.out.println(dugUpResources);
			}
		}
	}
	
	private void performPickUp(PlayerState state) {
		UnitState unit = getBehaviorUnit(state);
		if (unit == null) return;
		
		if (unit.energy > 20 && dugUpResources != null && !dugUpResources.isEmpty()) {
			state = cmd.pickupResources(unit, dugUpResources);
			
			System.out.println(" ++++ Pick Up result ++++ ");
			System.out.println(getBehaviorUnit(state).carriedResources);
		}
	}
	
	private void performDrop(PlayerState state) {
		UnitState unit = getBehaviorUnit(state);
		if (unit == null) return;
		
		HashMap<ResourceType, Integer> aux = new HashMap<ResourceType, Integer>();
		
		if (!unit.carriedResources.isEmpty()) {
			for (ResourceType br : unit.carriedResources.keySet()) {
				int quantity = unit.carriedResources.get(br) / 2;
				if (quantity > 0) {
					aux.put(br, quantity);
				}
				
			}
			
			System.out.println(" ++++ Dropping ++++ ");
			System.out.println(aux);
			state = cmd.dropResources(unit, aux);
			unit = getBehaviorUnit(state);
			
			int ii = unit.sight.length / 2;
			if (unit.sight[ii][ii] != null) {
				System.out.println("Visible resources: " + unit.sight[ii][ii].visibleResources);
			}
		}
	}
	
	private void performBuildObject(PlayerState state) {
		// TODO Auto-generated method stub
		
	}
	
	private void performBuildTower(PlayerState state) {
		/*
		UnitState unit = getBehaviorUnit(state);
		if (unit == null) return;
		
		if (state.credit > 75) {
			state = cmd.placeTower(unit);
			if (state.validLastTransition()) {
				System.out.println(" ==== Last transition valid ==== ");
			}
			else {
				System.out.println(" ==== Last transition NOT valid ==== ");
				System.out.println(state.getLastTransitionError());
			}
			System.out.println("Available towers: " + state.availableTowers);
		}
		*/
	}
		

	private void performSellObject(PlayerState state) {
		// TODO Auto-generated method stub
		
	}	
	
	
	private boolean allowedTerrain(UnitState unit, CellState cell) {
		return true;		// this is very optimistic - the unit might get stuck for a while
	}
	
	
	private Point2i smartChoiceDst(UnitState unit) {
		Point2i currentPos = unit.pos;
		Point2i newPos = new Point2i(currentPos.x, currentPos.y);
		
		double rndVal = rnd.nextDouble();
		if (rndVal < 0.5) {
			newPos.y++;
		}
		else {
			if(rndVal < 0.7) {
				newPos.x--;
			}
			else {
				if (rndVal < 0.9) {
					newPos.x++;
				}
				else {
					newPos.y--;
				}
			}
		}
		
		return newPos;
	}
}
