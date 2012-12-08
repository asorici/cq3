package org.aimas.craftingquest.core;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
//import java.util.ArrayList;
import java.util.HashMap;
//import java.util.List;

import org.aimas.craftingquest.core.energyreplenishmodels.ReplenishType;
import org.aimas.craftingquest.state.CellState;
import org.aimas.craftingquest.state.GameState;
import org.aimas.craftingquest.state.MapState;
import org.aimas.craftingquest.state.Merchant;
import org.aimas.craftingquest.state.Point2i;
import org.aimas.craftingquest.state.ResourceAttributes;
import org.aimas.craftingquest.state.CellState.CellType;
import org.aimas.craftingquest.state.CraftedObject.BasicResourceType;
import org.aimas.craftingquest.state.CraftedObject.ObjectType;
//import org.aimas.craftingquest.state.UnitState.UnitType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class GamePolicy {

	/* server configuration */
	public static String servername = "CraftingQuest";
    public static int serverPort = 1198;
	public static int connectWaitTime = 5000;
    public static int initializationWaitTime = 3000;
	
	/* number of players */
	public static int noPlayers = 2;
	public static int nrPlayerUnits = 3;

	/* durations associated with a player */
	public static long playerActionTime = 1000;
	public static long playerLateTime = 0;
	public static long playerTotalTime;

	/* total duration of all players during a round */
	public static long PLAYERSTotalTime;

	/* total duration of server update time during a round */
	public static long updateTime = 100;

	/* duration of an entire round */
	public static long roundTime;

	/* number of turns */
	public static int lastTurn = 160;
	
	/* game map */
	public static String mapName = "map_v1.cqm";
	public static MapState map;
	
	/* general */
	public static int scanAttributeCount = 5;
	public static Point2i mapsize = new Point2i(80, 80);
	public static int moveBase = 20;
	public static int resourceMoveCost = 1;
	public static int initialTeamCredit = 300;
	public static int unitEnergy = 150;
	public static ReplenishType energyReplenishModel = ReplenishType.FullReplenish;
	public static int baseObjectValue = 100;
	public static int valueIncrement = 20;

	// Grass, Dirt, Sand, Snow, Rock, Swamp, Lake, DeepSea
	// public static double[] movePenalty = {0, 0.2, 0.1, 0.25, -1, 0.25, 0, 0};
	public static HashMap<CellType, Double> movePenalty = new HashMap<CellType, Double>();
	// conventie: orice parametru cu valoare negativa nu se aplica
	// moveCost = moveBase * (1 + movePenalty) + nrRes * resourceMoveCost;

	public static int pickupCost = 5;
	public static int dropCost = 0;
	public static int scanCost = 10;
	public static int digCost = 10;
	public static int buildCost = 0;
	public static int scanRadius = 7;
	public static int sightRadius = 5;

	public static int towerBuildCost = 75;
	public static int towerCutoffRadius = 4;
	public static int towerDrainBase = 100;
	public static int towerEnergy = 250;
	// drain = towerDrainBase / min(abs(tower.pos.x - player.pos.x), abs(..y))
	
	public static int placeTrapCost = 40;
	
//	public static HashMap<CellType, List<UnitType>> terrainMovePossibilities = new HashMap<CellType, List<UnitType>>();
	
	public static int maxResourceSpots = 5;
	
	public static BasicResourceType getResTypeByOrdinal(int ord) {
		BasicResourceType[] vals = BasicResourceType.values();
		for (int i = 0; i < vals.length; i++) {
			if (vals[i].ordinal() == ord) {
				return vals[i];
			}
		}
		
		return null;		// will never actually get here
	}
	
	public static ObjectType getObjectTypeByOrdinal(int ord) {
		ObjectType[] vals = ObjectType.values();
		for (int i = 0; i < vals.length; i++) {
			if (vals[i].ordinal() == ord) {
				return vals[i];
			}
		}
		
		return null;		// will never actually get here
	}
	
	public static void initScenario() {
		
		Document paramDoc = GameUtils.readXMLDocument("GamePolicy.xml");
		MapReader.readMap("mapdata");
		mapsize = new Point2i(MapReader.mapWidth, MapReader.mapHeight);
		mapName = MapReader.mapName;
		lastTurn = MapReader.nrTurns;
		if (MapReader.mapWidth >= 60 && MapReader.mapWidth < 70) {
			lastTurn = 160;
		}
		if (MapReader.mapWidth >= 70 && MapReader.mapWidth < 80) {
			lastTurn = 180;
		}
		if (MapReader.mapWidth >= 80) {
			lastTurn = 200;
		}
		
		map = new MapState();
		map.cells = MapReader.cells;
		map.mapHeight = MapReader.mapHeight;
		map.mapWidth = MapReader.mapWidth;
		
		readParametersFrom(paramDoc);
		
		playerTotalTime = playerActionTime + playerLateTime;
		PLAYERSTotalTime = noPlayers * playerTotalTime;
		roundTime = PLAYERSTotalTime + updateTime;
	}
	
	
	public static void readParametersFrom(Document doc) {
		Element root = (Element)doc.getDocumentElement();
		Element parametersNode = (Element)root.getElementsByTagName("parameters").item(0);
		Element ruleNode = (Element)root.getElementsByTagName("rules").item(0);
		
		readServerParameters(parametersNode);
		readScenarioParameters(parametersNode);
		
		readScenarioRules(ruleNode);
	}
	
	private static void readServerParameters(Element parametersNode) {
		noPlayers = Integer.parseInt(parametersNode.getElementsByTagName("nrPlayers").item(0).getTextContent());
		playerActionTime = Integer.parseInt(parametersNode.getElementsByTagName("playerActionTime").item(0).getTextContent());
		playerLateTime = Integer.parseInt(parametersNode.getElementsByTagName("playerLateTime").item(0).getTextContent());
		updateTime = Integer.parseInt(parametersNode.getElementsByTagName("updateTime").item(0).getTextContent());
		connectWaitTime = Integer.parseInt(parametersNode.getElementsByTagName("connectWaitTime").item(0).getTextContent());
		//lastTurn = Integer.parseInt(parametersNode.getElementsByTagName("nrTurns").item(0).getTextContent());
		nrPlayerUnits = Integer.parseInt(parametersNode.getElementsByTagName("nrPlayerUnits").item(0).getTextContent());
		//mapName = parametersNode.getElementsByTagName("mapName").item(0).getTextContent();
	}
	
	private static void readScenarioParameters(Element parametersNode) {
		scanAttributeCount = Integer.parseInt(parametersNode.getElementsByTagName("scanAttributeCount").item(0).getTextContent());
		unitEnergy = Integer.parseInt(parametersNode.getElementsByTagName("unitEnergy").item(0).getTextContent());
		baseObjectValue = Integer.parseInt(parametersNode.getElementsByTagName("baseObjectValue").item(0).getTextContent());
		valueIncrement = Integer.parseInt(parametersNode.getElementsByTagName("valueIncrement").item(0).getTextContent());
		initialTeamCredit = Integer.parseInt(parametersNode.getElementsByTagName("initialTeamCredit").item(0).getTextContent());
		
		moveBase = Integer.parseInt(parametersNode.getElementsByTagName("moveBaseCost").item(0).getTextContent());
		resourceMoveCost = Integer.parseInt(parametersNode.getElementsByTagName("resourceMoveCost").item(0).getTextContent());
		pickupCost = Integer.parseInt(parametersNode.getElementsByTagName("pickupCost").item(0).getTextContent());
		dropCost = Integer.parseInt(parametersNode.getElementsByTagName("dropCost").item(0).getTextContent());
		scanCost = Integer.parseInt(parametersNode.getElementsByTagName("scanCost").item(0).getTextContent());
		digCost = Integer.parseInt(parametersNode.getElementsByTagName("digCost").item(0).getTextContent());
		buildCost = Integer.parseInt(parametersNode.getElementsByTagName("buildCost").item(0).getTextContent());
		
		towerBuildCost = Integer.parseInt(parametersNode.getElementsByTagName("towerBuildCost").item(0).getTextContent());
		scanRadius = Integer.parseInt(parametersNode.getElementsByTagName("scanRadius").item(0).getTextContent());
		sightRadius = Integer.parseInt(parametersNode.getElementsByTagName("sightRadius").item(0).getTextContent());
		towerCutoffRadius = Integer.parseInt(parametersNode.getElementsByTagName("towerCutoffRadius").item(0).getTextContent());
		towerDrainBase = Integer.parseInt(parametersNode.getElementsByTagName("towerDrainBase").item(0).getTextContent());
		towerEnergy = Integer.parseInt(parametersNode.getElementsByTagName("towerEnergy").item(0).getTextContent());
		String replenishType = parametersNode.getElementsByTagName("energyReplenishModel").item(0).getTextContent();
		if (replenishType.equals("FullReplenish"))
			energyReplenishModel = ReplenishType.FullReplenish;
		else if (replenishType.equals("ExponentialReplenish"))
			energyReplenishModel = ReplenishType.ExponentialReplenish;
		else
			energyReplenishModel = null;
		placeTrapCost = Integer.parseInt(parametersNode.getElementsByTagName("placeTrapCost").item(0).getTextContent());
	}
	
	private static void readScenarioRules(Element ruleNode) {
		// retrieve movePenalties
		Element movePenalties = (Element)ruleNode.getElementsByTagName("movePenalties").item(0);
		NodeList penaltyList = movePenalties.getElementsByTagName("penalty");
		for (int i = 0; i < penaltyList.getLength(); i++) {
			Element penalty = (Element)penaltyList.item(i);
			String terrain = penalty.getElementsByTagName("terrain").item(0).getTextContent();
			double value = Double.parseDouble(penalty.getElementsByTagName("value").item(0).getTextContent());
			
			movePenalty.put(CellType.valueOf(terrain), value);
		}
	}

	public static void saveMapResources(GameState state) {
		MapResourceWriter.saveMapResources(mapName, state); 
	}

}

class MapResourceWriter {
	public static void saveMapResources(String mapName, GameState game) {
		String mapFile = "maps/" + mapName + ".cqres";
		
		try {
			FileOutputStream fout = new FileOutputStream(mapFile);
			ObjectOutputStream objout = new ObjectOutputStream (fout);
			
			HashMap<Point2i, HashMap<BasicResourceType, Integer>> cellResources = new HashMap<Point2i, HashMap<BasicResourceType,Integer>>();
			HashMap<Point2i, HashMap<BasicResourceType, ResourceAttributes>> cellAttributes = new HashMap<Point2i, HashMap<BasicResourceType,ResourceAttributes>>();
			
			for (int i = 0; i < game.map.mapHeight; i++) {
				for (int j = 0; j < game.map.mapWidth; j++) {
					cellResources.put(game.map.cells[i][j].pos, new HashMap<BasicResourceType, Integer>(game.map.cells[i][j].resources));
					cellAttributes.put(game.map.cells[i][j].pos, new HashMap<BasicResourceType, ResourceAttributes>(game.map.cells[i][j].scanAttributes));
				}
			}
			
			// write game data
			objout.writeObject(game);
			
			// write resource and scan data
			objout.writeObject(cellResources);
			objout.writeObject(cellAttributes);
			
			objout.flush();
			objout.close();
			fout.close();
		}catch(FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

class MapReader {
	public static int mapWidth;
	public static int mapHeight;
	public static int nrTurns;
	public static String mapName = "map_v1.cqm"; 
	public static CellState[][] cells;
	
	public static void readMap(String mapDatafilename) {
		//Element root = (Element)paramDoc.getDocumentElement();
		//Element parametersNode = (Element)root.getElementsByTagName("parameters").item(0);
		//mapName = parametersNode.getElementsByTagName("mapName").item(0).getTextContent();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(mapDatafilename));
			mapName = reader.readLine().trim();
			System.out.println("Read map name from \"mapdata\" file.");
			reader.close();
		} catch(IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		String mapFile = "maps/" + mapName;
		
		try {
			FileInputStream fis = new FileInputStream(mapFile);
			DataInputStream dis = new DataInputStream(fis);
			
			// read general info
			mapHeight = dis.readInt();
			mapWidth = dis.readInt();
			nrTurns = dis.readInt();
			
			// read terrain info
			cells = new CellState[mapHeight][mapWidth];
			for (int i = 0; i < mapHeight; i++) {
				for (int j = 0; j < mapWidth; j++) {
					int cellTypeOrdinal = dis.readByte();
					CellType ct = CellState.getCellTypeByOrdinal(cellTypeOrdinal);
					cells[i][j] = new CellState(ct, new Point2i(j, i));
				}
			}
			
			// read merchant positions
			int ct = dis.readByte();	// merchant count
			for (int i = 0; i < ct; i++) {
				int x = dis.readByte();
				int y = dis.readByte();
				
				cells[y][x].strategicResource = new Merchant(new Point2i(x, y));
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
