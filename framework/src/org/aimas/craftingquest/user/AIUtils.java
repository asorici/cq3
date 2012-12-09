package org.aimas.craftingquest.user;

import java.util.ArrayList;
import java.util.List;

import org.aimas.craftingquest.core.pathfinding.Astar;
import org.aimas.craftingquest.core.pathfinding.IHeuristic;
import org.aimas.craftingquest.core.pathfinding.IPath;
import org.aimas.craftingquest.core.pathfinding.IPathMap;
import org.aimas.craftingquest.state.CellState;
import org.aimas.craftingquest.state.CellState.CellType;
import org.aimas.craftingquest.state.PlayerState;
import org.aimas.craftingquest.state.Point2i;
import org.aimas.craftingquest.state.UnitState;
import org.aimas.craftingquest.state.objects.Tower;
import org.aimas.craftingquest.state.objects.TrapObject;

public class AIUtils {
	/**
	 * Uses the Astar algorithm to compute a sequence of steps required to get 
	 * from the <code><b>from</b></code> position to the <code><b>to</b></code> position, given the 
	 * known world, using the supplied heuristic and performing a search of <code><b>maxSearchDist</b></code> depth.
	 * @param from  the start position
	 * @param to  the end position
	 * @param unit  the unit performing the search
	 * @param knownWorld  an array the size of game map (mapHeight x mapWidth) where the unknown cells
	 * 		  have a <b>null</b> value 
	 * @param heuristic	 the heuristic function used as part of the Astar algorithm
	 * @param maxSearchDist  the maximum distance to search from the starting point
	 * @return  the path to be followed to the destination given as a sequence of {@link Point2i} positions
	 */
	public static IPath findPath(Point2i from, Point2i to, UnitState unit, 
			CellState[][] knownWorld, IHeuristic heuristic, int maxSearchDist) {
		// create mapping from the CellState[][] array of cells known to the player to the
		// cost-function based interface that AStar expects 
		
		final CellState[][] fKnownWorld = knownWorld;
		final UnitState fUnit = unit;
		
		IPathMap knownMap = new IPathMap() {
			@Override
			public int getWidth() {
				return fKnownWorld[0].length;
			}
			
			@Override
			public int getHeight() {
				return fKnownWorld.length;
			}
			
			@Override
			public float getCellCost(Point2i cell, UnitState unit) {
				int x = cell.x;
				int y = cell.y;
				
				if (fKnownWorld[y][x] == null) {
					// if you don't know it you can't plan for it
					return IPathMap.INF;
				}
				else {
					if (fKnownWorld[y][x].type == CellType.Rock) {
						// if it's rocky terrain you can't go through
						return IPathMap.INF;
					}
					else if (fKnownWorld[y][x].strategicObject != null) {
						// if there's a strategic object (e.g. tower) in the cell you can't go through 
						return IPathMap.INF;
					}
					else {
						// otherwise we return the unit cost of moving to another cell
						return 1;
					}
				}
			}
		};
		
		Astar planner = new Astar(heuristic, maxSearchDist);
		return planner.computePath(from, to, knownMap, unit);
	}
	
	/**
	 * Returns the list of destroyed towers, given as a difference between your view of the player state and 
	 * the server's view on the <code><b>availableTowers</b></code> field from the player state. 
	 * Each time an action is performed, the server will return its view of the player state in the response.
	 * <p>Use this method at the beginning of your round to find out if any of your towers have been destroyed since
	 * you last performed an action.</p>
	 * @param clientPlayerState		your view of your current player state
	 * @param serverPlayerState		the server's update of the same player state
	 * @return  the list of towers that have been destroyed (they no longer appear in the server's view of the
	 * <code><b>availableTowers</b></code> field from the player state) 
	 */
	public static List<Tower> getDestroyedTowers(PlayerState clientPlayerState, PlayerState serverPlayerState) {
		List<Tower> clientPlayerTowers = clientPlayerState.availableTowers;
		List<Tower> serverPlayerTowers = serverPlayerState.availableTowers;
		
		List<Tower> diffList = new ArrayList<Tower>();
		
		for (Tower t: clientPlayerTowers) {
			if (!serverPlayerTowers.contains(t)) {
				diffList.add(t);
			}
		}
		
		return diffList;
	}
	
	
	/**
	 * Returns the list of destroyed traps, given as a difference between your view of the player state and 
	 * the server's view on the <code><b>availableTraps</b></code> field from the player state. 
	 * Each time an action is performed, the server will return its view of the player state in the response.
	 * <p>Use this method at the beginning of your round to find out if any of your traps have been destroyed since
	 * you last performed an action.</p>
	 * @param clientPlayerState		your view of your current player state
	 * @param serverPlayerState		the server's update of the same player state
	 * @return  the list of traps that have been destroyed (they no longer appear in the server's view of the
	 * <code><b>availableTowers</b></code> field from the player state) 
	 */
	public static List<TrapObject> getDestroyedTraps(PlayerState clientPlayerState, PlayerState serverPlayerState) {
		List<TrapObject> clientPlayerTraps = clientPlayerState.availableTraps;
		List<TrapObject> serverPlayerTraps = serverPlayerState.availableTraps;
		
		List<TrapObject> diffList = new ArrayList<TrapObject>();
		
		for (TrapObject t: clientPlayerTraps) {
			if (!serverPlayerTraps.contains(t)) {
				diffList.add(t);
			}
		}
		
		return diffList;
	}
}
