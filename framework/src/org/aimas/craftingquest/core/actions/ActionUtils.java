package org.aimas.craftingquest.core.actions;

import java.util.Iterator;

import org.aimas.craftingquest.state.Blueprint;
import org.aimas.craftingquest.state.UnitState;
import org.aimas.craftingquest.state.resources.ResourceType;

public class ActionUtils {
	protected static boolean checkCraftingRequirements(UnitState playerUnit,
			Blueprint blueprint) {

		/**
		 *  Check if resources are available
		 */
		Iterator<ResourceType> resIt = blueprint.getResourcesNeeded().keySet().iterator();
		while (resIt.hasNext()) {
			ResourceType rt = resIt.next(); 
			Integer required = blueprint.getResourcesNeeded().get(rt);
			Integer available = playerUnit.carriedResources.get(rt);
			if (available == null || available < required) return false;
		}
		/**
		 *  Everythin ok, Consume resources
		 */
		resIt = blueprint.getResourcesNeeded().keySet().iterator();
		while (resIt.hasNext()) {
			ResourceType rt = resIt.next(); 
			Integer required = blueprint.getResourcesNeeded().get(rt);
			Integer available = playerUnit.carriedResources.get(rt);
			playerUnit.carriedResources.put(rt, available-required);
		}
		return true;
	}

}
