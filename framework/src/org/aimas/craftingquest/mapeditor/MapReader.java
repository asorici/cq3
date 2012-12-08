package org.aimas.craftingquest.mapeditor;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.aimas.craftingquest.state.CellState;
import org.aimas.craftingquest.state.CellState.CellType;
import org.aimas.craftingquest.state.StrategicResource.StrategicResourceType;

public class MapReader {
	public static int mapWidth;
	public static int mapHeight;
	public static int nrTurns;
	public static MapCell[][] cells;
	
	public static void readMap(File mapFile) {
		try {
			FileInputStream fis = new FileInputStream(mapFile);
			DataInputStream dis = new DataInputStream(fis);
			
			// read general info
			mapHeight = dis.readInt();
			mapWidth = dis.readInt();
			nrTurns = dis.readInt();
			
			// read terrain info
			cells = new MapCell[mapHeight][mapWidth];
			for (int i = 0; i < mapHeight; i++) {
				for (int j = 0; j < mapWidth; j++) {
					int cellTypeOrdinal = dis.readByte();
					CellType ct = CellState.getCellTypeByOrdinal(cellTypeOrdinal);
					cells[i][j] = new MapCell(ct, null);
				}
			}
			
			// read merchant positions
			int ct = dis.readByte();	// merchant count
			for (int i = 0; i < ct; i++) {
				int x = dis.readByte();
				int y = dis.readByte();
				
				cells[y][x].strategicResType = StrategicResourceType.Merchant;
			}
			
			dis.close();
			fis.close();
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
