package org.aimas.craftingquest.state;

import java.io.Serializable;
import java.util.HashMap;

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
	 * (Tech) level of the object described by the blueprint
	 */
	int level;
	
	/**
	 * (Tech) the maximum level available for the object described by the blueprint
	 */
	int maxLevels;
	
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
	
	
	/*
	 *  ============================ tower specific fields ============================
	 */
	int towerInitialStrength;
	int towerRange;
	int towerDrain;
	
	
	/*
	 *  ============================ sword specific fields ============================
	 */
	int swordAttack;
	
	/*
	 *  ============================ armour specific fields ============================
	 */
	int armourDefense;
	
	
	public Blueprint(CraftedObjectType type, int level, int maxLevels, int weight, HashMap<ResourceType, 
			Integer> requiredResources, int upgradeCost) {
		this.level = level;
		this.maxLevels = maxLevels;
		this.weight = weight;
		this.type = type;
		this.requiredResources = requiredResources;
		this.upgradeCost = upgradeCost;
	}
	
	/**
	 * Auxiliary method used on the server side to set the specific scenario values 
	 * for each type of object crafted object. The ones not specific to the object described by this
	 * blueprint will be 0.
	 * 
	 * @param towerInitialStrength
	 * @param towerRange
	 * @param towerDrain
	 * @param swordAttack
	 * @param armourDefense
	 */
	public void setSpecificValues(int towerInitialStrength, int towerRange, 
			int towerDrain, int swordAttack, int armourDefense) {
		this.towerInitialStrength = towerInitialStrength;
		this.towerRange = towerRange;
		this.towerDrain = towerDrain;
		this.swordAttack = swordAttack;
		this.armourDefense = armourDefense;
	}
	
	
	public HashMap<ResourceType, Integer> getResourcesNeeded() {
		return this.requiredResources;
	}
	
	// getters	
	
	public int getLevel() {
		return level;
	}
	
	public int getMaxLevel() {
		return maxLevels;
		//return GamePolicy.maxLevels;
	}
	
	public int getUpgradeCost() {
		return upgradeCost;
	}

	
	/*
	 *  ============================ tower specific methods ============================
	 */
	public int getInitialStrength() {
		if (type != CraftedObjectType.TOWER)
			return 0;
		
		return towerInitialStrength;
		//return GamePolicy.towerBaseEnergy * (100 + GamePolicy.levelIncrease[level]) / 100;
	}
	
	
	public int getRange() {
		if (type != CraftedObjectType.TOWER) 
			return 0;
		
		return towerRange;
		//return GamePolicy.towerBaseRadius * (100 + GamePolicy.levelIncrease[level]) / 100;
	}
	
	
	public int getDrain() {
		if (type != CraftedObjectType.TOWER) 
			return 0;
		
		return towerDrain;
		//return GamePolicy.towerBaseDrain * (100 + GamePolicy.levelIncrease[level]) / 100;
	}
	
	
	public int getWeight() {
		return weight;
	}
	
	/*
	 * ============================ Sword specific method ============================
	 */
	public int getAttack() {
		if (type != CraftedObjectType.SWORD)
			return 0;
		
		return swordAttack;
		//return GamePolicy.levelIncrease[level];
	}
	
	/*
	 * ============================ Armour specific method ============================
	 */
	public int getDefence() {
		if (type != CraftedObjectType.ARMOUR)
			return 0;
		
		return armourDefense;
		//return GamePolicy.levelIncrease[level];
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
	 * @return the object crafted according to the descriptions of this blueprint
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
 
