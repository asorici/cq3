package org.aimas.craftingquest.state.objects;

import java.io.Serializable;

import org.aimas.craftingquest.state.Blueprint;
import org.aimas.craftingquest.state.ICarriable;

@SuppressWarnings("serial")
public class ArmourObject implements IEquippable, ICrafted, ICarriable, Serializable {
	
	private static final CraftedObjectType craftedObjectType = CraftedObjectType.ARMOUR;
	Blueprint blueprint;
	
	public ArmourObject(Blueprint blueprint) {
		this.blueprint = blueprint;
	}

	@Override
	public int getWeight() {
		return blueprint.getWeight();
	}

	@Override
	public int getLevel() {
		return blueprint.getLevel();
	}

	@Override
	public CraftedObjectType getType() {
		return craftedObjectType;
	}

	@Override
	public Blueprint getBlueprint() {
		return blueprint;
	}

	@Override
	public int getAttack() {
		return 0;
	}

	@Override
	public int getDefence() {
		return blueprint.getDefence();	
	}
	
	@Override
	public String toString() {
		return "Armour [level=" + blueprint.getLevel() + ", defence=" + blueprint.getDefence() + "]"; 
	}
	
	@Override
	public int hashCode() {
		return blueprint.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (! (obj instanceof ArmourObject)) return false;
		ArmourObject other = (ArmourObject) obj;
		return this.blueprint.equals(other.blueprint);
	}
	
}
