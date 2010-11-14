package org.aimas.craftingquest.mapeditor;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.aimas.craftingquest.state.Point2i;

public class MapWriter {
	private int mapWidth;
	private int mapHeight;
	private int nrTurns;
	private MapCell[][] terrain;
	private File file;
	
	private ArrayList<Point2i> merchantPositions = new ArrayList<Point2i>();
	
	public MapWriter(File file, MapCell[][] terrain) {
		this.file = file;
		this.terrain = terrain;
		
		mapHeight = terrain.length;
		mapWidth = terrain[0].length;
		
		if (mapHeight != mapWidth) {
			throw new IllegalArgumentException("Map creation error. Height and width do not match.");
		}
		
		nrTurns = 160;
		if (mapWidth > 60 && mapWidth <= 80) {
			nrTurns = 240;
		}
		
		if (mapWidth > 80) {
			nrTurns = 320;
		}
		
		for (int i = 0; i < mapHeight; i++) {
			for (int j = 0; j < mapWidth; j++) {
				if (terrain[i][j].strategicResType != null) {
					merchantPositions.add(new Point2i(j, i));
				}
			}
		}
	}
	
	public void writeMap() {
		FileOutputStream fos = null;
		DataOutputStream dos = null;
		
		try {
			fos = new FileOutputStream(file);
			dos = new DataOutputStream(fos);
			
			writeGeneralInfo(dos);
			writeTerrain(dos);
			writeMerchantPositions(dos);
			
			dos.close();
			fos.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void writeGeneralInfo(DataOutputStream dos) throws IOException {
		/* write map width and height */
		dos.writeInt(mapHeight);
		dos.writeInt(mapWidth);
		
		/* write number of turns */
		dos.writeInt(nrTurns);
		
	}
	
	private void writeTerrain(DataOutputStream dos) throws IOException {
		/* write map cell types */
		
		for (int i = 0; i < mapHeight; i++) {
			for(int j = 0; j < mapWidth; j++) {
				dos.writeByte(terrain[i][j].cellType.ordinal());
			}
		}
	}
	
	private void writeMerchantPositions(DataOutputStream dos) throws IOException {
		dos.writeByte(merchantPositions.size());
		
		for (Point2i pos : merchantPositions) {
			dos.writeByte(pos.x);
			dos.writeByte(pos.y);
		}
	}
}
