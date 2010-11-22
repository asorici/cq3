package org.aimas.craftingquest.state;

import java.util.ArrayList;
import java.util.List;

/**
 * Describes the strategic resource from which players can buy blueprints.
 */
@SuppressWarnings("serial")
public class Merchant extends StrategicResource {
	
	/**
	 * the list of blueprints held by this merchant
	 */
	private List<Blueprint> blueprints = new ArrayList<Blueprint>();
	
	public Merchant(Point2i pos) {
		super(StrategicResourceType.Merchant, pos);
	}
	
	public List<Blueprint> getBlueprints() {
		return blueprints;
	}
	
	@Override
	public String toString() {
		String info = "";
		
		info += "Merchant\n";
		info += "    Has blueprints for objects:\n";
		for (Blueprint bp : blueprints) {
			info += "    " + bp + "\n";
		}
		
		return info;
	}
	
}
