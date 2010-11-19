package org.aimas.craftingquest.state;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import org.aimas.craftingquest.core.GamePolicy;
import org.aimas.craftingquest.state.CraftedObject.BasicResourceType;

/**
 * 
 * @author Razvan
 */
@SuppressWarnings("serial")
public class UnitState implements Serializable {

	public enum UnitType {
		Crocodile, Tazmanian, Fox
	}

	private BasicUnit opponentPerspective = null;
	
	/* what */
	public int id;
	public int playerID;
	public UnitType type;
	public int energy;

	// must be cleared before each transition
	public List<int[]>[][] scannedResourceAttributes = null;
	public HashMap<BasicResourceType, Integer> currentCellResources = new HashMap<BasicResourceType, Integer>();

	// pertain from round to round
	public HashMap<BasicResourceType, Integer> carriedResources = new HashMap<BasicResourceType, Integer>();
	public HashMap<CraftedObject, Integer> carriedObjects = new HashMap<CraftedObject, Integer>();

	/* where */
	public Point2i pos;
	public CellState[][] sight;

	public UnitState() {
		int sightDim = 2 * GamePolicy.sightRadius + 1;
		sight = new CellState[sightDim][sightDim];
	}

	public UnitState(int id, int playerID, UnitType type, Point2i pos, int energy) {
		this.id = id;
		this.playerID = playerID;
		this.type = type;
		this.pos = pos;
		this.energy = energy;

		int sightDim = 2 * GamePolicy.sightRadius + 1;
		sight = new CellState[sightDim][sightDim];
	}

	@Override
	public String toString() {
		String info = "";
		info += "playerID=" + playerID + " type=" + type + " pos=" + pos + " energy=" + energy + "\n";
		info += "    carried resources: \n";
		info += "        ";
		for (BasicResourceType br : carriedResources.keySet()) {
			info += br.name() + ":" + carriedResources.get(br) + " ";
		}
		info += "\n";
		
		info += "    carried objects: \n";
		info += "        ";
		for (CraftedObject o : carriedObjects.keySet()) {
			info += o.getType().name() + ":" + carriedObjects.get(o) + " ";
		}
		info += "\n";
		

		return info;
	}

	public BasicUnit getOpponentPerspective() {
		if (opponentPerspective == null) {
			opponentPerspective = new BasicUnit();
			opponentPerspective.set(type, playerID, energy);
		}
		else {
			opponentPerspective.set(type, playerID, energy);
		}
		
		return opponentPerspective;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof UnitState)) {
			return false;
		}
		
		final UnitState other = (UnitState)obj;
		if (type != other.type) {
			return false;
		}
		
		if (playerID != other.playerID) {
			return false;
		}
		
		return true;
	}
}
