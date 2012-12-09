package org.aimas.craftingquest.state;

import java.io.Serializable;
import java.util.HashMap;

import org.aimas.craftingquest.core.GamePolicy;
import org.aimas.craftingquest.state.objects.ArmourObject;
import org.aimas.craftingquest.state.objects.CraftedObjectType;
import org.aimas.craftingquest.state.objects.ICrafted;
import org.aimas.craftingquest.state.objects.SwordObject;
import org.aimas.craftingquest.state.objects.Tower;
import org.aimas.craftingquest.state.objects.TrapObject;
import org.aimas.craftingquest.state.resources.ResourceType;

/**
 * Shows how to construct an artifact.
 *
 */
@SuppressWarnings("serial")
public class Blueprint implements Serializable {
	/**
	 * (Tech) level of the object described by the blue print
	 */
	int level;
	/**
	 * the weight of the object (just for swords and armours)
	 */
	int weight;
	/**
	 * the type of object described by the blueprint
	 */
	private CraftedObjectType type;
	/**
	 * the resources needed to craft the object
	 */
	
	private HashMap<ResourceType, Integer> requiredResources;
	
	/**
	 * the cost to upgrade to next level
	 */
	int upgradeCost;
	
	public Blueprint(CraftedObjectType type, int level, int weight, HashMap<ResourceType, 
			Integer> requiredResources, int upgradeCost) {
		this.level = level;
		this.weight = weight;
		this.type = type;
		this.requiredResources = requiredResources;
		this.upgradeCost = upgradeCost;
	}

	public HashMap<ResourceType, Integer> getResourcesNeeded()
	{
		return this.requiredResources;
	}
	
	// getters	
	
	public int getLevel() {
		return level;
	}
	
	public int getUpgradeCost() {
		return upgradeCost;
	}

	public int getInitialStrength() {
		if (type != CraftedObjectType.TOWER)
			return 0;
		return GamePolicy.towerBaseEnergy * (100 + GamePolicy.levelIncrease[level]) / 100;
	}

	public int getWeight() {
		return weight;
	}

	public int getAttack() {
		if (type != CraftedObjectType.SWORD)
			return 0;
		return GamePolicy.levelIncrease[level];
	}

	public int getDefence() {
		if (type != CraftedObjectType.ARMOUR)
			return 0;
		return GamePolicy.levelIncrease[level];
	}

	public CraftedObjectType getType() {
		return type;
	}	

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Blueprint)) {
			return false;
		}
		
		final Blueprint other = (Blueprint)obj;
		if (type != other.type || level != other.level || weight != other.weight) {
			return false;
		}
		
		return true;
	}

	@Override
	public int hashCode() {
		return ("bp:" + type + "_" + level + "_" + weight).hashCode();
	}

	@Override
	public String toString() {
		if(type == CraftedObjectType.ARMOUR || type == CraftedObjectType.SWORD)
			return "Blueprint for " + type + ":[level=" + level + ", weight=" + weight +"]" + "[required-resources:" + requiredResources.toString() + "]";
		else
			return "Blueprint for " + type + ":[level=" + level +"]" + "[required-resources:" + requiredResources.toString() + "]";
	}

	/**
	 * Creates an instance of the object described by the blueprint.
	 * @param playerID
	 * @param position
	 * @return
	 */
	public ICrafted craft(int playerID, Point2i position) {
		switch (type) {
		case ARMOUR: return new ArmourObject(this);
		case SWORD: return new SwordObject(this);
		case TOWER: return new Tower(playerID, position, this);
		case TRAP : return new TrapObject(playerID, position, this);
		default: return null;
		}
	}
	
}
 
