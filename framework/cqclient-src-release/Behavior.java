import java.util.HashMap;
import java.util.Random;

import org.aimas.craftingquest.state.CellState;
import org.aimas.craftingquest.state.PlayerState;
import org.aimas.craftingquest.state.Point2i;
import org.aimas.craftingquest.state.UnitState;
import org.aimas.craftingquest.state.resources.ResourceType;
import org.aimas.craftingquest.user.IPlayerActions;

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
			performBuildTower(state);
			
			performBuildTrap(state);
			performEquip(state);
			performAttack(state);
		}
		
		for(int i = 0; i < 3; i++) {
			if (state != null) {
				UnitState unit = getBehaviorUnit(state);
				Point2i newPos = smartChoiceDst(unit);
				System.out.println("Attempted new position: " + newPos);
				state = cmd.move(unit, newPos);
			}
		}
		return state;
	}

	private UnitState getBehaviorUnit(PlayerState state) {
		if (state == null) {
			// it is not our turn we have moved too fast or too slow
			return null;
		}
		
		for (UnitState unit : state.units) {
			if (unit.id == unitId) {
				return unit;
			}
		}
		
		// should never get here
		return null;
	}
	
	private void performDig(PlayerState state) {
		UnitState unit = getBehaviorUnit(state);
		if (unit == null) return;
		
		if (unit.energy > 20) {
			dugUpResources = null;
			
			state = cmd.dig(unit);
			if (state != null) {
				System.out.println(" ++++ Dig result ++++ ");
				dugUpResources = getBehaviorUnit(state).currentCellResources;
				if (dugUpResources != null) {
					System.out.println(dugUpResources);
				}
			}
		}
	}
	
	private void performPickUp(PlayerState state) {
		UnitState unit = getBehaviorUnit(state);
		if (unit == null) return;
		
		if (unit.energy > 20 && dugUpResources != null && !dugUpResources.isEmpty()) {
			state = cmd.pickupResources(unit, dugUpResources);
			
			if (state != null) {
				System.out.println(" ++++ Pick Up result ++++ ");
				System.out.println(getBehaviorUnit(state).carriedResources);
			}
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
			
			if (state != null) {
				unit = getBehaviorUnit(state);
				
				int ii = unit.sight.length / 2;
				if (unit.sight[ii][ii] != null) {
					System.out.println("Visible resources: " + unit.sight[ii][ii].visibleResources);
				}
			}
		}
	}
	
	private void performBuildObject(PlayerState state) {
		// TODO Auto-generated method stub
		
	}
	
	private void performBuildTower(PlayerState state) {
		// TODO Auto-generated method stub
	
	}
	
	private void performBuildTrap(PlayerState state) {
		// TODO Auto-generated method stub
		
	}
	
	private void performAttack(PlayerState state) {
		// TODO Auto-generated method stub
		
	}

	private void performEquip(PlayerState state) {
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
