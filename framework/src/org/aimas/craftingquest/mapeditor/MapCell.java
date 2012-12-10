package org.aimas.craftingquest.mapeditor;

import java.io.Serializable;
import java.util.HashMap;

import org.aimas.craftingquest.state.CellState.CellType;
import org.aimas.craftingquest.state.objects.CraftedObjectType;
import org.aimas.craftingquest.state.resources.ResourceType;

public class MapCell implements Serializable {
	public CellType cellType;
	public HashMap<ResourceType, Integer> cellResources;
	
	public MapCell() {
		this(CellType.Grass, new HashMap<ResourceType, Integer>());
	}
	
	public MapCell(CellType type) {
		this(type, new HashMap<ResourceType, Integer>());
	}
	
	public MapCell(CellType type, HashMap<ResourceType, Integer> resources) {
		cellType = type;
		cellResources = resources;
	}
	
	public CellType getCellType() {
		return cellType;
	}

	public void setCellType(CellType cellType) {
		this.cellType = cellType;
	}

	public HashMap<ResourceType, Integer> getCellResources() {
		return cellResources;
	}

	public void setCellResources(HashMap<ResourceType, Integer> cellResources) {
		this.cellResources = cellResources;
	}
	
	
}
