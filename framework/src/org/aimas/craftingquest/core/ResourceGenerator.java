package org.aimas.craftingquest.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.aimas.craftingquest.state.Blueprint;
import org.aimas.craftingquest.state.CraftedObject;
import org.aimas.craftingquest.state.MapState;
import org.aimas.craftingquest.state.Point2i;
import org.aimas.craftingquest.state.CellState.CellType;
import org.aimas.craftingquest.state.CraftedObject.BasicResourceType;
import org.aimas.craftingquest.state.CraftedObject.ObjectType;

public class ResourceGenerator {
	private static Random randGen = new Random();
	private static int maxRequiredResPerObject = GamePolicy.unitEnergy / 5;
	private static int maxRequiredCraftedObjects = GamePolicy.unitEnergy / 12;
	
	private static List<PlacementRegion> topPlacementRegions = new ArrayList<PlacementRegion>();
	private static List<PlacementRegion> bottomPlacementRegions = new ArrayList<PlacementRegion>();
	
	public static List<Blueprint> generateBlueprints(HashMap<BasicResourceType, Integer> resourceAmountsByType) {
		List<Blueprint> blueprints = new ArrayList<Blueprint>();
		int nrObjectTypes = ObjectType.values().length;
		int complexObjectCount = nrObjectTypes / 4;						// 1/4 of objects will be built from other objects
		
		List<ObjectType> complexObjects = new ArrayList<ObjectType>();
		List<ObjectType> simpleObjects = new ArrayList<ObjectType>();
		
		ArrayList<ObjectType> auxObjectTypes = new ArrayList<ObjectType>();
		for (int i = 0; i < nrObjectTypes; i++) {
			auxObjectTypes.add(ObjectType.values()[i]);
		}
		
		for (int i = 0; i < complexObjectCount; i++) {
			int idx = randGen.nextInt(auxObjectTypes.size());
			ObjectType o = auxObjectTypes.remove(idx);
			complexObjects.add(o);
		}
		
		// now auxObjectTypes contains the simple objects
		simpleObjects.addAll(auxObjectTypes);
		
		List<Blueprint> simpleBlueprints = generateSimpleObjectBlueprints(simpleObjects, resourceAmountsByType);
		List<Blueprint> complexBlueprints = generateComplexObjectBlueprints(complexObjects, simpleBlueprints);
		
		blueprints.addAll(simpleBlueprints);
		blueprints.addAll(complexBlueprints);
		
		return blueprints;
	}
	
	
	private static List<Blueprint> generateSimpleObjectBlueprints(List<ObjectType> simpleObjects, HashMap<BasicResourceType, Integer> resourceAmountsByType) {
		ArrayList<Blueprint> simpleBlueprints = new ArrayList<Blueprint>();
		
		for (int i = 0; i < simpleObjects.size(); i++) {
			ObjectType objType = simpleObjects.get(i);
			int nrIngredients = randGen.nextInt(3) + 2;
			ArrayList<BasicResourceType> alternative1 = getRandomIngredients(nrIngredients);
			
			nrIngredients = randGen.nextInt(3) + 2;
			ArrayList<BasicResourceType> alternative2 = getRandomIngredients(nrIngredients);
			
			// create alternatives  
			HashMap<BasicResourceType, Integer> recipe1 = buildRecipe(alternative1, resourceAmountsByType);
			HashMap<BasicResourceType, Integer> recipe2 = buildRecipe(alternative2, resourceAmountsByType);
			List<HashMap<BasicResourceType, Integer>> recipeAlternatives = new ArrayList<HashMap<BasicResourceType,Integer>>();
			recipeAlternatives.add(recipe1);
			recipeAlternatives.add(recipe2);
			
			int value = computeSimpleValue(alternative1, alternative2, resourceAmountsByType);
			CraftedObject craftedObj = new CraftedObject(objType, value, null, recipeAlternatives);
			Blueprint bp = new Blueprint(value / 4, craftedObj);
			
			simpleBlueprints.add(bp);
		}
		
		return simpleBlueprints;
	}
	

	private static int computeSimpleValue(ArrayList<BasicResourceType> alternative1, 
			ArrayList<BasicResourceType> alternative2,
			HashMap<BasicResourceType, Integer> resourceAmountsByType) {
		
		int maxAmount = 0;
		for (int i = 0; i < BasicResourceType.values().length; i++) {
			BasicResourceType rt = BasicResourceType.values()[i];
			int amount = resourceAmountsByType.get(rt);
			if (amount > maxAmount) {
				maxAmount = amount;
			}
		}
		
		double valueAlternative1 = GamePolicy.baseObjectValue, valueAlternative2 = GamePolicy.baseObjectValue;
		double weight1 = 0, weight2 = 0;
		
		for (BasicResourceType rt : alternative1) {
			int amount = resourceAmountsByType.get(rt);
			double w = (double)maxAmount / (double)amount; 
			double valInc = w * (double)GamePolicy.valueIncrement;
			
			weight1 += w;
			valueAlternative1 += valInc;
		}
		
		for (BasicResourceType rt : alternative2) {
			int amount = resourceAmountsByType.get(rt);
			double w = (double)maxAmount / (double)amount; 
			double valInc = w * (double)GamePolicy.valueIncrement;
			
			weight2 += w; 
			valueAlternative2 += valInc;
		}
		
		return (int)Math.ceil((valueAlternative1 * weight1 + valueAlternative2 * weight2) / (weight1 + weight2));
	}


	private static HashMap<BasicResourceType, Integer> buildRecipe(ArrayList<BasicResourceType> alternative, HashMap<BasicResourceType, Integer> resourceAmountsByType) {
		HashMap<BasicResourceType, Integer> recipe = new HashMap<BasicResourceType, Integer>();
		
		int totalAmount = 0;
		for (BasicResourceType rt : alternative) {
			totalAmount += resourceAmountsByType.get(rt);
		}
		
		for (int i = 0; i < alternative.size(); i++) {
			BasicResourceType rt = alternative.get(i);
			int rtAmount = maxRequiredResPerObject * resourceAmountsByType.get(rt) / totalAmount;
			if (rtAmount == 0) {
				rtAmount = 2;
			}
			
			recipe.put(rt, rtAmount);
		}
		
		return recipe;
	}
	
	
	private static ArrayList<BasicResourceType> getRandomIngredients(int nrIngredients) {
		ArrayList<Integer> auxList = new ArrayList<Integer>();
		ArrayList<BasicResourceType> resList = new ArrayList<BasicResourceType>();
		for (int i = 0; i < BasicResourceType.values().length; i++) {
			auxList.add(i);
		}
		
		for (int i = 0; i < nrIngredients; i++) {
			int index = randGen.nextInt(auxList.size());
			resList.add(BasicResourceType.values()[auxList.remove(index)]);
		}
		
		return resList;
	}

	
	private static List<Blueprint> generateComplexObjectBlueprints(List<ObjectType> complexObjects, List<Blueprint> simpleBlueprints) {
		ArrayList<Blueprint> complexBlueprints = new ArrayList<Blueprint>();
		int maxValue = 0;
		for (Blueprint bp : simpleBlueprints) {
			if (bp.getDescribedObject().getValue() > maxValue) {
				maxValue = bp.getDescribedObject().getValue();
			}
		}
		
		for (ObjectType objType : complexObjects) {
			int nrIngredients = randGen.nextInt(2) + 2;
			ArrayList<CraftedObject> alternative1 = getRandomObjects(nrIngredients, simpleBlueprints);
			
			nrIngredients = randGen.nextInt(2) + 2;
			ArrayList<CraftedObject> alternative2 = getRandomObjects(nrIngredients, simpleBlueprints);
			
			// create alternatives  
			HashMap<CraftedObject, Integer> recipe1 = buildObjectRecipe(alternative1);
			HashMap<CraftedObject, Integer> recipe2 = buildObjectRecipe(alternative2);
			List<HashMap<CraftedObject, Integer>> objectRecipeAlternatives = new ArrayList<HashMap<CraftedObject,Integer>>();
			objectRecipeAlternatives.add(recipe1);
			objectRecipeAlternatives.add(recipe2);
			
			int value = computeComplexValue(recipe1, recipe2);		// compute crafted object value
			CraftedObject craftedObj = new CraftedObject(objType, value, objectRecipeAlternatives, null);
			
			// set blueprint value to the same price as the most expensive ingredient object
			int maxBlueprintValue = 0;
			for (CraftedObject obj : recipe1.keySet()) {
				if (obj.getValue() > maxBlueprintValue) {
					maxBlueprintValue = obj.getValue();
				}
			}
			
			for (CraftedObject obj : recipe2.keySet()) {
				if (obj.getValue() > maxBlueprintValue) {
					maxBlueprintValue = obj.getValue();
				}
			}
			
			Blueprint bp = new Blueprint(maxBlueprintValue, craftedObj);
			
			complexBlueprints.add(bp);
		}
		
		return complexBlueprints;
	}

	
	private static int computeComplexValue(HashMap<CraftedObject, Integer> recipe1, HashMap<CraftedObject, Integer> recipe2) {
		int baseValue = GamePolicy.baseObjectValue;
		int value1 = 0, value2 = 0;
		int totalValue = 0;
		
		for (CraftedObject obj : recipe1.keySet()) {
			value1 += (2 * baseValue + obj.getValue()) * recipe1.get(obj);
		}
		
		for (CraftedObject obj : recipe2.keySet()) {
			value2 += (2 * baseValue + obj.getValue()) * recipe2.get(obj);
		}
		
		totalValue = (value1 + value2) / 2;		// total value is the average of the value for the two recipes
		return totalValue;
	}


	private static HashMap<CraftedObject, Integer> buildObjectRecipe(ArrayList<CraftedObject> alternative) {
		HashMap<CraftedObject, Integer> recipe = new HashMap<CraftedObject, Integer>();
		
		int totalAmount = 0;
		for (CraftedObject obj : alternative) {
			totalAmount += obj.getValue();
		}
		
		for (int i = 0; i < alternative.size(); i++) {
			CraftedObject obj = alternative.get(i);
			int rtAmount = maxRequiredCraftedObjects * obj.getValue() / totalAmount;
			if (rtAmount == 0) {
				rtAmount = 2;
			}
			
			recipe.put(obj, rtAmount);
		}
		
		return recipe;
	}


	private static ArrayList<CraftedObject> getRandomObjects(int nrIngredients, List<Blueprint> simpleBlueprints) {
		ArrayList<CraftedObject> objList = new ArrayList<CraftedObject>();
		
		ArrayList<Integer> auxList = new ArrayList<Integer>();
		for (int i = 0; i < simpleBlueprints.size(); i++) {
			auxList.add(i);
		}
		
		for (int i = 0; i < nrIngredients; i++) {
			int index = randGen.nextInt(auxList.size());
			objList.add(simpleBlueprints.get(index).getDescribedObject());
		}
		
		return objList;
	}

	
	private static void computePlacementRegions() {
		int maxRadius = 1;
		
		if (GamePolicy.mapsize.x <= 50) {
			maxRadius = 1;
		}
		else {
			if (GamePolicy.mapsize.x <= 80) {
				maxRadius = 2;
			}
			else {
				maxRadius = 3;
			}
		}
		
		int maxSize = 2 * maxRadius + 3;
		
		for (int x = 0; x < (GamePolicy.mapsize.x / maxSize); x++) {
			for (int y = 0; y < (GamePolicy.mapsize.y / maxSize); y++) {
				int xmin = x * maxSize;
				int xmax = (x + 1) * maxSize;
				
				int ymin = y * maxSize;
				int ymax = (y + 1) * maxSize;
				
				PlacementRegion pr = new PlacementRegion(xmin, xmax, ymin, ymax);
				
				if (pr.ycenter < GamePolicy.mapsize.x - pr.xcenter) {
					topPlacementRegions.add(pr);
				}
				else {
					bottomPlacementRegions.add(pr);
				}
			}
		}
	}
	
	public static HashMap<BasicResourceType, Integer> placeResources(MapState map) {
		int[] quant = generateRandomResourceQuantityDistribution(GamePolicy.maxResourceSpots);
		computePlacementRegions();
		HashMap<BasicResourceType, Integer> resourceAmountsByType = new HashMap<BasicResourceType, Integer>();
		
		for (int i = 0; i < quant.length; i++) {
			BasicResourceType resType = GamePolicy.getResTypeByOrdinal(i);
			System.out.println(resType.name() + " has ordinal: " + resType.ordinal() + " and associated quantum of: " + quant[i]);
			
			for (int k = 0; k < quant[i]; k++) {
				int resTypeAmount = placeResourceType(resType, map);
				Integer existing = resourceAmountsByType.get(resType);
				if (existing == null) {
					resourceAmountsByType.put(resType, resTypeAmount);
				}
				else {
					resourceAmountsByType.put(resType, resTypeAmount + existing);
				}
			}
		}
		
		return resourceAmountsByType;
	}
	
	
	private static int placeResourceType(BasicResourceType resType, MapState map) {
		int placedResCount = 0;
		int radius = 1;
		
		if (GamePolicy.mapsize.x <= 50) {
			radius = 1;
		}
		else {
			if (GamePolicy.mapsize.x <= 80) {
				radius = 2;
			}
			else {
				radius = 3;
			}
		}
		
		VeinDistribution vd = new VeinDistribution(radius + 2, radius, 10);
		DiscDistribution dd = new DiscDistribution(radius, 10);
		
		int option = randGen.nextInt(2);
		if (option == 0) {		// build a disc distribution
			placedResCount += discResourcePlacement(resType, map, radius, dd);
		}
		else {					// build a vein distribution
			placedResCount += veinResourcePlacement(resType, map, radius, vd);
		}
		
		return placedResCount;
	}
	
	
	private static Point2i getPlacementPosition(List<PlacementRegion> placementRegions) {
		
		// first search for empty placement region
		ArrayList<PlacementRegion> emptyRegions = new ArrayList<PlacementRegion>();
		for (PlacementRegion pr : placementRegions) {
			if (pr.containedComponents == 0) {
				emptyRegions.add(pr);
			}
		}
		
		PlacementRegion chosenPlacement = null;
		if (emptyRegions.isEmpty()) {
			int index = randGen.nextInt(placementRegions.size());
			chosenPlacement = placementRegions.get(index);
			chosenPlacement.containedComponents++;
		}
		else {
			int index = randGen.nextInt(emptyRegions.size());
			chosenPlacement = emptyRegions.get(index);
			chosenPlacement.containedComponents++;
		}
		
		int lowx = chosenPlacement.xmin, highx = chosenPlacement.xmax;
		int posx = lowx + randGen.nextInt(highx - lowx);
		if (posx < 4) { posx = 4; }
		if (posx > GamePolicy.mapsize.x - 5) { posx = GamePolicy.mapsize.x - 5; }
		
		int lowy = chosenPlacement.ymin, highy = chosenPlacement.ymax;
		int posy = lowy + randGen.nextInt(highy - lowy);
		if (posy < 4) { posy = 4; }
		if (posy > GamePolicy.mapsize.y - 5) { posy = GamePolicy.mapsize.y - 5; }
		
		return new Point2i(posx, posy);
	}
	
	
	private static int discResourcePlacement(BasicResourceType resType, MapState map, int radius, DiscDistribution dd) {
		int placedResCt = 0;
		
		Point2i topPlacementPos = getPlacementPosition(topPlacementRegions);
		// Point2i bottomPlacementPos = getPlacementPosition(bottomPlacementRegions);
		
		int posy = topPlacementPos.y;
		int posx = topPlacementPos.x;
		placedResCt += placeDiscRes(posx, posy, resType, map, dd);
		
		int auxPosy = GamePolicy.mapsize.y - 1 - posy;		
		int auxPosx = GamePolicy.mapsize.x - 1 - posx;
		int displacement = 7;
		
		int lowx = auxPosx - displacement;
		int lowy = auxPosy - displacement;
		int diagDiff = GamePolicy.mapsize.x - 1 - (lowx + lowy);
		if (diagDiff >= 0) {
			lowy += diagDiff;
		}
		
		posx = lowx + randGen.nextInt(2 * displacement);
		posy = lowy + randGen.nextInt(2 * displacement);
		if (posx < 4) { posx = 4; }
		if (posx > GamePolicy.mapsize.x - 5) { posx = GamePolicy.mapsize.x - 5; }
		if (posy < 4) { posy = 4; }
		if (posy > GamePolicy.mapsize.y - 5) { posy = GamePolicy.mapsize.y - 5; }
		
		placedResCt += placeDiscRes(posx, posy, resType, map, dd);
		
		return placedResCt;
	}
	
	private static int placeDiscRes(int posx, int posy, BasicResourceType resType, MapState map, DiscDistribution dd) {
		int placedResCt = 0;
		int[][] ddist = dd.getDisc();
		int radius = dd.radius;
		
		for (int y = posy - radius; y <= posy + radius; y++) {
			for (int x = posx - radius; x <= posx + radius; x++) {
				int amount = ddist[y + radius - posy][x + radius - posx];
				placedResCt += placeResourceInCell(resType, map, x, y, amount);
			}
		}
		
		return placedResCt;
	}
	
	private static int veinResourcePlacement(BasicResourceType resType, MapState map, int radius, VeinDistribution vd) {
		int placedResCt = 0;
		
		Point2i topPlacementPos = getPlacementPosition(topPlacementRegions);
		// Point2i bottomPlacementPos = getPlacementPosition(bottomPlacementRegions);
		int option = randGen.nextInt(4);
		
		int posy = topPlacementPos.y;
		int posx = topPlacementPos.x;
		placedResCt += placeVeinRes(posx, posy, resType, map, vd, option);
		
		int auxPosy = GamePolicy.mapsize.y - 1 - posy;		
		int auxPosx = GamePolicy.mapsize.x - 1 - posx;
		int displacement = 7;
		
		int lowx = auxPosx - displacement;
		int lowy = auxPosy - displacement;
		int diagDiff = GamePolicy.mapsize.x - 1 - (lowx + lowy);
		if (diagDiff >= 0) {
			lowy += diagDiff;
		}
		
		posx = lowx + randGen.nextInt(2 * displacement);
		posy = lowy + randGen.nextInt(2 * displacement);
		if (posx < 4) { posx = 4; }
		if (posx > GamePolicy.mapsize.x - 5) { posx = GamePolicy.mapsize.x - 5; }
		if (posy < 4) { posy = 4; }
		if (posy > GamePolicy.mapsize.y - 5) { posy = GamePolicy.mapsize.y - 5; }
		
		placedResCt += placeVeinRes(posx, posy, resType, map, vd, option);
		
		return placedResCt;
	}
	
	private static int placeVeinRes(int posx, int posy, BasicResourceType resType, MapState map, VeinDistribution vd, int option) {
		int placedResCt = 0;
		int[][] vdist = vd.getVein();
		int lengthRadius = vd.lengthRadius;
		int widthRadius = vd.widthRadius;
		
		switch(option){						//			 x	y	
		case 0:								// directia (1, 0)
			for (int y = posy - widthRadius; y <= posy + widthRadius; y++) {
				for (int x = posx - lengthRadius; x <= posx + lengthRadius; x++) {
					int amount = vdist[y + widthRadius - posy][x + lengthRadius - posx];
					placedResCt += placeResourceInCell(resType, map, x, y, amount);
				}
			}
			break;
		case 1:								// directia (0, 1)
			for (int y = posy - lengthRadius; y <= posy + lengthRadius; y++) {
				for (int x = posx - widthRadius; x <= posx + widthRadius; x++) {
					int amount = vdist[x + widthRadius - posx][y + lengthRadius - posy];
					placedResCt += placeResourceInCell(resType, map, x, y, amount);
				}
			}
			break;
		case 2:								// directia (1, 1)
			for (int d = 0; d <= widthRadius; d++) {
				for (int j = 0; j < 2 * lengthRadius + 1; j++) {
					int amount1 = vdist[widthRadius - d][j];
					int y1 = posy - lengthRadius + j;
					int x1 = posx - lengthRadius + d + j;
					placedResCt += placeResourceInCell(resType, map, x1, y1, amount1);
					
					int amount2 = vdist[widthRadius + d][j];
					int y2 = posy - lengthRadius + d + j;
					int x2 = posx - lengthRadius + j; 
					if (d != 0) {
						placedResCt += placeResourceInCell(resType, map, x2, y2, amount2);
					}
				}
			}
			
			break;
		case 3:								// directia (1, -1)
			for (int d = 0; d <= widthRadius; d++) {
				for (int j = 0; j < 2 * lengthRadius + 1; j++) {
					int amount1 = vdist[widthRadius - d][j];
					int y1 = posy + lengthRadius - j - d;
					int x1 = posx - lengthRadius + j;
					placedResCt += placeResourceInCell(resType, map, x1, y1, amount1);
					
					int amount2 = vdist[widthRadius + d][j];
					int y2 = posy + lengthRadius - j;
					int x2 = posx - lengthRadius + j + d; 
					if (d != 0) {
						placedResCt += placeResourceInCell(resType, map, x2, y2, amount2);
					}
				}
			}
			
			break;
		}
		
		return placedResCt;
	}
	
	private static int placeResourceInCell(BasicResourceType resType, MapState map, int x, int y, int amount) {
		int placedAmount = 0;
		
		if (x >= 0 && x < GamePolicy.mapsize.x && y >= 0 && y < GamePolicy.mapsize.y ) {
			// place resources in upper left quadrant
			int xx = x, yy = y;
			if (map.cells[yy][xx] != null && map.cells[yy][xx].type != CellType.Rock && map.cells[yy][xx].strategicResource == null) {
				// if nothing occupies this cell then put resources in it
				Integer contents = map.cells[yy][xx].resources.get(resType);
				if (contents == null) {
					map.cells[yy][xx].resources.put(resType, amount);
				}
				else {
					map.cells[yy][xx].resources.put(resType, amount + contents);
				}
				
				placedAmount += amount;
			}
			
		}
		
		return placedAmount;
	}
	
	@SuppressWarnings("unused")
	private static int[] generateUniformResourceQuantityDistribution(int maxSpots) {
		int nrTypes = BasicResourceType.values().length;
		int[] quant = new int[nrTypes];
		
		for (int i = 0; i < nrTypes; i++) {
			quant[i] = maxSpots;
		}
		
		return quant;
	}
	
	private static int[] generateRandomResourceQuantityDistribution(int maxSpots) {
		int nrTypes = BasicResourceType.values().length;
		int[] quant = new int[nrTypes];
		
		Random rand = new Random();
		int max = 0;
		
		for (int i = 0; i < nrTypes; i++) {
			int val = rand.nextInt(nrTypes) + 1;		// number between 1 and nrTypes
			quant[i] = val;
			if (val > max) {
				max = val;
			}
		}
		
		for (int i = 0; i < nrTypes; i++) {
			quant[i] = quant[i] * maxSpots / max;
			if (quant[i] == 0) {
				quant[i] = 1;
			}
		}
		
		return quant;
	}
	
	public static void main(String[] args) {
		VeinDistribution vd = new VeinDistribution(8, 4, 10);
		DiscDistribution dd = new DiscDistribution(5, 10);
		int [][] vdist = vd.getVein();
		int [][] ddist = dd.getDisc();
		
		for (int i = 0; i < vdist.length; i++) {
			for (int j = 0; j < vdist[i].length; j++) {
				System.out.printf("%3d", vdist[i][j]);
			}
			System.out.println();
		}
		
		System.out.println();
		System.out.println();
		
		for (int i = 0; i < ddist.length; i++) {
			for (int j = 0; j < ddist[i].length; j++) {
				System.out.printf("%3d", ddist[i][j]);
			}
			System.out.println();
		}
		
		System.out.println();
		System.out.println();
		
		
		TerrainGenerator terrainGen = new TerrainGenerator();
		MapState map = terrainGen.hardcoded1();
		
		HashMap<BasicResourceType, Integer> amountsPerType = placeResources(map);
		List<Blueprint> blueprints = generateBlueprints(amountsPerType);
		
		System.out.println();
		System.out.println();
		
		System.out.println("Resource quantities on the map");
		for (int i = 0; i < BasicResourceType.values().length; i++) {
			BasicResourceType resType = GamePolicy.getResTypeByOrdinal(i);
			System.out.println(resType.name() + ": " + amountsPerType.get(resType));
		}
		
		System.out.println();
		System.out.println();
		
		for (int i = 0; i < blueprints.size(); i++) {
			Blueprint bp = blueprints.get(i);
			CraftedObject obj = bp.getDescribedObject();
			int bpvalue = bp.getValue();
			
			System.out.println("==== Blueprint for object of type: " + obj.getType().name() + "====");
			System.out.println("	- blueprint value: " + bpvalue);
			System.out.println("	- object value: " + obj.getValue());
			System.out.println("	- construction alternatives: ");
			
			List<HashMap<BasicResourceType, Integer>> requiredResources = obj.getRequiredResources();
			List<HashMap<CraftedObject, Integer>> requiredObjects = obj.getRequiredObjects();
			
			if (requiredResources != null) {
				for (HashMap<BasicResourceType, Integer> alternative : requiredResources) {
					System.out.println("		:: ALTERNATIVE");
					
					Iterator<BasicResourceType> resIt = alternative.keySet().iterator();
					while(resIt.hasNext()) {
						BasicResourceType rt = resIt.next();
						System.out.println("			- " + rt.name() + ": " + alternative.get(rt));
					}
					
					System.out.println();
				}
			}
			
			if (requiredObjects != null) {
				for (HashMap<CraftedObject, Integer> alternative : requiredObjects) {
					System.out.println("		:: ALTERNATIVE");
					
					Iterator<CraftedObject> objIt = alternative.keySet().iterator();
					while(objIt.hasNext()) {
						CraftedObject o = objIt.next();
						System.out.println("			- " + o.getType() + ": " + alternative.get(o));
					}
					
					System.out.println();
				}
			}
			
			System.out.println();
		}
	}

}

class VeinDistribution {
	int lengthRadius;
	int widthRadius;
	private int[][] dist; 
	
	public int peak;
	
	public VeinDistribution() {
	}
	
	public VeinDistribution (int lengthRadius, int widthRadius, int peak) {
		this.lengthRadius = lengthRadius;
		this.widthRadius = widthRadius;
		this.peak = peak;
	
		dist = generateVein();
	}
	
	private int[][] generateVein() {
		int n = 2 * widthRadius + 1;
		int m = 2 * lengthRadius + 1;
		
		int [][] distribution = new int[n][m];
		
		for (int k = widthRadius; k >= 0; k--) {
			int diff = widthRadius - k;
			int val = getGaussian(diff, widthRadius / 2.0, peak);
			
			for (int j = 0; j < m; j++) {
				distribution[k][j] = val;
				distribution[n - 1 - k][j] = val;
			}
		}
		
		return distribution;
	}
	
	public int[][] getVein() {
		return dist;
	}
	
	private int getGaussian(double diff, double sigma, double peak) {		// diff is x - miu
		double gaussVal = 0;
		double fraction = peak;
		double exponent = - (diff * diff) / (2.0 * sigma * sigma);
		
		gaussVal = fraction * Math.pow(Math.E, exponent);
		//System.out.println(gaussVal);
		return (int)Math.ceil(gaussVal);
	}
}

class DiscDistribution {
	int radius;
	int peak;
	private int[][] dist; 
	
	public DiscDistribution() {
	}
	
	public DiscDistribution(int radius, int peak) {
		this.radius = radius;
		this.peak = peak;
		dist = generateDisc();
	}
	
	public int[][] getDisc() {
		return dist;
	}
	
	private int[][] generateDisc() {
		int n = 2 * radius + 1;
		int [][] distribution = new int[n][n];
		
		for (int k = radius; k >= 0; k-- ) {
			int diff = radius - k;
			int val = getGaussian(diff, radius / 2.0, peak);
			
			
			for (int i = radius - diff; i <= radius + diff; i++) {
				// generate lines
				distribution[k][i] = val;
				distribution[n - 1 -k][i] = val;
				
				// generate columns
				distribution[i][k] = val;
				distribution[i][n - 1 - k] = val;
			}
			
		}
		
		return distribution;
	}
	
	private int getGaussian(double diff, double sigma, double peak) {		// diff is x - miu
		double gaussVal = 0;
		double fraction = peak;
		double exponent = - (diff * diff) / (2.0 * sigma * sigma);
		
		gaussVal = fraction * Math.pow(Math.E, exponent);
		return (int)Math.ceil(gaussVal);
	}
}

class PlacementRegion {
	int xmin, xmax;
	int ymin, ymax;
	
	int xcenter, ycenter;
	int containedComponents = 0;
	
	public PlacementRegion(int xmin, int xmax, int ymin, int ymax) {
		this.xmin = xmin;
		this.xmax = xmax;
		this.ymin = ymin;
		this.ymax = ymax;
		
		xcenter = (xmin + xmax) / 2;
		ycenter = (ymin + ymax) / 2;
	}
}