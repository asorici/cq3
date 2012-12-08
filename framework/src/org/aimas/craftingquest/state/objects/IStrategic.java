package org.aimas.craftingquest.state.objects;

import org.aimas.craftingquest.state.Point2i;

public interface IStrategic {
	Point2i getPosition();
	int getPlayerID();
	CraftedObjectType getType();
}
