package org.aimas.craftingquest.core;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.aimas.craftingquest.core.energyreplenishmodels.ReplenishType;
import org.aimas.craftingquest.state.Blueprint;
import org.aimas.craftingquest.state.CellState;
import org.aimas.craftingquest.state.CellState.CellType;
import org.aimas.craftingquest.state.GameState;
import org.aimas.craftingquest.state.MapState;
import org.aimas.craftingquest.state.Point2i;
import org.aimas.craftingquest.state.objects.CraftedObjectType;
import org.aimas.craftingquest.state.resources.ResourceType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
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
	public static Point2i mapsize = new Point2i(80, 80);
	public static int moveBase = 20;
	public static int resourceMoveCost = 1;
	public static int initialTeamGold = 300;
	public static int initialUnitMaxLife = 150;
	public static ReplenishType energyReplenishModel = ReplenishType.FullReplenish;
	public static int movePenaltyWeight = 50;
	
	/* action costs */
	public static int pickupCost = 5;
	public static int dropCost = 0;
	public static int scanCost = 10;
	public static int digCost = 10;
	public static int buildCost = 0;
	public static int scanRadius = 7;
	public static int sightRadius = 5;
	public static int placeTowerCost = 75;
	public static int placeTrapCost = 40;

	/* tower related */
	public static int towerBaseRadius = 4;
	public static int towerBaseDrain = 100;
	public static int towerBaseEnergy = 100;
	
	public static int maxResourceSpots = 5;
	public static int leatherWeight = 1;
	public static int stoneWeight = 2;
	public static int woodWeight = 1;
	public static int bronzeWeight = 3;
	public static int titaniumWeight = 9;
	public static int ironWeight = 5;
	
	/* leveling */
	public static int maxLevels = 3;
	public static int[] levelIncrease;
	
	/* blueprints */
	public static List<Blueprint> blueprints;
	
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
		Element blueprintsNode = (Element)root.getElementsByTagName("blueprints").item(0);
		//Element ruleNode = (Element)root.getElementsByTagName("rules").item(0);
		
		readServerParameters(parametersNode);
		readScenarioParameters(parametersNode);
		readScenarioBlueprints(blueprintsNode);
		
		//readScenarioRules(ruleNode);
	}
	
	private static void readServerParameters(Element parametersNode) {
		noPlayers = Integer.parseInt(parametersNode.getElementsByTagName("nrPlayers").item(0).getTextContent());
		playerActionTime = Integer.parseInt(parametersNode.getElementsByTagName("playerActionTime").item(0).getTextContent());
		playerLateTime = Integer.parseInt(parametersNode.getElementsByTagName("playerLateTime").item(0).getTextContent());
		updateTime = Integer.parseInt(parametersNode.getElementsByTagName("updateTime").item(0).getTextContent());
		connectWaitTime = Integer.parseInt(parametersNode.getElementsByTagName("connectWaitTime").item(0).getTextContent());
		lastTurn = Integer.parseInt(parametersNode.getElementsByTagName("nrTurns").item(0).getTextContent());
		nrPlayerUnits = Integer.parseInt(parametersNode.getElementsByTagName("nrPlayerUnits").item(0).getTextContent());
		movePenaltyWeight = Integer.parseInt(parametersNode.getElementsByTagName("movePenaltyWeight").item(0).getTextContent());
		mapName = parametersNode.getElementsByTagName("mapName").item(0).getTextContent();
	}
	
	private static void readScenarioParameters(Element parametersNode) {
		initialUnitMaxLife = Integer.parseInt(parametersNode.getElementsByTagName("unitEnergy").item(0).getTextContent());
		initialTeamGold = Integer.parseInt(parametersNode.getElementsByTagName("initialTeamGold").item(0).getTextContent());
		
		moveBase = Integer.parseInt(parametersNode.getElementsByTagName("moveBaseCost").item(0).getTextContent());
		resourceMoveCost = Integer.parseInt(parametersNode.getElementsByTagName("resourceMoveCost").item(0).getTextContent());
		pickupCost = Integer.parseInt(parametersNode.getElementsByTagName("pickupCost").item(0).getTextContent());
		dropCost = Integer.parseInt(parametersNode.getElementsByTagName("dropCost").item(0).getTextContent());
		digCost = Integer.parseInt(parametersNode.getElementsByTagName("digCost").item(0).getTextContent());
		buildCost = Integer.parseInt(parametersNode.getElementsByTagName("buildCost").item(0).getTextContent());
		
		placeTowerCost = Integer.parseInt(parametersNode.getElementsByTagName("placeTowerCost").item(0).getTextContent());
		sightRadius = Integer.parseInt(parametersNode.getElementsByTagName("sightRadius").item(0).getTextContent());
		towerBaseRadius = Integer.parseInt(parametersNode.getElementsByTagName("towerBaseRadius").item(0).getTextContent());
		towerBaseDrain = Integer.parseInt(parametersNode.getElementsByTagName("towerBaseDrain").item(0).getTextContent());
		towerBaseEnergy = Integer.parseInt(parametersNode.getElementsByTagName("towerBaseEnergy").item(0).getTextContent());
		String replenishType = parametersNode.getElementsByTagName("energyReplenishModel").item(0).getTextContent();
		if (replenishType.equals("FullReplenish"))
			energyReplenishModel = ReplenishType.FullReplenish;
		else if (replenishType.equals("ExponentialReplenish"))
			energyReplenishModel = ReplenishType.ExponentialReplenish;
		else
			energyReplenishModel = null;
		placeTrapCost = Integer.parseInt(parametersNode.getElementsByTagName("placeTrapCost").item(0).getTextContent());
	}
	
	private static void readScenarioBlueprints(Element blueprintsNode) {
		blueprints = new ArrayList<Blueprint>();
		
		System.out.println("Reading blueprints");
		NodeList blueprintNodeList = blueprintsNode.getElementsByTagName("blueprint");
		System.out.println(blueprintNodeList.getLength() + " blueprints found in GamePolicy.xml");
		for (int i = 0; i < blueprintNodeList.getLength(); i++) {
			Node blueprintNode = blueprintNodeList.item(i);
			
			// Get object type
			CraftedObjectType craftedObjectType = CraftedObjectType.valueOf(blueprintNode.getAttributes().getNamedItem("type").getTextContent().toUpperCase());
			
			// Get object level
			int level = Integer.parseInt(blueprintNode.getAttributes().getNamedItem("level").getTextContent());
			
			// Get object upgradeCost (if available)
			int upgradeCost = 0;
			Node upgradeCostNode = blueprintNode.getAttributes().getNamedItem("upgradeCost");
			if(upgradeCostNode != null)
				Integer.parseInt(blueprintNode.getAttributes().getNamedItem("upgradeCost").getTextContent());
			
			// Get object weight (if available)
			int weight = 0;
			Node weightNode = blueprintNode.getAttributes().getNamedItem("weight");
			if(weightNode != null)
				weight = Integer.parseInt(weightNode.getTextContent());
			
			// Get the object's required resources
			HashMap<ResourceType, Integer> requiredResources = new HashMap<ResourceType, Integer>();
			NodeList resourceNodeList = blueprintNode.getChildNodes();
			for (int j = 0; j < resourceNodeList.getLength(); j++) {
				ResourceType resourceType = ResourceType.valueOf(resourceNodeList.item(j).getAttributes().getNamedItem("type").getTextContent().toUpperCase());
				int quantity = Integer.parseInt(resourceNodeList.item(j).getAttributes().getNamedItem("quantity").getTextContent());
				requiredResources.put(resourceType, quantity);
			}
			
			Blueprint readBlueprint = new Blueprint(craftedObjectType, level, weight, requiredResources, upgradeCost);
			blueprints.add(readBlueprint);
			System.out.println("Read blueprint: " + readBlueprint.toString());
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
			
			HashMap<Point2i, HashMap<ResourceType, Integer>> cellResources = new HashMap<Point2i, HashMap<ResourceType,Integer>>();
			
			for (int i = 0; i < game.map.mapHeight; i++) {
				for (int j = 0; j < game.map.mapWidth; j++) {
					cellResources.put(game.map.cells[i][j].pos, new HashMap<ResourceType, Integer>(game.map.cells[i][j].resources));
				}
			}
			
			// write game data
			objout.writeObject(game);
			
			// write resource and scan data
			objout.writeObject(cellResources);
			
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
			
			dis.close();
			fis.close();
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
