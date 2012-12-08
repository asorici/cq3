package org.aimas.craftingquest.core;

import java.io.InputStream;
import java.io.OutputStream;

import org.aimas.craftingquest.state.CellState;
import org.aimas.craftingquest.state.MapState;
import org.aimas.craftingquest.state.CellState.CellType;

/**
 * 
 * @author Razvan
 */
public class TerrainGenerator {

	public MapState generate() {
		return null;
	}

	public MapState read(InputStream stream) {
		return null;
	}

	public void save(MapState terrain, OutputStream stream) {
	}

	public MapState load(InputStream stream) {
		return null;
	}

	// 00 01 02 03 04 05 (x,y)
	// 10 11 12 13 14 15
	// 20 21 22 23 24 25
	public MapState hardcoded1() {
		MapState t = new MapState();
		int sizex = GamePolicy.mapsize.x;
		int sizey = GamePolicy.mapsize.y;
		t.mapWidth = sizex;
		t.mapHeight = sizey;
		
		t.cells = new CellState[sizey][sizex];

		for (int i = 0; i < sizey; i++) {
			for (int j = 0; j < sizex; j++) {
				CellState c = new CellState();
				c.type = CellType.Grass;
				t.cells[i][j] = c;
			}
		}

		return t;
	}
}
