package org.aimas.craftingquest.state;

import java.io.Serializable;
import java.util.Arrays;

public class MapState implements Serializable {

	public Point2i minAnchor; // upper left min point of array on the real map
	public CellState[][] cells;

	@Override
	public String toString() {
		String info = "MapState\n ";
		for (int i = 0; i < cells.length; i++) {
			for (int j = 0; j < cells[0].length; j++) {
				info += cells[i][j].toString() + " ";
			}
			info += "\n";
		}
		return info;
	}

}
