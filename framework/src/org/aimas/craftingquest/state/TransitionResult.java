package org.aimas.craftingquest.state;

import java.io.Serializable;

/**
 * 
 * @author Razvan
 */
public class TransitionResult implements Serializable {

	public enum TransitionError {
		NoError, MoveError, TerrainError, ObstacleError, NoEnergyError, PickupError, BuildError, CraftingError, ResourceMissingError, NoCreditError, BuyRequestError, SellRequestError, GuardError
	};

	public int id; // egal cu cel trimis
	public TransitionError errorType;
	public String errorReason;

	// public Transition attemptedTransition;

	public TransitionResult(int id) {
		this.id = id;
	}

	/*
	 * public TransitionResult(int id, Transition transition) { this.id = id;
	 * this.attemptedTransition = transition; }
	 */
	
	public boolean valid() {
		if (errorType == TransitionError.NoError) {
			return true;
		}
		
		return false;
	}

}
