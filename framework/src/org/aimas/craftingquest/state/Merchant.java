package org.aimas.craftingquest.state;

import java.util.ArrayList;
import java.util.List;

import org.aimas.craftingquest.core.GamePolicy;

public class Merchant extends StrategicResource {
	
	private List<Blueprint> blueprints = new ArrayList<Blueprint>();
	
	public Merchant(Point2i pos) {
		super(GamePolicy.StrategicResourceType.Merchant, pos);
	}
	
	public List<Blueprint> getBlueprints() {
		return blueprints;
	}
	
}
