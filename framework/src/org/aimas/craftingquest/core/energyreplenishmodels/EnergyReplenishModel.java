package org.aimas.craftingquest.core.energyreplenishmodels;

public abstract class EnergyReplenishModel {
	protected ReplenishType type;

	protected EnergyReplenishModel(ReplenishType type) {
		this.type = type;
	}

	/**
	 * Use this to replenish unit energy at start of turn.
	 *
	 * @param nowEnergy the current energy of the unit, before replenishing
	 * @param maxEnergy the maximum energy of the unit, as defined by scenario policy
	 * @return the new energy of the unit.
	 */
	public abstract int replenishEnergy(int nowEnergy, int maxEnergy);

	public static EnergyReplenishModel getInstance(ReplenishType type) {
		switch (type) {
			case FullReplenish:
				return new FullEnergyReplenishModel(type);
			default: return null;
		}
	}

	public ReplenishType getType() {
		return type;
	}
}
