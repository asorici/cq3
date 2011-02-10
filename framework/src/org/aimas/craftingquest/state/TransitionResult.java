package org.aimas.craftingquest.state;

import java.io.Serializable;

/**
 * 
 * @author Razvan
 */
@SuppressWarnings("serial")
public class TransitionResult implements Serializable {

	public enum TransitionError implements Serializable {
		NoError, OperandError, MoveError, TerrainError, ObstacleError, NoEnergyError, PickupError, BuildError, CraftingError, ResourceMissingError, NoCreditError, BuyRequestError, SellRequestError, GuardError
	};

	public int id; // egal cu cel trimis
	public TransitionError errorType;
	public String errorReason;
	
	public TransitionResult(int id) {
		this.id = id;
	}
	
	public boolean valid() {
		if (errorType == TransitionError.NoError) {
			return true;
		}
		
		return false;
	}
	
	@Override
	public String toString() {
		String info = "";
		info += "errorType: " + errorType.name() + "\n";
		info += "errorReason: " + errorReason + "\n";
		return info;
	}
}
