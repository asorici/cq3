package org.aimas.craftingquest.state;

import java.io.Serializable;

public class Blueprint implements Serializable {
	int value;
	CraftedObject describedObject;
	
	public Blueprint () {
	}
	
	public Blueprint (int value, CraftedObject describedObject) {
		this.value = value;
		this.describedObject = describedObject;
	}

	public CraftedObject getDescribedObject() {
		return describedObject;
	}

	public int getValue() {
		return value;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Blueprint)) {
			return false;
		}
		
		final Blueprint other = (Blueprint)obj;
		if (describedObject.getType() != other.describedObject.getType()) {
			return false;
		}
		
		return true;
	}
}
