package org.aimas.craftingquest.mapeditor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;

import org.aimas.craftingquest.state.Point2i;
import org.aimas.craftingquest.state.resources.ResourceType;

public class MapReader {
	public static int mapWidth;
	public static int mapHeight;
	public static MapCell[][] cells;
	
	public static void readMap(File mapFile) {
		FileInputStream fis = null;
		ObjectInputStream objin = null;
		
		try {
			fis = new FileInputStream(mapFile);
			objin = new ObjectInputStream(fis); 
			
			// read terrain data
			cells = (MapCell[][])objin.readObject();
			
			// set general info
			mapHeight = cells.length;
			mapWidth = cells[0].length;
			
			// read resources map and set resources in cells
			HashMap<Point2i, HashMap<ResourceType, Integer>> cellResourceMap = 
					(HashMap<Point2i, HashMap<ResourceType, Integer>>)objin.readObject();
			
			for (Point2i p : cellResourceMap.keySet()) {
				int x = p.x;
				int y = p.y;
				
				System.out.println("pos: " + p + " -- " + cellResourceMap.get(p));
				
				cells[y][x].cellResources = cellResourceMap.get(p);
			}
			
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally{
			//Close the ObjectInputStream
            try {
                if (objin != null) {
                    objin.close();
                }
                
                if (fis != null) {
                	fis.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
		}
	}
}
