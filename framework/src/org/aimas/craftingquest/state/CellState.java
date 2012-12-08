package org.aimas.craftingquest.state;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.aimas.craftingquest.state.CraftedObject.BasicResourceType;

/**
 * Maintains the state of a map cell. It keeps data about the cell's type, position, <br/>
 * the lists of visible resources and objects, <br/>
 * the list of soil resources and of soil scan attributes, <br/>
 * the list of units and the strategicResource (if any) present in the cell.
 */
@SuppressWarnings("serial")
public class CellState implements Serializable {
	
	public static enum CellType implements Serializable {
		Grass, Dirt, Sand, Snow, Rock, Swamp, Water, DeepWater
	};
	
	/**
	 * terrain type
	 */
	public CellType type;
	
	/**
	 * position on the map
	 */
	public Point2i pos;
	
	/**
	 * quantities of each visible resource
	 */
	public HashMap<BasicResourceType, Integer> visibleResources = new HashMap<BasicResourceType, Integer>();
	
	/**
	 * quantities of each crafted object (must have been previously dropped by an other unit)
	 */
	public HashMap<CraftedObject, Integer> craftedObjects = new HashMap<CraftedObject, Integer>();
	
	/**
	 * the list of units present in the cell - as seen by an opponent's view
	 */
	public List<BasicUnit> cellUnits = new ArrayList<BasicUnit>();
	
	/**
	 * the strategic resource (may be null) contained in this cell.
	 */
	public StrategicResource strategicResource;
	
	/**
	 * the quantities of buried cell resources - these will not be available to the client side as they are 
	 * declared <code>transient</code> and thus will not be serialized.
	 */
	public transient HashMap<BasicResourceType, Integer> resources = new HashMap<BasicResourceType, Integer>();
	
	/**
	 * the scan attributes for each type of resource - like in the case of buried resources, these will not be 
	 * available on the client side
	 */
	public transient HashMap<BasicResourceType, ResourceAttributes> scanAttributes = new HashMap<BasicResourceType, ResourceAttributes>();
	
	public CellState() {
	}
	
	public CellState(CellType type, Point2i pos) {
		this.type = type;
		this.pos = pos;
	}
	
	/**
	 * return the cell type by its ordinal position in the Enumeration
	 * @param ord
	 * @return the cell type by its ordinal
	 */
	public static CellType getCellTypeByOrdinal(int ord) {
		for (CellType ct : CellType.values()) {
			if (ct.ordinal() == ord) {
				return ct;
			}
		}
		
		throw new IllegalArgumentException("No existing CellType for given ordinal.");
	}
}
