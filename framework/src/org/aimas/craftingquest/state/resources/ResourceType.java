package org.aimas.craftingquest.state.resources;

import java.io.Serializable;

import org.aimas.craftingquest.core.GamePolicy;
import org.aimas.craftingquest.state.ICarriable;

public enum ResourceType implements Serializable, ICarriable {
	//TODO de adăugat în GamePolicy
	WOOD (GamePolicy.woodWeight), 
	STONE (GamePolicy.stoneWeight), 
	LEATHER (GamePolicy.leatherWeight), 
	BRONZE (GamePolicy.bronzeWeight), 
	IRON (GamePolicy.ironWeight), 
	TITANIUM (GamePolicy.titaniumWeight), 
	GOLD (0);
	
	private int weight;
	public final static int size = 7;
	
	private ResourceType(int weight)
	{
		this.weight = weight;
	}

	@Override
	public int getWeight() {
		return weight;
	}
}
