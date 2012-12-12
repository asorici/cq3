package org.aimas.craftingquest.core;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.List;

import org.aimas.craftingquest.state.Blueprint;
import org.aimas.craftingquest.state.GameState;
import org.aimas.craftingquest.state.MapState;
import org.aimas.craftingquest.state.PlayerState;
import org.aimas.craftingquest.state.Point2i;
import org.aimas.craftingquest.state.Transition;
import org.aimas.craftingquest.state.Transition.ActionType;
import org.aimas.craftingquest.state.TransitionResult;
import org.aimas.craftingquest.state.TransitionResult.TransitionError;
import org.aimas.craftingquest.state.UnitState;
import org.aimas.craftingquest.state.objects.ArmourObject;
import org.aimas.craftingquest.state.objects.CraftedObjectType;
import org.aimas.craftingquest.state.objects.SwordObject;
import org.aimas.craftingquest.state.resources.ResourceType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory.Woodstox;

public class UnitTesting {
	static GameState game;
	static ActionEngine actionEngine;
	static PlayerState player1;
	static PlayerState player2;
	
	static Point2i posPlayer1;
	static Point2i posPlayer2;
	
	static {
		posPlayer1 = new Point2i(30, 28);
		posPlayer2 = new Point2i(30, 29);
	}
	
	// ====================================== SETUP ======================================== //
	
	@Before
	public  void setupGame() {
		/* initialize scenario */
		GamePolicy.initScenario();
		
		/* create game state */
		game = new GameState();
		game.map = GamePolicy.map;
		
		actionEngine = new ActionEngine(game);
		
		/* setup player states - 2 players each with one unit */
		player1 = setupPlayerState(1, game.map, GamePolicy.blueprints);
		player2 = setupPlayerState(2, game.map, GamePolicy.blueprints);
		game.playerStates.put(player1.id, player1);
		game.playerStates.put(player2.id, player2);
	}
	
	
	private static PlayerState setupPlayerState(int playerID, MapState map, List<Blueprint> playerBlueprints) {
		PlayerState pState = new PlayerState();
		pState.id = playerID;
		pState.gold = GamePolicy.initialTeamGold;
		
		pState.round.currentRound = 0;
		pState.round.noRounds = GamePolicy.lastTurn;
		pState.round.roundDuration = GamePolicy.playerActionTime;
		
		pState.mapHeight = map.mapHeight;
		pState.mapWidth = map.mapWidth;
		
		//pState.availableBlueprints.addAll(playerBlueprints);
		
		return pState;
	}

	// method to "empower" a unit for everything it needs for a test
	private static UnitState setupUnit(int id, int playerId, Point2i pos, int energy, 
			HashMap<ResourceType, Integer> unitResources) {
		UnitState unit = new UnitState(id, playerId, pos, energy);
		
		unit.carriedResources.putAll(unitResources);
		
		// setup unit sight
		for (int ii = 0, y = unit.pos.y - GamePolicy.sightRadius; y <= unit.pos.y + GamePolicy.sightRadius; y++, ii++) {
			for (int jj = 0, x = unit.pos.x - GamePolicy.sightRadius; x <= unit.pos.x + GamePolicy.sightRadius; x++, jj++) {
				unit.sight[ii][jj] = null;
				if (x >= 0 && x < GamePolicy.mapsize.x && y >= 0 && y < GamePolicy.mapsize.y) {
					unit.sight[ii][jj] = game.map.cells[y][x];
				}
			}
		}
		
		return unit;
	}
	
	
	// ======================================================================================== //
	// ================================ MOVE ACTION TESTS =================================== //
	//@Test
	public void testMove() {
		Point2i fromPos = new Point2i(15, 15);
		Point2i toPos = new Point2i(16, 15);
		
		// setup unit
		UnitState unit = setupUnit(0, player1.id, fromPos, GamePolicy.initialUnitMaxLife, 
				new HashMap<ResourceType, Integer>());
		
		// add unit to player
		player1.units.add(unit);
		
		// add unit to map
		game.map.cells[unit.pos.y][unit.pos.x].cellUnits.add(unit.getOpponentPerspective());
		
		// build transition object
		Transition transition = new Transition(ActionType.Move, new Object[] { unit.id, toPos });
		
		// perform move
		TransitionResult transitionResult = actionEngine.process(player1, transition);
		
		// assertions - this move should have succeeded
		assertEquals("MoveResult", transitionResult.errorType, TransitionError.NoError);
		assertEquals("MoveEnergyConsumption", unit.energy, GamePolicy.initialUnitMaxLife - GamePolicy.moveBase);
		assertEquals("prevCellContents", game.map.cells[fromPos.y][fromPos.x].cellUnits.size(), 0);
		assertEquals("nextCellContents", game.map.cells[toPos.y][toPos.x].cellUnits.size(), 1);
	}
	
	
	//@Test
	public void testNoEnergyMove() {
		Point2i fromPos = new Point2i(15, 15);
		Point2i toPos = new Point2i(16, 15);
		
		// setup unit
		UnitState unit = setupUnit(0, player1.id, fromPos, GamePolicy.moveBase - 1, 
				new HashMap<ResourceType, Integer>());
		
		// add unit to player
		player1.units.add(unit);
		
		// add unit to map
		game.map.cells[unit.pos.y][unit.pos.x].cellUnits.add(unit.getOpponentPerspective());
		
		// build transition object
		Transition transition = new Transition(ActionType.Move, new Object[] { unit.id, toPos });
		
		// perform move
		TransitionResult transitionResult = actionEngine.process(player1, transition);
		
		// assertions - this move should have succeeded
		assertEquals("MoveResult", transitionResult.errorType, TransitionError.NoEnergyError);
		assertEquals("MoveEnergyConsumption", unit.energy, GamePolicy.moveBase - 1);
		assertEquals("prevCellContents", game.map.cells[fromPos.y][fromPos.x].cellUnits.size(), 1);
		assertEquals("prevCellContents", game.map.cells[toPos.y][toPos.x].cellUnits.size(), 0);
	}
	
	
	//@Test
	public void testIllegalTerrainMove() {
		Point2i fromPos = new Point2i(15, 15);
		Point2i toPos = new Point2i(15, 16);
		
		// setup unit
		UnitState unit = setupUnit(0, player1.id, fromPos, GamePolicy.initialUnitMaxLife, 
				new HashMap<ResourceType, Integer>());
		
		// add unit to player
		player1.units.add(unit);
		
		// add unit to map
		game.map.cells[unit.pos.y][unit.pos.x].cellUnits.add(unit.getOpponentPerspective());
		
		// build transition object
		Transition transition = new Transition(ActionType.Move, new Object[] { unit.id, toPos });
		
		// perform move
		TransitionResult transitionResult = actionEngine.process(player1, transition);
		
		// assertions - this move should have succeeded
		assertEquals("MoveResult", transitionResult.errorType, TransitionError.TerrainError);
		assertEquals("MoveEnergyConsumption", unit.energy, GamePolicy.initialUnitMaxLife);
		assertEquals("prevCellContents", game.map.cells[fromPos.y][fromPos.x].cellUnits.size(), 1);
		assertEquals("prevCellContents", game.map.cells[toPos.y][toPos.x].cellUnits.size(), 0);
	}
	
	
	// ======================================================================================== //
	// ================================ DIG ACTION TESTS =================================== //
	//@Test
	public void testDig() {
		Point2i pos = new Point2i(15, 15);
		
		
		// setup unit
		UnitState unit = setupUnit(0, player1.id, pos, GamePolicy.initialUnitMaxLife, 
				new HashMap<ResourceType, Integer>());
		
		// add unit to player
		player1.units.add(unit);
		
		// add unit to map
		game.map.cells[unit.pos.y][unit.pos.x].cellUnits.add(unit.getOpponentPerspective());
		
		// build transition object
		Transition transition = new Transition(ActionType.Dig, new Object[] { unit.id, pos });
		
		// perform dig
		TransitionResult transitionResult = actionEngine.process(player1, transition);
		
		// assertions - this move should have succeeded
		assertEquals("DigResult", transitionResult.errorType, TransitionError.NoError);
		assertEquals("DigResultContents", unit.currentCellResources.get(ResourceType.WOOD), 
				game.map.cells[pos.y][pos.x].resources.get(ResourceType.WOOD));
		
	}
	
	
	// ======================================================================================== //
	// ================================ CRAFT ACTION TESTS =================================== //
	//@Test
	public void testCraft() {
		Point2i pos = new Point2i(15, 15);

		// setup unit
		HashMap<ResourceType, Integer> unitResources = new HashMap<ResourceType, Integer>();
		unitResources.put(ResourceType.IRON, 20);
		unitResources.put(ResourceType.STONE, 5);
		unitResources.put(ResourceType.WOOD, 5);
		
		UnitState unit = setupUnit(0, player1.id, pos,
				GamePolicy.initialUnitMaxLife,
				unitResources);
		

		// add unit to player
		player1.units.add(unit);

		// add unit to map
		game.map.cells[unit.pos.y][unit.pos.x].cellUnits.add(unit.getOpponentPerspective());
		
		// find blueprint
		Blueprint sword2Blueprint = null;
		for (Blueprint bp : GamePolicy.blueprints) {
			if (bp.getType() == CraftedObjectType.SWORD && bp.getLevel() == 2) {
				sword2Blueprint = bp;
				break;
			}
		}
		
		// build transition object
		Transition transition = new Transition(ActionType.CraftObject, 
				new Object[] {unit.id, sword2Blueprint });

		// perform craft
		TransitionResult transitionResult = actionEngine.process(player1,transition);

		// assertions - this move should have succeeded
		assertEquals("CraftResult", transitionResult.errorType, TransitionError.NoError);
		Assert.assertTrue("CraftResultObject", 
				unit.carriedObjects.keySet().iterator().next().getType() == CraftedObjectType.SWORD);
		assertEquals("CraftResultResources", unit.carriedResources.get(ResourceType.IRON), new Integer(0));
		assertEquals("CraftResultResources", unit.carriedResources.get(ResourceType.STONE), new Integer(0));
		assertEquals("CraftResultResources", unit.carriedResources.get(ResourceType.WOOD), new Integer(0));
	}

	// ======================================================================================== //
	// ================================ ATTACK ACTION TESTS =================================== //
	//@Test
	public void testAttack() {
		Point2i posAttacker = new Point2i(15, 15);
		Point2i posDefender = new Point2i(16, 15);
		
		HashMap<ResourceType, Integer> unitResources = new HashMap<ResourceType, Integer>();
		
		// find blueprints for attack and defense
		Blueprint swordBlueprint = null;
		Blueprint armourBlueprint = null;
		
		for (Blueprint bp : GamePolicy.blueprints) {
			if (bp.getType() == CraftedObjectType.SWORD && bp.getLevel() == 2) {
				swordBlueprint = bp;
				break;
			}
		}
		
		for (Blueprint bp : GamePolicy.blueprints) {
			if (bp.getType() == CraftedObjectType.ARMOUR && bp.getLevel() == 2) {
				armourBlueprint = bp;
				break;
			}
		}
		
		SwordObject sword = new SwordObject(swordBlueprint);
		ArmourObject armour = new ArmourObject(armourBlueprint);
		
		
		// setup units
		UnitState unitAttacker = setupUnit(0, player1.id, posAttacker, GamePolicy.initialUnitMaxLife, unitResources);
		unitAttacker.carriedObjects.put(sword, 1);
		unitAttacker.equipedSword = sword;
		
		UnitState unitDefender = setupUnit(1, player2.id, posDefender, GamePolicy.initialUnitMaxLife / 2, unitResources);
		unitDefender.carriedObjects.put(armour, 1);
		//unitDefender.equipedArmour = armour;
		
		// add unit to player
		player1.units.add(unitAttacker);
		player2.units.add(unitDefender);
		
		// build transition object
		Transition transition = new Transition(ActionType.Attack, new Object[] {
				unitAttacker.id, unitDefender.playerID, unitDefender.id, unitAttacker.energy / 2});
		
		// perform attack
		TransitionResult transitionResult = actionEngine.process(player1, transition);
		
		// interpret
		assertEquals("AttackResult", TransitionError.NoError, transitionResult.errorType);
		assertEquals("attackerLife", GamePolicy.initialUnitMaxLife / 2, unitAttacker.energy);
		
		int defenderLife = GamePolicy.initialUnitMaxLife / 2 - (int)Math.round((GamePolicy.initialUnitMaxLife / 2) * (1 + sword.getAttack() / 100.0));  
		
		assertEquals("defenderLife", defenderLife, player2.units.get(0).life, 0.001);
		assertEquals("attackerKills", 1, player1.getKills());
	}
	
	//@Test
	public void testAttackWithRetaliate() {
		Point2i posAttacker = new Point2i(15, 15);
		Point2i posDefender = new Point2i(16, 15);
		
		HashMap<ResourceType, Integer> unitResources = new HashMap<ResourceType, Integer>();
		
		// find blueprints for attack and defense
		Blueprint swordBlueprint = null;
		Blueprint armourBlueprint = null;
		
		for (Blueprint bp : GamePolicy.blueprints) {
			if (bp.getType() == CraftedObjectType.SWORD && bp.getLevel() == 2) {
				swordBlueprint = bp;
				break;
			}
		}
		
		for (Blueprint bp : GamePolicy.blueprints) {
			if (bp.getType() == CraftedObjectType.ARMOUR && bp.getLevel() == 2) {
				armourBlueprint = bp;
				break;
			}
		}
		
		SwordObject sword = new SwordObject(swordBlueprint);
		ArmourObject armour = new ArmourObject(armourBlueprint);
		
		// setup units
		UnitState unitAttacker = setupUnit(0, player1.id, posAttacker, GamePolicy.initialUnitMaxLife, unitResources);
		unitAttacker.carriedObjects.put(sword, 1);
		unitAttacker.equipedSword = sword;
		
		UnitState unitDefender = setupUnit(1, player2.id, posDefender, GamePolicy.initialUnitMaxLife, unitResources);
		unitDefender.carriedObjects.put(armour, 1);
		unitDefender.equipedArmour = armour;
		
		
		// add unit to player
		player1.units.add(unitAttacker);
		player2.units.add(unitDefender);
		
		// build attack transition object
		Transition transitionAttack = new Transition(ActionType.Attack, new Object[] {
				unitAttacker.id, unitDefender.playerID, unitDefender.id, unitAttacker.energy / 2});
		
		// build prepare transition object
		Transition transitionDefend = new Transition(ActionType.Prepare,
				new Object[] { unitDefender.id, unitDefender.energy / 2, unitDefender.energy / 4});
		
		// perform prepare
		TransitionResult transitionResultDefend = actionEngine.process(player2, transitionDefend);
		
		// perform attack
		TransitionResult transitionResultAttack = actionEngine.process(player1, transitionAttack);
		
		// interpret
		assertEquals("AttackResult", TransitionError.NoError, transitionResultAttack.errorType);
		assertEquals("attackerLife", 
				GamePolicy.initialUnitMaxLife - (GamePolicy.initialUnitMaxLife / 2), 
				unitAttacker.life);
		
		int defenderLife = GamePolicy.initialUnitMaxLife - (int)Math.round((GamePolicy.initialUnitMaxLife / 2) * (1 + sword.getAttack() / 100.0) * (1 - armour.getDefence() / 100.0));  
		
		assertEquals("defenderLife", defenderLife, player2.units.get(0).life, 0.001);
		assertEquals("attackerKills", 0, player1.getKills());
	}
	
	// ======================================================================================== //
	// ================================ UPGRADE ACTION TESTS ================================== //
	
	//@Test
	public void testUpgrade() {
		Point2i pos= new Point2i(15, 15);
		HashMap<ResourceType, Integer> unitResources = new HashMap<ResourceType, Integer>();
		
		// find blueprints for attack
		Blueprint swordBlueprint = null;
		
		for (Blueprint bp : GamePolicy.blueprints) {
			if (bp.getType() == CraftedObjectType.SWORD && bp.getLevel() == 2) {
				swordBlueprint = bp;
				break;
			}
		}
		
		// add blueprint to player
		player1.availableBlueprints.add(swordBlueprint);
		player1.gold = swordBlueprint.getUpgradeCost();
		
		SwordObject sword = new SwordObject(swordBlueprint);
		
		
		// setup units
		UnitState unit = setupUnit(0, player1.id, pos, GamePolicy.initialUnitMaxLife, unitResources);
		unit.carriedObjects.put(sword, 1);
		unit.equipedSword = sword;
		
		
		// add unit to player
		player1.units.add(unit);
		
		
		// build transition object
		Transition transition = new Transition(ActionType.Upgrade, new Object[] {
				unit.id, swordBlueprint});
		
		// perform attack
		TransitionResult transitionResult = actionEngine.process(player1, transition);
		
		// interpret
		assertEquals("UpgradeResult", TransitionError.NoError, transitionResult.errorType);
		assertEquals("UpgradeGoldRemaining", 0, player1.gold);
		
		// get a level 3 blueprint to see if its in the player's list
		Blueprint bp3 = null;
		for (Blueprint bp : GamePolicy.blueprints) {
			if (bp.getType() == CraftedObjectType.SWORD && bp.getLevel() == 3) {
				bp3 = bp;
				break;
			}
		}
		
		System.out.println(player1.availableBlueprints);
		
		Assert.assertTrue("UpgradeBlueprintExists", player1.availableBlueprints.contains(bp3));
	}
	
	
	// ======================================================================================== //
	// ================================= EQUIP ACTION TESTS =================================== //
	
	//@Test
	public void testEquip() {
		Point2i pos= new Point2i(15, 15);
		HashMap<ResourceType, Integer> unitResources = new HashMap<ResourceType, Integer>();
		
		// find blueprints for attack
		Blueprint swordBlueprint = null;
		
		for (Blueprint bp : GamePolicy.blueprints) {
			if (bp.getType() == CraftedObjectType.SWORD && bp.getLevel() == 2) {
				swordBlueprint = bp;
				break;
			}
		}
		
		// add blueprint to player
		player1.availableBlueprints.add(swordBlueprint);
		player1.gold = swordBlueprint.getUpgradeCost();
		
		SwordObject sword = new SwordObject(swordBlueprint);
		
		
		// setup units
		UnitState unit = setupUnit(0, player1.id, pos, GamePolicy.initialUnitMaxLife, unitResources);
		unit.carriedObjects.put(sword, 1);
		
		// add unit to player
		player1.units.add(unit);
		
		
		// build transition object
		Transition transition = new Transition(ActionType.Equip, new Object[] {
				unit.id, sword});
		
		// perform attack
		TransitionResult transitionResult = actionEngine.process(player1, transition);
		
		// interpret
		assertEquals("EquipResult", TransitionError.NoError, transitionResult.errorType);
		assertEquals("EquipWeapon", sword, unit.equipedSword);
	}
	
	@Test
	public void testPlaceTower() {
		Point2i pos= new Point2i(14, 15);
		HashMap<ResourceType, Integer> unitResources = new HashMap<ResourceType, Integer>();
		
		// find blueprints for tower
		Blueprint towerBlueprint = null;
		
		for (Blueprint bp : GamePolicy.blueprints) {
			if (bp.getType() == CraftedObjectType.TOWER && bp.getLevel() == 2) {
				towerBlueprint = bp;
				break;
			}
		}
		
		// add blueprint to player
		player1.availableBlueprints.add(towerBlueprint);
		
		// setup unit resources
		unitResources.put(ResourceType.STONE, 60);
		unitResources.put(ResourceType.IRON, 40);
		unitResources.put(ResourceType.WOOD, 40);
		
		// setup units
		UnitState unit = setupUnit(0, player1.id, pos, GamePolicy.initialUnitMaxLife, unitResources);
		
		// add unit to player
		player1.units.add(unit);
		
		
		// build transition object
		Transition transition = new Transition(ActionType.PlaceTower, new Object[] {
				unit.id, towerBlueprint});
		
		// perform attack
		TransitionResult transitionResult = actionEngine.process(player1, transition);
		
		// interpret
		assertEquals("PlaceTowerResult", TransitionError.NoError, transitionResult.errorType);
		Assert.assertTrue("PlaceTowerExists", player1.availableTowers.contains(towerBlueprint.craft(player1.id, unit.pos)));
		assertEquals("RemainingResources", new Integer(0), unit.carriedResources.get(ResourceType.WOOD));
		Assert.assertTrue("PlaceTowerExists", game.map.cells[unit.pos.y][unit.pos.x].strategicObject.equals(towerBlueprint.craft(player1.id, unit.pos)));
	}
	
	@Test
	public void testPlaceTrap() {
		Point2i pos= new Point2i(16, 15);
		HashMap<ResourceType, Integer> unitResources = new HashMap<ResourceType, Integer>();
		
		// find blueprints for tower
		Blueprint trapBlueprint = null;
		
		for (Blueprint bp : GamePolicy.blueprints) {
			if (bp.getType() == CraftedObjectType.TRAP && bp.getLevel() == 2) {
				trapBlueprint = bp;
				break;
			}
		}
		
		// add blueprint to player
		player1.availableBlueprints.add(trapBlueprint);
		
		// setup unit resources
		unitResources.put(ResourceType.LEATHER, 20);
		unitResources.put(ResourceType.IRON, 20);
		unitResources.put(ResourceType.WOOD, 10);
		
		// setup units
		UnitState unit = setupUnit(0, player1.id, pos, GamePolicy.initialUnitMaxLife, unitResources);
		
		// add unit to player
		player1.units.add(unit);
		
		
		// build transition object
		Transition transition = new Transition(ActionType.PlaceTrap, new Object[] {
				unit.id, trapBlueprint});
		
		// perform attack
		TransitionResult transitionResult = actionEngine.process(player1, transition);
		
		// interpret
		assertEquals("PlaceTrapResult", TransitionError.NoError, transitionResult.errorType);
		Assert.assertTrue("PlaceTrapExists", player1.availableTraps.contains(trapBlueprint.craft(player1.id, unit.pos)));
		assertEquals("RemainingResources", new Integer(0), unit.carriedResources.get(ResourceType.WOOD));
		assertEquals("RemainingResources", new Integer(0), unit.carriedResources.get(ResourceType.IRON));
		assertEquals("RemainingResources", new Integer(0), unit.carriedResources.get(ResourceType.LEATHER));
		Assert.assertTrue("PlaceTrapExists", game.map.cells[unit.pos.y][unit.pos.x].strategicObject.equals(trapBlueprint.craft(player1.id, unit.pos)));
	}
}
