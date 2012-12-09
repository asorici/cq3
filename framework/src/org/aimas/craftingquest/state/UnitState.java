package org.aimas.craftingquest.state;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import org.aimas.craftingquest.core.GamePolicy;
import org.aimas.craftingquest.state.objects.ArmourObject;
import org.aimas.craftingquest.state.objects.ICrafted;
import org.aimas.craftingquest.state.objects.SwordObject;
import org.aimas.craftingquest.state.resources.*;

/**
 * Describes the state of a unit at any moment in the game. The class defines:
 * <ul>
 * <li>the possible unit types</li>
 * <li>the unit id</li>
 * <li>the id of the player the unit belongs to</li>
 * <li>the unit's position on the map</li>
 * <li>the unit's sight from its current position</li>
 * <li>the unit's energy levels</li>
 * <li>the opponent view for this unit</li>
 * <li>the equiped sword and armour</li>
 * <li>the list of carried basic resources and their quantities</li>
 * <li>the list of carried artifacts and their quantities</li>
 * </ul>
 * 
 * <p>Additionally, the state of unit contains two fields which get cleared before each action attempt.
 * These are the last scanned resource attributes and the last dug up soil resources.
 * </p> 
 */
@SuppressWarnings("serial")
public class UnitState implements Serializable {

	/**
	 * the opponent view for this unit
	 */
	private BasicUnit opponentPerspective = null;
	
	/**
	 * the unit id
	 */
	public int id;
	
	/**
	 * the id of the player that owns the unit
	 */
	public int playerID;
	
	/**
	 * the unit energy points
	 */
	public int energy;
	
	/**
	 * 
	 */
	public SwordObject equipedSword;
	
	/**
	 * 
	 */
	public ArmourObject equipedArmour;

	/**
	 * Energy used for retaliation.
	 */
	public int retaliateEnergy;

	/**
	 * Minimum energy at which retaliation is possible.
	 */
	public int retaliateThreshold;

	/**
	 * The unit's maximum energy points.
	 * Also, unit's life.
	 */
	public int life;

	// must be cleared before each transition
	/**
	 * the last scanned resource attributes. the field will be reset to null before each action attempt
	 */
	public List<int[]>[][] scannedResourceAttributes = null;
	
	/**
	 * the last dug up soil resources. the field will be reset to null before each action attempt
	 */
	public HashMap<ResourceType, Integer> currentCellResources = new HashMap<ResourceType, Integer>();

	// pertain from round to round
	/**
	 * the list of carried basic resources and their associated quantities
	 */
	public HashMap<ResourceType, Integer> carriedResources = new HashMap<ResourceType, Integer>();
	
	/**
	 * the list of carried objects and their associated quantities
	 */
	public HashMap<ICrafted, Integer> carriedObjects = new HashMap<ICrafted, Integer>();

	/* where */
	/**
	 * the unit position
	 */
	public Point2i pos;
	
	/**
	 * <p>The unit sight. It is a fixed 5x5 array of {@link CellState} objects.</p>
	 * <p>The unit is positioned in the middle of the visibility array.</p>
	 * <p>If the unit is near the margins of the map, cells falling outside the boundaries will be null.</p>  
	 */
	public CellState[][] sight;
	
	/*
	 * number of turns frozen
	 */
	
	int frozen;
	
	public void freeze (int trapLevel) {
		frozen = trapLevel;
	}

	public UnitState() {
		int sightDim = 2 * GamePolicy.sightRadius + 1;
		sight = new CellState[sightDim][sightDim];
		retaliateEnergy = 0;
		retaliateThreshold = 0;
	}

	public UnitState(int id, int playerID, Point2i pos, int energy) {
		this.id = id;
		this.playerID = playerID;
		this.pos = pos;
		this.energy = energy;
		this.life = energy;
		this.frozen = 0;

		int sightDim = 2 * GamePolicy.sightRadius + 1;
		sight = new CellState[sightDim][sightDim];
		retaliateEnergy = 0;
		retaliateThreshold = 0;
	}

	@Override
	public String toString() {
		String info = "";
		info += "playerID=" + playerID + " pos=" + pos + " energy=" + energy + " life=" + life + "\n";
		info += "    carried resources: \n";
		info += "        ";
		for (ResourceType br : carriedResources.keySet()) {
			info += br.name() + ":" + carriedResources.get(br) + " ";
		}
		info += "\n";
		
		info += "    carried objects: \n";
		info += "        ";
		// This needs some explaining
		for (ICrafted o : carriedObjects.keySet()) {
			info += o.getType().toString() + ":" + carriedObjects.get(o) + " ";
		}
		info += "\n";
		

		return info;
	}

	/**
	 * Gives the opponent's view of this unit.
	 * @return the opponent perspective of this unit
	 */
	public BasicUnit getOpponentPerspective() {
		if (opponentPerspective == null) {
			opponentPerspective = new BasicUnit();
			opponentPerspective.set(id, playerID, energy, life);
		}
		else {
			opponentPerspective.set(id, playerID, energy, life);
		}
		
		return opponentPerspective;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof UnitState)) {
			return false;
		}
		
		final UnitState other = (UnitState)obj;
		
		if (playerID != other.playerID) {
			return false;
		}
		
		if (id != other.id) {
			return false;
		}
		
		return true;
	}
	
	@Override
	public int hashCode() {
		return (playerID + "_" + id).hashCode();
	}

	public void unfreeze() {
		if (frozen > 0) frozen--;
		
	}

	public boolean isFrozen() {
		return (frozen>0);
	}
}
