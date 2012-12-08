package org.aimas.craftingquest.state;

import java.io.Serializable;

import org.aimas.craftingquest.state.CraftedObject.BasicResourceType;

/**
 * <p>Describes the resource attributes of the given BasicResourceType.<p>
 * <p>There are 5 types of soil attributes marked 0 to 4. 
 * Each attribute can have a value between 0 and 4. Yet, a resource is very likely 
 * determined only by a subset of the attributes. 
 * <p>Using a combination of scan, dig operations and a clever learning algorithm, players 
 * will be able to determine the correspondence. </p>
 * 
 */
@SuppressWarnings("serial")
public class ResourceAttributes implements Serializable {
	/**
	 * the resource type for which the attributes are given
	 */
	public BasicResourceType resourceType;
	
	/**
	 * the values for each type of attribute. The index will denote the type of attribute (0 to 4)
	 */
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
