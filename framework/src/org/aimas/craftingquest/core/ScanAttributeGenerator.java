package org.aimas.craftingquest.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.aimas.craftingquest.core.GamePolicy.BasicResourceType;
import org.aimas.craftingquest.state.CellState;
import org.aimas.craftingquest.state.MapState;
import org.aimas.craftingquest.state.ResourceAttributes;

public class ScanAttributeGenerator {
	private static HashMap<Integer, int[]> attrValues;
	private static Random randomGen = new Random();
	
	static {
		attrValues = new HashMap<Integer, int[]>();
		for (int type = 0; type < GamePolicy.scanAttributeCount; type++) {
			int nrVals = randomGen.nextInt(2) + 4;		// generates 4 or 5
			int[] vals = new int[nrVals];
			for (int i = 0; i < vals.length; i++) {
				vals[i] = i + 1;
			}
			
			attrValues.put(type, vals);
		}
	}
	
	public static void setupScanAttributes(MapState map) {
		AttributeNode root = buildAttributeTree();
		HashMap<BasicResourceType, List<ResourceAttributes>> generatedResAttributes = generateResourceAttributes(root);
		
		for (int y = 0; y < GamePolicy.mapsize.y; y++) {
			for (int x = 0; x < GamePolicy.mapsize.x; x++) {
				CellState cell = map.cells[y][x];
				Iterator<BasicResourceType> resIt = cell.resources.keySet().iterator();
				while(resIt.hasNext()) {
					BasicResourceType resType = resIt.next();
					List<ResourceAttributes> resAttributeList = generatedResAttributes.get(resType);
					int index = randomGen.nextInt(resAttributeList.size());
					
					ResourceAttributes resAttr = resAttributeList.get(index);
					cell.scanAttributes.put(resType, resAttr);
				}
			}
		}
	}
	
	private static AttributeNode buildAttributeTree() {
		int targetClassNumber = GamePolicy.BasicResourceType.values().length;
		List<AttributeNode> nodeList = new ArrayList<AttributeNode>();
		
		int attrNum = randomGen.nextInt(GamePolicy.scanAttributeCount);
		AttributeNode root = new AttributeNode(null, attrNum, attrValues.get(attrNum));
		nodeList.add(root);
		
		generateTree(nodeList, 0, targetClassNumber);
		fixDimension(root, targetClassNumber);
		
		return root;
	}

	
	private static void generateTree(List<AttributeNode> nodeList, int nodesGenerated, int targetClassNum) {
		
		while (!nodeList.isEmpty()) {
			AttributeNode expandingNode = nodeList.remove(0);
			List<Integer> availableAttributes = new ArrayList<Integer>();
			
			for (int i = 0; i < GamePolicy.scanAttributeCount; i++) {
				availableAttributes.add(new Integer(i));
			}
			
			for (AttributeNode p = expandingNode; p != null; p = p.parent) {
				Integer attrNum = new Integer(p.attrNum);
				availableAttributes.remove(attrNum);
			}
			
			if (availableAttributes.size() == 0) {		// if no attribute types are available any more
				continue;								// skip this attribute
			}
			
			if (nodesGenerated == 0) {		// if no possible class nodes exist yet
				for (int i = 0; i < expandingNode.values.length; i++) {
					
					int auxIndex = randomGen.nextInt(availableAttributes.size());
					int attrNum = availableAttributes.get(auxIndex);
					
					AttributeNode nextNode = new AttributeNode(expandingNode, attrNum, attrValues.get(attrNum));
					expandingNode.childList.put(expandingNode.values[i], nextNode);
					nodeList.add(nextNode);
					
					// increase number of generated nodes
					nodesGenerated++;
				}
			}
			else {	// we have at least one node - generate next nodes with lesser probability
				if (nodesGenerated >= targetClassNum) {
					continue;
				}
				
				double con = (double)nodesGenerated / (double) targetClassNum;
				double pro = 1.0 - con;
				
				for (int i = 0; i < expandingNode.values.length; i++) {
					double randVal = randomGen.nextDouble();
					
					if (randVal < pro) {
						int auxIndex = randomGen.nextInt(availableAttributes.size());
						int attrNum = availableAttributes.get(auxIndex);
						
						AttributeNode nextNode = new AttributeNode(expandingNode, attrNum, attrValues.get(attrNum));
						expandingNode.childList.put(expandingNode.values[i], nextNode);
						nodeList.add(nextNode);
					}
				}
				
				if (expandingNode.childList.size() == 1) {	// if just one element was added, no splitting
					expandingNode.childList.clear();		// is being done at this node, so delete it
					nodeList.remove(nodeList.size() - 1);   // from the list
				}
				
				if (expandingNode.childList.size() >= 2) {
					nodesGenerated += expandingNode.childList.size() - 1;
					// -1 because we eliminate the intermediary parent node
				}
			}
		}
		
	}
	
	private static void fixDimension(AttributeNode root, int targetClassNum) {
		List<AttributeNode> classNodeList = new ArrayList<AttributeNode>();
		getClassNodeList(classNodeList, root);
		int generatedNodes = classNodeList.size();
		
		int dimDiff = targetClassNum - generatedNodes;
		if (dimDiff == 0) {
			return;
		}
		System.out.println("Initial diff is:" + dimDiff);
		
		for (int i = generatedNodes - 1; i >= 0; i--) {
			AttributeNode leafNode = classNodeList.get(i);
			int nrAdjusted = adjustTree(leafNode.parent, dimDiff);
			
			if (nrAdjusted == dimDiff) {
				break;
			}
			else {
				dimDiff -= nrAdjusted;
			}
		}
	}
	
	private static int adjustTree(AttributeNode node, int dimDiff) {
		int added = 0;
		
		for (int k = 0; k < node.values.length; k++) {
			if (added == dimDiff) {
				break;
			}
			
			AttributeNode child = node.childList.get(node.values[k]);
			if (child == null) {
				List<Integer> availableAttributes = new ArrayList<Integer>();
				
				for (int i = 0; i < GamePolicy.scanAttributeCount; i++) {
					availableAttributes.add(new Integer(i));
				}
				
				for (AttributeNode p = node; p != null; p = p.parent) {
					Integer attrNum = new Integer(p.attrNum);
					availableAttributes.remove(attrNum);
				}
				
				int auxIndex = randomGen.nextInt(availableAttributes.size());
				int attrNum = availableAttributes.get(auxIndex);
				
				AttributeNode nextNode = new AttributeNode(node, attrNum, attrValues.get(attrNum));
				node.childList.put(node.values[k], nextNode);
				added++;
			}
		}
		
		if (added < dimDiff && node.parent != null) {
			return added + adjustTree(node.parent, dimDiff - added);
		}
		else {
			return added;
		}
	}
	
	private static void getClassNodeList(List<AttributeNode> classNodeList, AttributeNode node) {
		if (node.childList.isEmpty()) {
			classNodeList.add(node);
		}
		else {
			for (int i = 0; i < node.values.length; i++) {
				AttributeNode child = node.childList.get(node.values[i]);
				if (child != null) {
					getClassNodeList(classNodeList, child);
				}
			}
		}
	}
	
	private static HashMap<BasicResourceType, List<ResourceAttributes>> generateResourceAttributes(AttributeNode root) {
		List<AttributeNode> classNodeList = new ArrayList<AttributeNode>();
		getClassNodeList(classNodeList, root);
		BasicResourceType[] resTypes = BasicResourceType.values();
		List<BasicResourceType> resourceTypes = new ArrayList<BasicResourceType>();
		for (int i = 0; i < resTypes.length; i++) {
			resourceTypes.add(resTypes[i]);
		}
		
		HashMap<BasicResourceType, List<ResourceAttributes>> generatedResAttributes = new HashMap<BasicResourceType, List<ResourceAttributes>>();
		walkAndGenerate(root, resourceTypes, new HashMap<Integer, Integer>(), generatedResAttributes);
		
		return generatedResAttributes;
	}
	
	private static void walkAndGenerate(AttributeNode node, List<BasicResourceType> resTypes, 
		HashMap<Integer, Integer> attrPath, HashMap<BasicResourceType, List<ResourceAttributes>> generated) {
		
		if (!node.childList.isEmpty()) {
			for (int i = 0; i < node.values.length; i++) {
				AttributeNode child = node.childList.get(node.values[i]);
				if (child != null) {
					attrPath.put(node.attrNum, node.values[i]);
					walkAndGenerate(child, resTypes, attrPath, generated);
					attrPath.remove(node.attrNum);
				}
			}
		}
		else {
			List<Integer> remainingAttrTypes = new ArrayList<Integer>();
			for (int i = 0; i < GamePolicy.scanAttributeCount; i++) {	// each i is an attribute type
				if (attrPath.get(i) == null) {		// if it's not used on the current path
					remainingAttrTypes.add(i);
				}
			}
			
			Random randGen = new Random();
			int index = randGen.nextInt(resTypes.size());
			BasicResourceType selectedRes = resTypes.remove(index);
			List<ResourceAttributes> generatedResAttributes = new ArrayList<ResourceAttributes>();
			generated.put(selectedRes, generatedResAttributes);
			
			fill(selectedRes, attrPath, remainingAttrTypes, generatedResAttributes);
		}
	}
	
	private static void fill(BasicResourceType resType, HashMap<Integer, Integer> attrPath, List<Integer> remainingAttrTypes, List<ResourceAttributes> generatedResAttr) {
		if (!remainingAttrTypes.isEmpty()) {
			int attrNum = remainingAttrTypes.remove(0);		// remove as from top of stack
			int[] vals = attrValues.get(attrNum);
			for (int i = 0; i < vals.length; i++) {
				attrPath.put(attrNum, vals[i]);		// add to path
				fill(resType, attrPath, remainingAttrTypes, generatedResAttr);
				attrPath.remove(attrNum);			// remove it on the way back
			}
			remainingAttrTypes.add(0, attrNum);				// push it back in for next recursive call
		}
		else {
			int[] completeAttrVals = new int[GamePolicy.scanAttributeCount];
			//System.out.println(attrPath);
			for (int type = 0; type < GamePolicy.scanAttributeCount; type++) {
				completeAttrVals[type] = attrPath.get(type);
			}
			
			ResourceAttributes resAttr = new ResourceAttributes(resType, completeAttrVals);
			generatedResAttr.add(resAttr);
		}
	}
	
	
	public static void main(String[] args) {
		AttributeNode root = buildAttributeTree();
		//root.printTree();
		
		HashMap<BasicResourceType, List<ResourceAttributes>> generatedResAttributes = generateResourceAttributes(root);
		List<ResourceAttributes> resList = generatedResAttributes.get(BasicResourceType.R3);
		
		for(ResourceAttributes ra : resList) {
			System.out.print(ra.resourceType.name() + ": ");
			int [] attributes = ra.attributeValues;
			for (int type = 0; type < attributes.length; type++) {
				System.out.print(type + "(" + attributes[type] + ") ");
			}
			
			System.out.println();
		}
	}

	
}

class AttributeNode {
	static int counter = 0;
	int id = counter++;
	
	int attrNum;
	int nrValues;
	int[] values;
	AttributeNode parent = null;
	HashMap<Integer, AttributeNode> childList;
	
	public AttributeNode(AttributeNode parent, int attrNum, int[] values) {
		this.parent = parent;
		this.attrNum = attrNum;
		this.values = values;
		this.nrValues = values.length;
		childList = new HashMap<Integer, AttributeNode>();
	}
	
	public void setValues(int[] vals) {
		values = vals;
	}
	
	public int[] getValues() {
		return values;
	}
	
	public int getNrValues() {
		return nrValues;
	}
	
	public void addBranch(Integer val, AttributeNode child) {
		childList.put(val, child);
	}
	
	public AttributeNode getChildNode(Integer val) {
		return childList.get(val);
	}
	
	public boolean isLeaf() {
		return childList.isEmpty();
	}
	
	public int getAttrNum() {
		return attrNum;
	}

	public void setAttrNum(int attrNum) {
		this.attrNum = attrNum;
	}
	
	public void printTree() {
		System.out.println("id(" + id + ") attrNum: " + attrNum);
		System.out.println("Child list: ");
		for (int i = 0; i < values.length; i++) {
			AttributeNode child = childList.get(values[i]);
			if (child == null) {
				System.out.print(values[i] + ": null ");
			}
			else {
				System.out.print(values[i] + ":(" + child.getId() + ")" + child.attrNum + " ");
			}
		}
		
		System.out.println();
		System.out.println();
		
		for (int i = 0; i < values.length; i++) {
			AttributeNode child = childList.get(values[i]);
			if (child != null) {
				child.printTree();
			}
		}
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}