package org.aimas.craftingquest.core;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.aimas.craftingquest.state.Blueprint;
import org.aimas.craftingquest.state.CellState;
import org.aimas.craftingquest.state.GameState;
import org.aimas.craftingquest.state.MapState;
import org.aimas.craftingquest.state.Merchant;
import org.aimas.craftingquest.state.PlayerState;
import org.aimas.craftingquest.state.Point2i;
import org.aimas.craftingquest.state.ResourceAttributes;
import org.aimas.craftingquest.state.StrategicResource;
import org.aimas.craftingquest.state.UnitState;
import org.aimas.craftingquest.state.CraftedObject.BasicResourceType;
import org.aimas.craftingquest.state.UnitState.UnitType;


public class GameGenerator {
	private static Random randGen = new Random();
	
	
	public static GameState setupGame() {
		/* initialize scenario */
		GamePolicy.initScenario();
		
		GameState game = checkForSavedGame();
		//GameState game = null;
		
		/* create initial game state if saved state does not exist */
		if (game == null) {
			game = new GameState();
			game.map = GamePolicy.map;
			
			/* set merchant list */
			game.merchantList = setupMerchantList(game.map);
			
			/* set map resources */
			HashMap<BasicResourceType, Integer> resourceAmountsByType = ResourceGenerator.placeResources(game.map);
			game.blueprints = ResourceGenerator.generateBlueprints(resourceAmountsByType);		// generate blueprints
			game.resourceAmountsByType = resourceAmountsByType;
			
			/* distribute blueprints to merchants */
			distributeBlueprints(game);
			 
			ScanAttributeGenerator.setupScanAttributes(game.map);		// generate scan attributes for each map cell
			
			/* setup initial player states - there should be a maximum of 4 players */
			for (int i = 0; i < GamePolicy.noPlayers; i++) {
				if (i % 4 == 0) {
					PlayerState player = setupPlayerState(i + 1, GamePolicy.nrPlayerUnits, new Point2i(5, 5), game.map);
					player.availableBlueprints = game.blueprints;	// all available blueprints are known at the start
					//game.playerStates.add(player);
					game.playerStates.put(player.id, player);
				}
				else if (i % 4 == 1) {
					PlayerState player = setupPlayerState(i + 1, GamePolicy.nrPlayerUnits, new Point2i(GamePolicy.mapsize.x - 5, 5), game.map);
					player.availableBlueprints = game.blueprints;	// all available blueprints are known at the start
					//game.playerStates.add(player);
					game.playerStates.put(player.id, player);
				}
				else if (i % 4 == 2) {
					PlayerState player = setupPlayerState(i + 1, GamePolicy.nrPlayerUnits, new Point2i(5, GamePolicy.mapsize.y - 5), game.map);
					player.availableBlueprints = game.blueprints;	// all available blueprints are known at the start
					//game.playerStates.add(player);
					game.playerStates.put(player.id, player);
				}
				else {
					PlayerState player = setupPlayerState(i + 1, GamePolicy.nrPlayerUnits, new Point2i(GamePolicy.mapsize.x - 5, GamePolicy.mapsize.y - 5), game.map);
					player.availableBlueprints = game.blueprints;	// all available blueprints are known at the start
					//game.playerStates.add(player);
					game.playerStates.put(player.id, player);
				}
			}
			
			/* initialize tower list for every player */
			game.initializeTowerLists();
		}
		else {
			/*reset number of turns according to map size*/
			if (game.map.mapWidth >= 60 && game.map.mapWidth < 70) {
				game.round.noRounds = 160;
			}
			
			if (game.map.mapWidth >= 70 && game.map.mapWidth < 80) {
				game.round.noRounds = 180;
			}
			
			if (MapReader.mapWidth >= 80) {
				game.round.noRounds = 200;
			}
			
			/* setup initial player states - there should be a maximum of 4 players */
			game.playerStates.clear();
			
			for(int y = 0; y < game.map.mapHeight; y++) {
				for(int x = 0; x < game.map.mapWidth; x++) {
					game.map.cells[y][x].cellUnits.clear();
				}
			}
			
			for (int i = 0; i < GamePolicy.noPlayers; i++) {
				if (i % 4 == 0) {
					PlayerState player = setupPlayerState(i + 1, GamePolicy.nrPlayerUnits, new Point2i(5, 5), game.map);
					player.availableBlueprints = game.blueprints;	// all available blueprints are known at the start
					//game.playerStates.add(player);
					game.playerStates.put(player.id, player);
				}
				else if (i % 4 == 1) {
					PlayerState player = setupPlayerState(i + 1, GamePolicy.nrPlayerUnits, new Point2i(GamePolicy.mapsize.x - 5, 5), game.map);
					player.availableBlueprints = game.blueprints;	// all available blueprints are known at the start
					//game.playerStates.add(player);
					game.playerStates.put(player.id, player);
				}
				else if (i % 4 == 2) {
					PlayerState player = setupPlayerState(i + 1, GamePolicy.nrPlayerUnits, new Point2i(5, GamePolicy.mapsize.y - 5), game.map);
					player.availableBlueprints = game.blueprints;	// all available blueprints are known at the start
					//game.playerStates.add(player);
					game.playerStates.put(player.id, player);
				}
				else {
					PlayerState player = setupPlayerState(i + 1, GamePolicy.nrPlayerUnits, new Point2i(GamePolicy.mapsize.x - 5, GamePolicy.mapsize.y - 5), game.map);
					player.availableBlueprints = game.blueprints;	// all available blueprints are known at the start
					//game.playerStates.add(player);
					game.playerStates.put(player.id, player);
				}
			}
			
			/* initialize tower list for every player */
			game.initializeTowerLists();
		}
		
		printResourceStatistics(game.resourceAmountsByType, game.blueprints);
		
		return game;
	}
	
	private static GameState checkForSavedGame() {
		String savedResFilename = GamePolicy.mapName + ".cqres";
		String mapFile = "maps/" + savedResFilename;
		
		ObjectInputStream objin = null;
		GameState game = null;
		HashMap<Point2i, HashMap<BasicResourceType, Integer>> cellResources = null;
		HashMap<Point2i, HashMap<BasicResourceType, ResourceAttributes>> cellAttributes = null;
		
		try {
			objin = new ObjectInputStream (new FileInputStream(mapFile));
			
			Object obj = objin.readObject();
			if (obj == null || !(obj instanceof GameState)) {
				return null;
			}
			
			game = (GameState)obj;
						
			cellResources = (HashMap<Point2i, HashMap<BasicResourceType, Integer>>)objin.readObject();
			if (cellResources == null) {
				return null;
			}

			cellAttributes = (HashMap<Point2i, HashMap<BasicResourceType, ResourceAttributes>>)objin.readObject();
			if (cellAttributes == null) {
				return null;
			}
			
			for (Point2i p : cellResources.keySet()) {
				game.map.cells[p.y][p.x].resources = new HashMap<BasicResourceType, Integer>(cellResources.get(p));
			}
			
			for (Point2i p : cellAttributes.keySet()) {
				game.map.cells[p.y][p.x].scanAttributes = new HashMap<BasicResourceType, ResourceAttributes>(cellAttributes.get(p));
			}
		}catch (EOFException ex) { 
            System.out.println("End of file reached.");
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }catch(FileNotFoundException e) {
			System.out.println("No .cqres file found. Will build fresh game state.");
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		finally{
			//Close the ObjectInputStream
            try {
                if (objin != null) {
                    objin.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
		}
	
		return game;
	}

	private static void distributeBlueprints(GameState game) throws IllegalArgumentException {
		List<Blueprint> blueprints = game.blueprints;
		List<Merchant> merchants = game.merchantList;
		
		// we rely on the premise that merchant camps are symmetrically distributed across the map
		List<Merchant> topSideMerchants = new ArrayList<Merchant>();		// these two lists
		List<Merchant> bottomSideMerchants = new ArrayList<Merchant>();		// must have the same size
		
		
		for (Merchant m : merchants) {
			Point2i pos = m.getPosition();
			if (pos.x + pos.y < game.map.mapWidth) {
				topSideMerchants.add(m);
			}
			else {
				bottomSideMerchants.add(m);
			}
		}
		
		if (topSideMerchants.size() != bottomSideMerchants.size()) {
			System.out.println(topSideMerchants.size() - bottomSideMerchants.size());
			throw new IllegalArgumentException("The merchant camps are not evenly distributed");
		}
		
		// step 1 - ensure that each blueprint is present in at least half the merchant camps
		for (Blueprint bp : blueprints) {
			int count = topSideMerchants.size() / 2;
			ArrayList<Merchant> auxListTop = new ArrayList<Merchant>(topSideMerchants);
			ArrayList<Merchant> auxListBottom = new ArrayList<Merchant>(bottomSideMerchants);
			
			for (int i = 0; i < count; i++) {
				int index = randGen.nextInt(auxListTop.size());
				Merchant mTop = auxListTop.remove(index);
				Merchant mBottom = auxListBottom.remove(index);
				
				mTop.getBlueprints().add(bp);
				mBottom.getBlueprints().add(bp);
			}
		}
		
		// step 2 - ensure that every merchant has at least 1/4 blueprints
		int count = blueprints.size() / 4;
		for (int k = 0; k < topSideMerchants.size(); k++) {
			Merchant mTop = topSideMerchants.get(k);
			Merchant mBottom = bottomSideMerchants.get(k);
			
			ArrayList<Blueprint> auxList = new ArrayList<Blueprint>(blueprints);
			for (int i = 0; i < count; i++) {
				int index = randGen.nextInt(auxList.size());
				Blueprint bp = auxList.remove(index);
				
				if (!mTop.getBlueprints().contains(bp)) {
					mTop.getBlueprints().add(bp);
				}
				
				if (!mBottom.getBlueprints().contains(bp)) {
					mBottom.getBlueprints().add(bp);
				}
			}
		}
	}

	private static List<Merchant> setupMerchantList(MapState map) {
		List<Merchant> merchantList = new ArrayList<Merchant>();
		
		for (int i = 0; i < map.mapHeight; i++) {
			for (int j = 0; j < map.mapWidth; j++) {
				StrategicResource strRes = map.cells[i][j].strategicResource;
				if (strRes != null && strRes instanceof Merchant) {
					merchantList.add((Merchant)strRes);
				}
			}
		}
		
		return merchantList;
	}

	private static PlayerState setupPlayerState(int playerID, int nrUnits, Point2i initPos, MapState map) {
		PlayerState pState = new PlayerState();
		pState.id = playerID;
		pState.credit = GamePolicy.initialTeamCredit;
		
		pState.round.currentRound = 0;
		pState.round.noRounds = GamePolicy.lastTurn;
		pState.round.roundDuration = GamePolicy.playerActionTime;
		
		pState.mapHeight = map.mapHeight;
		pState.mapWidth = map.mapWidth;
		
		// setup player units
		for (int i = 0; i < nrUnits; i++) {
			UnitType utype = UnitType.Tazmanian;
			switch (i % 3) {
			case 0:
				utype = UnitType.Tazmanian;
				break;
			case 1:
				utype = UnitType.Fox;
				break;
			case 2:
				utype = UnitType.Crocodile;
				break;
			}

			// search for a position for the unit in the vicinity of the given initial position
			Point2i unitPos = null;
			if (initPos.x < GamePolicy.mapsize.x / 2) {
				unitPos = setUnitInitialPosition1(map, utype, initPos.x - 2, initPos.x + 8, initPos.y - 2, initPos.y + 8);
				if (unitPos == null) {
					unitPos = setUnitInitialPosition1(map, utype, initPos.x - 2, GamePolicy.mapsize.x / 2, initPos.y - 2, GamePolicy.mapsize.y / 2);
				}
			} else {
				unitPos = setUnitInitialPosition2(map, utype, initPos.x - 8, initPos.x + 2, initPos.y - 8, initPos.y + 2);
				if (unitPos == null) {
					unitPos = setUnitInitialPosition2(map, utype, GamePolicy.mapsize.x / 2, initPos.x + 2, GamePolicy.mapsize.y / 2, initPos.y + 2);
				}
			}

			if (unitPos == null) { // it should never come to this
				throw new NullPointerException("Unit initial position error: " + utype.name());
			}
			
			int unitID = playerID * nrUnits + i;
			UnitState unit = new UnitState(unitID, playerID, utype, unitPos, GamePolicy.unitEnergy);
			
			// set unit's sight
			for (int ii = 0, y = unit.pos.y - GamePolicy.sightRadius; y <= unit.pos.y + GamePolicy.sightRadius; y++, ii++) {
				for (int jj = 0, x = unit.pos.x - GamePolicy.sightRadius; x <= unit.pos.x + GamePolicy.sightRadius; x++, jj++) {
					unit.sight[ii][jj] = null;
					if (x >= 0 && x < GamePolicy.mapsize.x && y >= 0 && y < GamePolicy.mapsize.y) {
						unit.sight[ii][jj] = map.cells[y][x];
					}
				}
			}
			
			pState.units.add(unit); // add unit to player's list
			map.cells[unit.pos.y][unit.pos.x].cellUnits.add(unit.getOpponentPerspective()); // add unit to its cell
			
		}
		
		return pState;
	}
	
	private static Point2i setUnitInitialPosition1(MapState map, UnitType utype, int xmin, int xmax, int ymin, int ymax) {
		for(int y = ymin; y < ymax; y++) {
			for(int x = xmin; x < xmax; x++) {
				CellState cell = map.cells[y][x];
				if (cell.cellUnits.isEmpty() && GamePolicy.terrainMovePossibilities.get(cell.type).contains(utype)) {
					return new Point2i(x, y);
				}
			}
		}
		
		return null;
	}
	
	private static Point2i setUnitInitialPosition2(MapState map, UnitType utype, int xmin, int xmax, int ymin, int ymax) {
		for(int y = ymax; y > ymin; y--) {
			for(int x = xmax; x > xmin; x--) {
				CellState cell = map.cells[y][x];
				if (cell.cellUnits.isEmpty() && GamePolicy.terrainMovePossibilities.get(cell.type).contains(utype)) {
					return new Point2i(x, y);
				}
			}
		}
		
		return null;
	}
	
	private static void printResourceStatistics(HashMap<BasicResourceType, Integer> resourceAmountsByType,
			List<Blueprint> blueprints) {
		try {
			FileWriter fw = new FileWriter("resource-stats.txt");
			fw.write("======== Resources -- Quantity ========");
			fw.write("\n");
			for (BasicResourceType br : resourceAmountsByType.keySet()) {
				fw.write(br.name() + ": " + resourceAmountsByType.get(br) + "\n");
			}
			
			fw.write("\n");
			fw.write("======== Objects -- Values ========");
			fw.write("\n");
			for (Blueprint bp : blueprints) {
				fw.write(bp.getDescribedObject().getType().name() + ":" + bp.getDescribedObject().getValue() + "\n");
			}
			
			fw.close();
		}catch(IOException ioe) {
			ioe.printStackTrace();
		}
		
	}

}
