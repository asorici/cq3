package org.aimas.craftingquest.state;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Transition implements Serializable {

	// public enum ActionType implements Serializable{
	public enum ActionType implements Serializable {
		Nothing, PlayerReady, EndTurn, RequestState, Move, Dig, ScanLand, PickupResources, PickupObjects, DropResources, DropObjects,  
		RequestBlueprints, BuyBlueprint, CraftObject, SellObject, PlaceTower, Trade, Attack, TuneScanner, Equip
	}

	// static int count;
	public int id;
	public ActionType operator; // OPERATOR
	public Object[] operands; // OPERANDS
	public long secret;

	public Transition(ActionType operator, Object[] operands) {
		this.operator = operator;
		this.operands = operands;
	}

}
