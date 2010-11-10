package org.aimas.craftingquest.state;

import java.util.List;

import org.aimas.craftingquest.core.GamePolicy;

public class Merchant extends StrategicResource {
	
	private List<Blueprint> blueprints;
	
	public Merchant(Point2i pos, List<Blueprint> blueprints) {
		super(GamePolicy.StrategicResourceType.Merchant, pos);
		this.blueprints = blueprints;
	}
	
	public List<Blueprint> getBlueprints() {
		return blueprints;
	}
	
}
