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

	/* game */
	private PlayerState state;
	private IPlayerHooks player;
	private PausableThreadPoolExecutor ptpe;

	/* public */
	public Client0(String host, int port, String serverName, long secret) throws Exception {
		this.secret = secret;
		ptpe = new PausableThreadPoolExecutor();
		
		//server = Remote.getItem("//localhost:1198/" + "CraftingQuest");
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
		Logger2.log("cln", "onEvent", "");
		switch (event.type) {
		case Nothing:
			break;
		case NewRound:
			if (player != null) {
				ptpe.execute(new Runnable() {

					public void run() {
						player.beginRound();
					}
				});
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
		this.player.initGame();
	}
	
	public PlayerState getPlayerState() {
		return state;
	}

	private synchronized PlayerState doGenericAction(Transition action) {
		try {
			action.secret = secret;
			this.state = (PlayerState) Remote.invoke(server, "process", action);
			return getPlayerState();
		} catch (Exception ex) {
			Logger.getLogger(Client0.class.getName()).log(Level.SEVERE, null,ex);
		}
		return null; // means grail error
	}

	void log(String where, String what) {
		System.out.println("["
				+ (System.currentTimeMillis() - Test.getStartTime())
				+ "][Interface][" + where + "][" + what + "]");
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
