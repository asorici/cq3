package org.aimas.craftingquest.user;
import java.util.HashMap;
import java.util.Random;

import org.aimas.craftingquest.state.Blueprint;
import org.aimas.craftingquest.state.CellState;
import org.aimas.craftingquest.state.Merchant;
import org.aimas.craftingquest.state.PlayerState;
import org.aimas.craftingquest.state.Point2i;
import org.aimas.craftingquest.state.StrategicResource;
import org.aimas.craftingquest.state.UnitState;
import org.aimas.craftingquest.state.CraftedObject.BasicResourceType;
import org.aimas.craftingquest.state.StrategicResource.StrategicResourceType;

public class Behavior {
	IPlayerActions cmd;
	int unitId;
	
	Random rnd = new Random();
	int lastRound = -1;
	
	HashMap<BasicResourceType, Integer> dugUpResources;
	
	
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
			//checkForMerchantCamp(state);
			performScan(state);
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
	/*

	private void checkForMerchantCamp(PlayerState state) {
		// TODO Auto-generated method stub
		UnitState unit = getBehaviorUnit(state);
		if (unit == null) return;
		
		CellState[][] sight = unit.sight;
		int mid = sight.length / 2;
		if (sight[mid][mid] != null) {
			if (sight[mid][mid].resources == null) {
				System.out.println("Confirming that cell.resources is NULL");
			}
			
			if (sight[mid][mid].scanAttributes == null) {
				System.out.println("Confirming that cell.scanAttributes is NULL");
			}
			
			if (sight[mid][mid].visibleResources != null) {
				System.out.println("Confirming that cell.visibleRes is NOT NULL");
			}
		}
		
		for (int i = 0; i < sight.length; i++) {
			for (int j = 0; j < sight[i].length; j++) {
				if (sight[i][j] != null && Math.abs(sight[i][j].pos.x - unit.pos.x) == 1 && Math.abs(sight[i][j].pos.y - unit.pos.y) == 1) {
					StrategicObject strRes = sight[i][j].strategicResource;
					if (strRes != null && strRes.getType() == StrategicResourceType.Merchant) {
						Merchant m = (Merchant)strRes;
						for (Blueprint bp : m.getBlueprints()) {
							if(!state.boughtBlueprints.contains(bp) && state.credit > bp.getValue()) {
								state = cmd.buyBlueprint(unit, bp);
								break;
							}
						}
					}
				}
			}
		}
		
		System.out.println("Bought blueprints: " + state.boughtBlueprints);
	}
	*/
	
	private void performScan(PlayerState state) {
		// TODO Auto-generated method stub
		UnitState unit = getBehaviorUnit(state);
		if (unit == null) return;
		
		if (unit.energy > 20) {
			state = cmd.scan(unit);
			
			/*
			System.out.println(" ++++ Scan result ++++ ");
			List<int[]>[][] scanned = getBehaviorUnit(state).scannedResourceAttributes;
			
			for (int i = 0; i < scanned.length; i++) {
				for (int j = 0; j < scanned[i].length; j++) {
					if (scanned[i][j] != null) {
						for (int[] attrs : scanned[i][j]) {
							for (int k = 0; k < attrs.length; k++) {
								System.out.print(attrs[k] + "  ");
							}
							System.out.println();
						}
					}
				}
			}
			*/
		}
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
		
		HashMap<BasicResourceType, Integer> aux = new HashMap<BasicResourceType, Integer>();
		
		if (!unit.carriedResources.isEmpty()) {
			for (BasicResourceType br : unit.carriedResources.keySet()) {
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
