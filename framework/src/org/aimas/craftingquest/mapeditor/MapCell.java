package org.aimas.craftingquest.mapeditor;

import org.aimas.craftingquest.state.CellState.CellType;
import org.aimas.craftingquest.state.StrategicResource.StrategicResourceType;

public class MapCell {
	public CellType cellType;
	public StrategicResourceType strategicResType = null;
	
	public MapCell(){
	}
	
	public MapCell(CellType type, StrategicResourceType strategic) {
		cellType = type;
		strategicResType = strategic;
	}
	
}
