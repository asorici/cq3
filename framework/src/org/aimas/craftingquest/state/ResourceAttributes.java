package org.aimas.craftingquest.state;

import org.aimas.craftingquest.core.GamePolicy.BasicResourceType;

public class ResourceAttributes {
	public BasicResourceType resourceType;
	public int[] attributeValues;		// one value for each of the GamePolicy.scanAttributeCount attributes 
										// index will denote the type of the attribute
	public ResourceAttributes() {
	}
	
	public ResourceAttributes(BasicResourceType resourceType, int[] attributeValues) {
		this.resourceType = resourceType;
		this.attributeValues = attributeValues;
	}
	
	public BasicResourceType getResourceType() {
		return resourceType;
	}
	
	public int[] getAttributeValues() {
		return attributeValues;
	}
	
}
