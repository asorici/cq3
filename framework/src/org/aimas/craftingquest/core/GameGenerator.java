package org.aimas.craftingquest.core;

import java.util.HashMap;

import org.aimas.craftingquest.core.GamePolicy.BasicResourceType;
import org.aimas.craftingquest.state.CellState;
import org.aimas.craftingquest.state.GameState;
import org.aimas.craftingquest.state.MapState;
import org.aimas.craftingquest.state.PlayerState;
import org.aimas.craftingquest.state.Point2i;
import org.aimas.craftingquest.state.UnitState;
import org.aimas.craftingquest.state.UnitState.UnitType;

/**
 * 
 * @author Razvan
 */
public class GameGenerator {

	public static Configuration readConfigFromFile(){
		return new Configuration();
	}
	
	public static GameState hardcoded1(int noPlayers) {
		GameState g = new GameState();

		// maximum number of turns
		// g.lastTurn = 3;

		g.map = new TerrainGenerator().hardcoded1();
		for (int i = 0; i < noPlayers; i++) {
			g.playerStates.add(hardcoded2(i, 3));
		}

		return g;
	}

	public static PlayerState hardcoded2(int id, int noUnits) {
		PlayerState p = new PlayerState();

		p.id = new Integer(id);
		for (int i = 0; i < noUnits; i++) {
			UnitState unit = new UnitState();
			unit.playerID = id;
			unit.type = UnitType.values()[i];
			unit.pos = new Point2i();
			p.units.add(unit);
		}

		return p;
	}

	public static GameState setupGame() {
		/* initialize scenario */
		GamePolicy.initScenario();
		
		/* create initial game state */
		GameState game = new GameState();
		game.map = new TerrainGenerator().hardcoded1();
		
		/* set map resources */
		HashMap<BasicResourceType, Integer> resourceAmountsByType = ResourceGenerator.placeResources(game.map);
		game.blueprints = ResourceGenerator.generateBlueprints(resourceAmountsByType);		// generate blueprints
		game.resourceAmountsByType = resourceAmountsByType;
		
		// TODO - blueprint-urile trebuie distribuite la merchants 
		ScanAttributeGenerator.setupScanAttributes(game.map);		// generate scan attributes for each map cell
		
		/* setup initial player states - there should be only 2 players */
		for (int i = 0; i < GamePolicy.noPlayers; i++) {
			if (i % 2 == 0) {
				PlayerState player = setupPlayerState(i + 1, GamePolicy.nrPlayerUnits, new Point2i(5, 5), game.map);
				game.playerStates.add(player);
			}
			else {
				PlayerState player = setupPlayerState(i + 1, GamePolicy.nrPlayerUnits, new Point2i(GamePolicy.mapsize.x - 5, GamePolicy.mapsize.y - 5), game.map);
				game.playerStates.add(player);
			}
		}
		
		/* TODO: setup merchant list */
		
		return game;
	}
	
	private static PlayerState setupPlayerState(int playerID, int nrUnits, Point2i initPos, MapState map) {
		PlayerState pState = new PlayerState();
		pState.id = playerID;
		pState.credit = GamePolicy.initialTeamCredit;
		pState.totalScore = 0;
		pState.round.currentRound = 1;
		
		
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

			UnitState unit = new UnitState(playerID, utype, unitPos, GamePolicy.unitEnergy);
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
	
	/*
	public static GamePolicy hardcoded3() {// int noPlayers) {
		int noPlayers = 2;
		int noTurns = 10;
		return new GamePolicy(noPlayers, 1000, 100, 0, noTurns);
	}
	*/
}
