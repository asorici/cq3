package org.aimas.craftingquest.mapeditor;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import org.aimas.craftingquest.state.CellState;
import org.aimas.craftingquest.state.Point2i;
import org.aimas.craftingquest.state.resources.ResourceType;

public class MapWriter {
	private int mapWidth;
	private int mapHeight;
	private MapCell[][] terrain;
	private File file;
	
	public MapWriter(File file, MapCell[][] terrain) {
		this.file = file;
		this.terrain = terrain;
		
		mapHeight = terrain.length;
		mapWidth = terrain[0].length;
		
		if (mapHeight != mapWidth) {
			throw new IllegalArgumentException("Map creation error. Height and width do not match.");
		}
	}
	
	public void writeMap() {
		serializeMap();
		saveToText();
	}
	
	private void serializeMap() {
		FileOutputStream fout = null;
		ObjectOutputStream objout = null;
		
		try {
			fout = new FileOutputStream(file);
			objout = new ObjectOutputStream(fout);
			
			// build resource
			HashMap<Point2i, HashMap<ResourceType, Integer>> cellResourceMap = new HashMap<Point2i, HashMap<ResourceType,Integer>>();
			
			// build CellState structure
			CellState[][] cells = new CellState[mapHeight][mapWidth];
			for (int i = 0; i < mapHeight; i++) {
				for(int j = 0; j < mapWidth; j++) {
					Point2i pos = new Point2i(j, i);
					cells[i][j] = new CellState(terrain[i][j].cellType, pos);
					cellResourceMap.put(pos, terrain[i][j].cellResources);
				}
			}
			
			// write cell data
			objout.writeObject(terrain);
			
			// write resource map
			objout.writeObject(cellResourceMap);
			
			objout.flush();
			objout.close();
			fout.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void saveToText() {
		// build CellState structure
		String serializableFileName = file.getName();
		//System.out.println("FILENAME: " + serializableFileName);
		String textFileName =  serializableFileName + ".txt";
		
		FileWriter fw = null;
		try {
			fw = new FileWriter(textFileName);
			
			// write map size
			fw.write(String.valueOf(mapHeight));
			fw.write('\n');
			fw.write(String.valueOf(mapWidth));
			fw.write('\n');
			
			for (int i = 0; i < mapHeight; i++) {
				for(int j = 0; j < mapWidth; j++) {
					String terrainType = terrain[i][j].getCellType().name();
					fw.write(j + " " + i + " " + terrainType);
					fw.write('\n');
				}
			}
			
			fw.flush();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			if (fw != null) {
				try {
					fw.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		
	}
}
