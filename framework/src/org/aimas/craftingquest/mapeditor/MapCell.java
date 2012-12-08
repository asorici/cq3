package org.aimas.craftingquest.mapeditor;

import org.aimas.craftingquest.state.CellState.CellType;
import org.aimas.craftingquest.state.objects.CraftedObjectType;
import org.aimas.craftingquest.state.objects.IStrategic;

public class MapCell {
	public CellType cellType;
	public CraftedObjectType strategicResType = null;
	
	public MapCell(){
	}
	
	public MapCell(CellType type, CraftedObjectType strategic) {
		cellType = type;
		strategicResType = strategic;
	}
	
}
