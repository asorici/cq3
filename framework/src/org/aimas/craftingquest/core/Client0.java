package org.aimas.craftingquest.core;

import gnu.cajo.invoke.Remote;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.aimas.craftingquest.state.Blueprint;
import org.aimas.craftingquest.state.CraftedObject;
import org.aimas.craftingquest.state.PlayerState;
import org.aimas.craftingquest.state.Point2i;
import org.aimas.craftingquest.state.Transition;
import org.aimas.craftingquest.state.UnitState;
import org.aimas.craftingquest.state.CraftedObject.BasicResourceType;
import org.aimas.craftingquest.state.Transition.ActionType;
import org.aimas.craftingquest.user.IPlayerActions;
import org.aimas.craftingquest.user.IPlayerHooks;

public final class Client0 implements IClient, IPlayerActions {

	/* communication */
	private int id;
	private long secret;
	private Object server;
	private Remote client;

	/* action sync */
	private Boolean actionSync = new Boolean(true);
	
	/* game */
	private PlayerState state;
	private IPlayerHooks player;
	private PausableThreadPoolExecutor ptpe;

	/* public */
	public Client0(String host, int port, String serverName, long secret) throws Exception {
		this.secret = secret;
		ptpe = new PausableThreadPoolExecutor();
		
		server = Remote.getItem("//" + host + ":" + port + "/" + serverName);
		client = new Remote(this);
		
		id = (Integer) Remote.invoke(server, "addRemoteClient", client);
	}

	/* communication */
	@Override
	final public Long getSecret(int n) {
		return secret;
	}

	@Override
	final public void onEvent(Event event) {
		Logger2.log("cln", "onEvent", event.type.name());
		switch (event.type) {
		case Nothing:
			break;
		case Initialization:
			if (player != null) {
				ptpe.execute(new Runnable() {
					public void run() {
						player.initGame();
						player.initPlayer();
					}
				});
			}
		case NewRound:
			synchronized(actionSync) {
				actionSync = true;
				if (player != null) {
					ptpe.execute(new Runnable() {
						public void run() {
							player.beginRound();
						}
					});
				}
			}
			
			break;
		case EndRound:
			log("client", "end round");
			synchronized(actionSync) {
				actionSync = false;
			}
			
			break;
		case GameEnd:
			if (player != null) {
				ptpe.execute(new Runnable() {
					public void run() {
						player.finishGame();
					}
				});
			}
			break;
		default:
			break;
		}
	}

	/* interface */
	public void addArtificialIntelligence(IPlayerHooks usercode) {
		// assume object was initalized do a check
		this.player = usercode;
		this.state = doGenericAction(new Transition(Transition.ActionType.PlayerReady, null));
	}
	
	public PlayerState getPlayerState() {
		return state;
	}

	private synchronized PlayerState doGenericAction(Transition action) {
		try {
			synchronized(actionSync) {
				if (actionSync) {
					action.secret = secret;
					
					PlayerState responseState = (PlayerState) Remote.invoke(server, "process", action);
					if (responseState != null) {
						this.state = responseState;
					}
					
					return getPlayerState();
				}
				else {
					return null;
				}
			}
			
			
		} catch (Exception ex) {
			Logger.getLogger(Client0.class.getName()).log(Level.SEVERE, null,ex);
		}
		return null; // means grail error
	}

	void log(String where, String what) {
		System.out.println("[Interface][" + where + "][" + what + "]");
	}
	
	
	@Override
	public PlayerState move(UnitState unit, Point2i newPosition) {
		return doGenericAction(new Transition(Transition.ActionType.Move, new Object[] { unit.id, newPosition }));
	}

	
	@Override
	public PlayerState dig(UnitState unit) {
		log("dig", "send cmd");
		return doGenericAction(new Transition(ActionType.Dig, new Object[] {unit.id, unit.pos}));
	}
	
	
	@Override
	public PlayerState scan(UnitState unit) {
		log("scan", "send cmd");
		return doGenericAction(new Transition(ActionType.ScanLand, new Object[] {unit.id}));
	}
	
	
	@Override
	public PlayerState pickupResources(UnitState unit, HashMap<BasicResourceType, Integer> desiredResources) {
		log("pickup resources", "send cmd");
		return doGenericAction(new Transition(ActionType.PickupResources, new Object[] {unit.id, desiredResources}));
	}
	
	/**
	 * Allows the given unit to pick up the crafted objects contained in the current cell.
	 * Crafted objects can be picked up from a cell if a unit has previously dropped them there.
	 * <p> If successful, the {@link UnitState} of the unit performing the action will have
	 * the specified resources added to its <code>carriedObjects</code> field.</p>
	 * <p>In case of an error, the returned player state will not be different from the current one. 
	 * It will also contain a <code>TransitionResult</code> which gives the reason for the failure.</p>
	 * @param unit   the unit performing the operation
	 * @param desiredObjects   the desired crafted objects and their quantity
	 * @return the new player state or null if the player attempts to move outside his turn.
	 */
	@Override
	public PlayerState pickupObjects(UnitState unit, HashMap<CraftedObject, Integer> desiredObjects) {
		log("pickup objects", "send cmd");
		return doGenericAction(new Transition(ActionType.PickupObjects, new Object[] {unit.id, desiredObjects}));
	}
	
	
	@Override
	public PlayerState dropResources(UnitState unit, HashMap<BasicResourceType, Integer> unwantedResources) {
		log("drop", "send cmd");
		return doGenericAction(new Transition(ActionType.DropResources, new Object[] {unit.id, unwantedResources}));
	}
	
	
	@Override
	public PlayerState dropObjects(UnitState unit, HashMap<CraftedObject, Integer> unwantedObjects) {
		log("drop", "send cmd");
		return doGenericAction(new Transition(ActionType.DropObjects, new Object[] {unit.id, unwantedObjects}));
	}
	
	
	@Override
	public PlayerState craftObject(UnitState unit, CraftedObject target, 
			HashMap<CraftedObject, Integer> usedObjects, HashMap<BasicResourceType, Integer> usedResources) {
		log("craft", "send cmd");
		return doGenericAction(new Transition(ActionType.CraftObject, new Object[] {unit.id, target, usedObjects, usedResources}));
	}
	
	
	@Override
	public PlayerState sellObject(UnitState unit, CraftedObject craftedObject, Integer quantity) {
		log("sell", "send cmd");
		return doGenericAction(new Transition(ActionType.SellObject, new Object[] {unit.id, craftedObject, quantity}));
	}
	
	
	@Override
	public PlayerState placeTower(UnitState unit) {
		log("build tower", "send cmd");
		return doGenericAction(new Transition(ActionType.PlaceTower, new Object[] {unit.id}));
	}
	
	
	@Override
	public PlayerState buyBlueprint(UnitState unit, Blueprint blueprint) {
		log("buy blueprint", "send cmd");
		return doGenericAction(new Transition(ActionType.BuyBlueprint, new Object[] {unit.id, blueprint}));
	}
}
