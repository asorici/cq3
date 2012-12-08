package org.aimas.craftingquest.core.actions;

import org.aimas.craftingquest.state.GameState;
import org.aimas.craftingquest.state.PlayerState;
import org.aimas.craftingquest.state.Transition;
import org.aimas.craftingquest.state.Transition.ActionType;
import org.aimas.craftingquest.state.TransitionResult;
import org.aimas.craftingquest.state.UnitState;

public abstract class Action {
	protected ActionType type;
	protected UnitState playerUnit;
	
	protected Action(ActionType type) {
		this.type = type;
	}
	
	public TransitionResult doAction(GameState game, PlayerState player, Transition transition) {
		Integer unitID = (Integer)transition.operands[0];
		
		playerUnit = null;
		for (UnitState u : player.units) {
			if (u.id == unitID && u.playerID == player.id) {
				playerUnit = u;
				break;
			}
		}
		
		if (playerUnit == null) {
			TransitionResult res = new TransitionResult(transition.id);
			res.errorType = TransitionResult.TransitionError.UnitIdError;
			return res;
		}
		else {
			if (validOperands(transition)) {
				return handle(game, player, transition);
			}
			else {
				TransitionResult res = new TransitionResult(transition.id);
				res.errorType = TransitionResult.TransitionError.OperandError;
				return res;
			}
		}
	}
	
	protected abstract TransitionResult handle(GameState game, PlayerState player, Transition transition);
	protected abstract boolean validOperands(Transition transition);
	
	public static Action getInstance(ActionType type) {
		if (type == null) {
			return null;
		}
		
		switch(type) {
			case Move:
				return new MoveAction(type);
			case Dig:
				return new DigAction(type);
			case ScanLand:
				return new ScanLandAction(type);
			case PickupResources:
				return new PickupResourcesAction(type);
			case PickupObjects:
				return new PickupObjectsAction(type);
			case DropResources:
				return new DropResourcesAction(type);
			case DropObjects:
				return new DropObjectAction(type);
			case PlaceTrap:
				return new PlaceTrapAction(type);
			case CraftObject:
				return new CraftObjectAction(type);
			case Equip:
				return new EquipAction(type);
			case SellObject:
				return new SellObjectAction(type);
			case PlaceTower:
				return new PlaceTowerAction(type);
			case BuyBlueprint:
				return new BuyBlueprintAction(type);
			
			default: return null;
		}
	}
	
	public ActionType getType() {
		return type;
	}
}
