package org.aimas.craftingquest.core;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

import org.aimas.craftingquest.state.Blueprint;
import org.aimas.craftingquest.state.CellState;
import org.aimas.craftingquest.state.GameState;
import org.aimas.craftingquest.state.MapState;
import org.aimas.craftingquest.state.PlayerState;
import org.aimas.craftingquest.state.Point2i;
import org.aimas.craftingquest.state.UnitState;
import org.aimas.craftingquest.state.resources.ResourceType;


public class GameGenerator {

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
			/* game.merchantList = setupMerchantList(game.map); */
			
			/* set map resources */
			HashMap<ResourceType, Integer> resourceAmountsByType = ResourceGenerator.placeResources(game.map);
			GamePolicy.blueprints = ResourceGenerator.generateBlueprints(resourceAmountsByType);		// generate blueprints
			game.resourceAmountsByType = resourceAmountsByType;
			
			/* setup initial player states - there should be only 2 players */
			for (int i = 0; i < GamePolicy.noPlayers; i++) {
				if (i % 4 == 0) {
					PlayerState player = setupPlayerState(i + 1, GamePolicy.nrPlayerUnits, GamePolicy.initialPlayerPositions.get(0), game.map);
					player.availableBlueprints = GamePolicy.blueprints;	// all available blueprints are known at the start
					//game.playerStates.add(player);
					game.playerStates.put(player.id, player);
				}
				else if (i % 4 == 1) {
					PlayerState player = setupPlayerState(i + 1, GamePolicy.nrPlayerUnits, GamePolicy.initialPlayerPositions.get(1), game.map);
					player.availableBlueprints = GamePolicy.blueprints;	// all available blueprints are known at the start
					//game.playerStates.add(player);
					game.playerStates.put(player.id, player);
				} else if (i % 4 == 2) {
					PlayerState player = setupPlayerState(i + 1, GamePolicy.nrPlayerUnits, GamePolicy.initialPlayerPositions.get(2), game.map);
					player.availableBlueprints = GamePolicy.blueprints;	// all available blueprints are known at the start
					//game.playerStates.add(player);
					game.playerStates.put(player.id, player);
				}
				else {
					PlayerState player = setupPlayerState(i + 1, GamePolicy.nrPlayerUnits, GamePolicy.initialPlayerPositions.get(3), game.map);
					player.availableBlueprints = GamePolicy.blueprints;	// all available blueprints are known at the start
					//game.playerStates.add(player);
											game.playerStates.put(player.id, player);
				}
			}
			
			/* initialize tower list for every player */
			game.initializeTowerTrapLists();
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
			
			/* setup initial player states - there should be only 2 players */
			game.playerStates.clear();
			
			for(int y = 0; y < game.map.mapHeight; y++) {
				for(int x = 0; x < game.map.mapWidth; x++) {
					game.map.cells[y][x].cellUnits.clear();
				}
			}
			
			for (int i = 0; i < GamePolicy.noPlayers; i++) {
				if (i % 2 == 0) {
					PlayerState player = setupPlayerState(i + 1, GamePolicy.nrPlayerUnits, new Point2i(5, 5), game.map);
					player.availableBlueprints = GamePolicy.blueprints;	// all available blueprints are known at the start
					//game.playerStates.add(player);
					game.playerStates.put(player.id, player);
				}
				else if (i % 2 == 1) {
					PlayerState player = setupPlayerState(i + 1, GamePolicy.nrPlayerUnits, new Point2i(5, GamePolicy.mapsize.y - 5), game.map);
					player.availableBlueprints = GamePolicy.blueprints;	// all available blueprints are known at the start
					//game.playerStates.add(player);
					game.playerStates.put(player.id, player);
				}
				else if (i % 2 == 2) {
					PlayerState player = setupPlayerState(i + 1, GamePolicy.nrPlayerUnits, new Point2i(GamePolicy.mapsize.x - 5, 5), game.map);
					player.availableBlueprints = GamePolicy.blueprints;	// all available blueprints are known at the start
					//game.playerStates.add(player);
					game.playerStates.put(player.id, player);
				}
				else {
					PlayerState player = setupPlayerState(i + 1, GamePolicy.nrPlayerUnits, new Point2i(GamePolicy.mapsize.x - 5, GamePolicy.mapsize.y - 5), game.map);
					player.availableBlueprints = GamePolicy.blueprints;	// all available blueprints are known at the start
					//game.playerStates.add(player);
					game.playerStates.put(player.id, player);
				}
			}
			
			/* initialize tower list for every player */
			game.initializeTowerTrapLists();
		}
		
		printResourceStatistics(game.resourceAmountsByType, GamePolicy.blueprints);
		
		return game;
	}
	
	@SuppressWarnings("unchecked")
	private static GameState checkForSavedGame() {
		String savedResFilename = GamePolicy.mapName + ".cqres";
		String mapFile = "maps/" + savedResFilename;
		
		ObjectInputStream objin = null;
		GameState game = null;
		HashMap<Point2i, HashMap<ResourceType, Integer>> cellResources = null;
		
		try {
			objin = new ObjectInputStream (new FileInputStream(mapFile));
			
			Object obj = objin.readObject();
			if (obj == null || !(obj instanceof GameState)) {
				return null;
			}
			
			game = (GameState)obj;
						
			cellResources = (HashMap<Point2i, HashMap<ResourceType, Integer>>)objin.readObject();
			if (cellResources == null) {
				return null;
			}

			for (Point2i p : cellResources.keySet()) {
				game.map.cells[p.y][p.x].resources = new HashMap<ResourceType, Integer>(cellResources.get(p));
				game.map.cells[p.y][p.x].resourceTypes = new TreeSet<ResourceType>(cellResources.get(p).keySet());
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
	
	private static PlayerState setupPlayerState(int playerID, int nrUnits, Point2i initPos, MapState map) {
		PlayerState pState = new PlayerState();
		pState.id = playerID;
		pState.gold = GamePolicy.initialTeamGold;
		
		pState.round.currentRound = 0;
		pState.round.noRounds = GamePolicy.lastTurn;
		pState.round.roundDuration = GamePolicy.playerActionTime;
		
		pState.mapHeight = map.mapHeight;
		pState.mapWidth = map.mapWidth;
		
		// setup player units
		for (int i = 0; i < nrUnits; i++) {
			/*UnitType utype = UnitType.Tazmanian;
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
			}*/

			// search for a position for the unit in the vicinity of the given initial position
			Point2i unitPos = null;
			if (initPos.x < GamePolicy.mapsize.x / 2) {
				unitPos = setUnitInitialPosition1(map, /*utype,*/ initPos.x - 2, initPos.x + 8, initPos.y - 2, initPos.y + 8);
				if (unitPos == null) {
					unitPos = setUnitInitialPosition1(map, /*utype,*/ initPos.x - 2, GamePolicy.mapsize.x / 2, initPos.y - 2, GamePolicy.mapsize.y / 2);
				}
			} else {
				unitPos = setUnitInitialPosition2(map, /*utype,*/ initPos.x - 8, initPos.x + 2, initPos.y - 8, initPos.y + 2);
				if (unitPos == null) {
					unitPos = setUnitInitialPosition2(map, /*utype,*/ GamePolicy.mapsize.x / 2, initPos.x + 2, GamePolicy.mapsize.y / 2, initPos.y + 2);
				}
			}

			if (unitPos == null) { // it should never come to this
				throw new NullPointerException("Unit initial position error for player: " + playerID/*utype.name()*/);
			}
			
			int unitID = playerID * nrUnits + i;
			UnitState unit = new UnitState(unitID, playerID, /*utype,*/ unitPos, GamePolicy.initialUnitMaxLife);
			
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
	
	private static Point2i setUnitInitialPosition1(MapState map,/* UnitType utype,*/ int xmin, int xmax, int ymin, int ymax) {
		for(int y = ymin; y < ymax; y++) {
			for(int x = xmin; x < xmax; x++) {
				CellState cell = map.cells[y][x];
				if (cell.cellUnits.isEmpty() /*&& GamePolicy.terrainMovePossibilities.get(cell.type).contains(utype)*/) {
					return new Point2i(x, y);
				}
			}
		}
		
		return null;
	}
	
	private static Point2i setUnitInitialPosition2(MapState map, /*UnitType utype,*/ int xmin, int xmax, int ymin, int ymax) {
		for(int y = ymax; y > ymin; y--) {
			for(int x = xmax; x > xmin; x--) {
				CellState cell = map.cells[y][x];
				if (cell.cellUnits.isEmpty() /*&& GamePolicy.terrainMovePossibilities.get(cell.type).contains(utype)*/) {
					return new Point2i(x, y);
				}
			}
		}
		
		return null;
	}
	
	private static void printResourceStatistics(HashMap<ResourceType, Integer> resourceAmountsByType,
			List<Blueprint> blueprints) {
		try {
			FileWriter fw = new FileWriter("resource-stats.txt");
			fw.write("======== Resources -- Quantity ========");
			fw.write("\n");
			for (ResourceType br : resourceAmountsByType.keySet()) {
				fw.write(br.name() + ": " + resourceAmountsByType.get(br) + "\n");
			}
			
			fw.write("\n");
			fw.write("======== Objects -- Values ========");
			fw.write("\n");
			for (Blueprint bp : blueprints) {
				fw.write(bp.getType() + ":" + bp.getLevel() + "\n");
			}
			
			fw.close();
		}catch(IOException ioe) {
			ioe.printStackTrace();
		}
		
	}

}
