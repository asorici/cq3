package org.aimas.craftingquest.core;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.aimas.craftingquest.state.CellState;
import org.aimas.craftingquest.state.MapState;
import org.aimas.craftingquest.state.Merchant;
import org.aimas.craftingquest.state.Point2i;
import org.aimas.craftingquest.state.CellState.CellType;
import org.aimas.craftingquest.state.CraftedObject.BasicResourceType;
import org.aimas.craftingquest.state.CraftedObject.ObjectType;
import org.aimas.craftingquest.state.UnitState.UnitType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


/**
 * 
 * @author Razvan
 */
public class GamePolicy {

	/* server configuration */
	public static String servername = "CraftingQuest";
    public static int serverPort = 1198;
	public static int connectWaitTime = 8000;
    
	/* number of players */
	public static int noPlayers = 2;
	public static int nrPlayerUnits = 3;

	/* durations associated with a player */
	public static long playerActionTime = 1000;
	public static long playerLateTime = 100;
	public static long playerTotalTime;

	/* total duration of all players during a round */
	public static long PLAYERSTotalTime;

	/* total duration of server update time during a round */
	public static long updateTime = 0;

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
	
	public static HashMap<CellType, List<UnitType>> terrainMovePossibilities = new HashMap<CellType, List<UnitType>>();
	
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
		for (CellType ct : CellType.values()) {
			terrainMovePossibilities.put(ct, new ArrayList<UnitType>());
			movePenalty.put(ct, 0.0);
		}
		
		Document paramDoc = GameUtils.readXMLDocument("GamePolicy.xml");
		MapReader.readMap(paramDoc);
		mapsize = new Point2i(MapReader.mapWidth, MapReader.mapHeight);
		lastTurn = MapReader.nrTurns;
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
		lastTurn = Integer.parseInt(parametersNode.getElementsByTagName("nrTurns").item(0).getTextContent());
		nrPlayerUnits = Integer.parseInt(parametersNode.getElementsByTagName("nrPlayerUnits").item(0).getTextContent());
		mapName = parametersNode.getElementsByTagName("mapName").item(0).getTextContent();
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
		
		// retrieve character abilities
		Element abilities = (Element)ruleNode.getElementsByTagName("abilities").item(0);
		NodeList movementAbilities = abilities.getElementsByTagName("movement");
		for (int i = 0; i < movementAbilities.getLength(); i++) {
			Element movement = (Element)movementAbilities.item(i);
			String terrain = movement.getElementsByTagName("terrain").item(0).getTextContent();
			String unit = movement.getElementsByTagName("unit").item(0).getTextContent();
			
			CellType terrainType = CellType.valueOf(terrain);
			if (unit.equals("Any")) {
				List<UnitType> unitAccessList = terrainMovePossibilities.get(terrainType);
				if (unitAccessList == null) {
					unitAccessList = new ArrayList<UnitType>();
					unitAccessList.add(UnitType.Tazmanian);
					unitAccessList.add(UnitType.Fox);
					unitAccessList.add(UnitType.Crocodile);
					terrainMovePossibilities.put(terrainType, unitAccessList);
				}
				else {
					unitAccessList.add(UnitType.Tazmanian);
					unitAccessList.add(UnitType.Fox);
					unitAccessList.add(UnitType.Crocodile);
				}
			}
			else {
				UnitType allowedUnit = UnitType.valueOf(unit);
				List<UnitType> unitAccessList = terrainMovePossibilities.get(terrainType);
				if (unitAccessList == null) {
					unitAccessList = new ArrayList<UnitType>();
					unitAccessList.add(allowedUnit);
					terrainMovePossibilities.put(terrainType, unitAccessList);
				}
				else {
					unitAccessList.add(allowedUnit);
				}
			}
		}
		
	}

}

class MapReader {
	public static int mapWidth;
	public static int mapHeight;
	public static int nrTurns;
	public static CellState[][] cells;
	
	public static void readMap(Document paramDoc) {
		Element root = (Element)paramDoc.getDocumentElement();
		Element parametersNode = (Element)root.getElementsByTagName("parameters").item(0);
		String mapFile = "maps/" + parametersNode.getElementsByTagName("mapName").item(0).getTextContent();
		
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