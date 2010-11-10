package org.aimas.craftingquest.state;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.aimas.craftingquest.core.GamePolicy.BasicResourceType;

public class CraftedObject implements Serializable {
	private int type;
	private int value;
	private List<HashMap<CraftedObject, Integer>> requiredObjects;
	private List<HashMap<BasicResourceType, Integer>> requiredResources;
	
	public CraftedObject() {
	}
	
	public CraftedObject(int type, int value, List<HashMap<CraftedObject, Integer>> requiredObjects, List<HashMap<BasicResourceType, Integer>> requiredResources) {
		this.type = type;
		this.value = value;
		this.requiredObjects = requiredObjects;
		this.requiredResources = requiredResources;
	}

	public List<HashMap<CraftedObject, Integer>> getRequiredObjects() {
		return requiredObjects;
	}

	public List<HashMap<BasicResourceType, Integer>> getRequiredResources() {
		return requiredResources;
	}
		
	public List<List<CraftedObject>> getRequiredObjectsList() {
		List<List<CraftedObject>> list = new ArrayList<List<CraftedObject>>();
		
		for (HashMap<CraftedObject, Integer> alternative : requiredObjects) {
			Set<CraftedObject> keys = alternative.keySet();
			ArrayList<CraftedObject> keysArray = new ArrayList<CraftedObject>(keys);
			
			list.add(keysArray);
		}
		
		return list;
	}
	
	public int getType() {
		return type;
	}

	public int getValue() {
		return value;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof CraftedObject)) {
			return false;
		}
		
		final CraftedObject other = (CraftedObject)obj;
		
		if (this.type != other.type) {
			return false;
		}
		
		if (this.value != other.value) {
			return false;
		}
		
		return true;
	}
}
