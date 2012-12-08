/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.aimas.craftingquest.core;

import java.io.Serializable;

/**
 * 
 * @author Razvan
 */
@SuppressWarnings("serial")
public class Event implements Serializable {

	public enum EventType {
		Nothing, Initialization, NewRound, EndRound, GameEnd
	}

	public EventType type;

	public Event(EventType type) {
		this.type = type;
	}
}
