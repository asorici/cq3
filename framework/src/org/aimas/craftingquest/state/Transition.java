package org.aimas.craftingquest.state;

import java.io.Serializable;
import java.util.Arrays;

public class Transition implements Serializable {

	// public enum ActionType implements Serializable{
	public enum ActionType {
		Nothing, PlayerReady, EndTurn, Move, Dig, ScanLand, PickupResources, PickupObjects, DropResources, DropObjects,  
		RequestBlueprints, BuyBlueprint, CraftObject, SellObject, PlaceTower, Trade, Attack, TuneScanner
	}

	// static int count;
	public int id;
	public ActionType operator; // OPERATOR
	public Object[] operands; // OPERANDS
	public long secret;

	public Transition(ActionType operator, Object[] operands) {
		// synchronized (Transition.class) {
		// this.id = count++;
		// }
		this.operator = operator;
		this.operands = operands;
	}

}
