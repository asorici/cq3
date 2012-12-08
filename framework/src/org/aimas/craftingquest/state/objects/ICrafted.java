package org.aimas.craftingquest.state.objects;

import org.aimas.craftingquest.state.Blueprint;

public interface ICrafted {
	int getLevel();
	CraftedObjectType getType();
	Blueprint getBlueprint();
}
