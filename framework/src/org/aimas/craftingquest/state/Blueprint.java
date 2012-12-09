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
	
	int level;
	int weight;
	private CraftedObjectType type;
	private HashMap<ResourceType, Integer> requiredResources;
	
	public Blueprint(CraftedObjectType type, int level, int weight, HashMap<ResourceType, Integer> requiredResources) {
		this.level = level;
		this.weight = weight;
		this.type = type;
		this.requiredResources = requiredResources;
	}

	public HashMap<ResourceType, Integer> getResourcesNeeded()
	{
		return this.requiredResources;
	}
	
	public int getLevel() {
		return level;
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
		return GamePolicy.attackBaseValue * (100 + GamePolicy.levelIncrease[level]) / 100;
	}

	public int getDefence() {
		if (type != CraftedObjectType.ARMOUR)
			return 0;
		return GamePolicy.defenseBaseValue * (100 + GamePolicy.levelIncrease[level]) / 100;
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
		return "Blueprint for " + type + "[level=" + level + ", weight=" + weight +"]";
	}

	public CraftedObjectType getType() {
		return type;
	}
	
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
 