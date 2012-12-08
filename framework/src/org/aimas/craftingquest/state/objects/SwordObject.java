package org.aimas.craftingquest.state.objects;

import java.io.Serializable;

import org.aimas.craftingquest.state.Blueprint;
import org.aimas.craftingquest.state.ICarriable;

@SuppressWarnings("serial")
public class SwordObject implements IEquippable, ICrafted, ICarriable, Serializable {
	
	private static final CraftedObjectType craftedObjectType = CraftedObjectType.SWORD;
	Blueprint blueprint;
	
	public SwordObject(Blueprint blueprint) {
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
		return blueprint.getAttack();
	}

	@Override
	public int getDefence() {
		return 0;	
	}
	
	@Override
	public String toString() {
		return "Sword [level=" + blueprint.getLevel() + ", attack=" + blueprint.getAttack() + "]"; 
	}
	
	@Override
	public int hashCode() {
		return blueprint.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (! (obj instanceof SwordObject)) return false;
		SwordObject other = (SwordObject) obj;
		return this.blueprint.equals(other.blueprint);
	}
	
}
