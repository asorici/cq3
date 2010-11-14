package org.aimas.craftingquest.state;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.aimas.craftingquest.core.GamePolicy.BasicResourceType;
import org.aimas.craftingquest.core.GamePolicy.ObjectType;

/**
 * 
 * @author Razvan
 */
public class CellState implements Serializable {
	
	public static enum CellType {
		Grass, Dirt, Sand, Snow, Rock, Swamp, Water, DeepWater
	};
	
	public CellType type;
	public Point2i pos;
	
	public HashMap<BasicResourceType, Integer> visibleResources = new HashMap<BasicResourceType, Integer>();
	public HashMap<CraftedObject, Integer> craftedObjects = new HashMap<CraftedObject, Integer>();
	public List<BasicUnit> cellUnits = new ArrayList<BasicUnit>();
	public StrategicResource strategicResource;
	
	//List<BasicRes> resources;
	public transient HashMap<BasicResourceType, Integer> resources = new HashMap<BasicResourceType, Integer>();
	public transient HashMap<BasicResourceType, ResourceAttributes> scanAttributes = new HashMap<BasicResourceType, ResourceAttributes>();
	
	public CellState() {
	}
	
	public CellState(CellType type, Point2i pos) {
		this.type = type;
		this.pos = pos;
	}
	
	public static CellType getCellTypeByOrdinal(int ord) {
		for (CellType ct : CellType.values()) {
			if (ct.ordinal() == ord) {
				return ct;
			}
		}
		
		throw new IllegalArgumentException("No existing CellType for given ordinal.");
	}
}
