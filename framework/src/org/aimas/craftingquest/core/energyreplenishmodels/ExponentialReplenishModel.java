package org.aimas.craftingquest.core.energyreplenishmodels;

public class ExponentialReplenishModel extends EnergyReplenishModel {

	public ExponentialReplenishModel(ReplenishType type) {
		super(type);
	}

	public int replenishEnergy(int nowEnergy, int maxEnergy) {
		final float ratio = 0.6f;
		return (int)Math.round(ratio * nowEnergy + (1 - ratio) * maxEnergy);
	}
}
