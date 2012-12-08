package org.aimas.craftingquest.core.energyreplenishmodels;

public class FullEnergyReplenishModel extends EnergyReplenishModel {

	public FullEnergyReplenishModel(ReplenishType type) {
		super(type);
	}

	public int replenishEnergy(int nowEnergy, int maxEnergy) {
		return maxEnergy;
	}
}
