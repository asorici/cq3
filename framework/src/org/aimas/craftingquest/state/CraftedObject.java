package org.aimas.craftingquest.state;

import java.io.Serializable;
//import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//import java.util.Set;

/**
 * Description of a crafted object - value and means of construction. There are
 * two types of crafted objects:
 * <ul>
 * <li>simple objects - are constructed only from basic resources</li>
 * <li>complex objects - are constructed only from other objects</li>
 * </ul>
 * 
 */
@SuppressWarnings("serial")
public class CraftedObject implements Serializable {
	public enum BasicResourceType implements Serializable {
		WOOD, STONE, LEATHER, BRONZE, IRON, TITANIUM, GOLD
	}

	public enum ObjectType implements Serializable {
		SWORD, SHIELD, TOWER, TRAP
	}

	/**
	 * the object type
	 */
	private ObjectType type;

	/**
	 * the object value
	 */
	private int value;

	/**
	 * the object weight
	 */
	private int weight;
	
	/**
	 * If not null, this field gives a list of alternative ways of constructing
	 * a simple object. <br/>
	 * Each alternative is a mapping between <code>BasicResourceType</code>s and
	 * their required quantities.
	 */
	private List<HashMap<BasicResourceType, Integer>> requiredResources;

	public CraftedObject() {
	}

	public CraftedObject(ObjectType type, int value,
			List<HashMap<CraftedObject, Integer>> requiredObjects,
			List<HashMap<BasicResourceType, Integer>> requiredResources) {
		this.type = type;
		this.value = value;
		this.requiredResources = requiredResources;
	}

	public List<HashMap<BasicResourceType, Integer>> getRequiredResources() {
		return requiredResources;
	}

	public ObjectType getType() {
		return type;
	}

	public int getValue() {
		return value;
	}
	
	public int getWeight() {
		return weight;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof CraftedObject)) {
			return false;
		}

		final CraftedObject other = (CraftedObject) obj;

		if (this.type != other.type) {
			return false;
		}

		if (this.value != other.value) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		return type.ordinal();
	}

	@Override
	public String toString() {
		String info = "";

		info += "Obj: " + type.name() + "\n";
		info += "    value: " + value + "\n";

		if (requiredResources != null && !requiredResources.isEmpty()) {
			info += "    prerequisites \n";
			for (HashMap<BasicResourceType, Integer> alternative : requiredResources) {
				info += "        ";
				for (BasicResourceType br : alternative.keySet()) {
					info += br.name() + ":" + alternative.get(br) + " ";
				}
				info += "\n";
			}
		}

		return info;
	}
}
