package org.aimas.craftingquest.user;

import java.util.HashMap;

import org.aimas.craftingquest.state.Blueprint;
import org.aimas.craftingquest.state.CraftedObject;
import org.aimas.craftingquest.state.PlayerState;
import org.aimas.craftingquest.state.Point2i;
import org.aimas.craftingquest.state.UnitState;
import org.aimas.craftingquest.state.CraftedObject.BasicResourceType;


public interface IPlayerActions {

    public void addArtificialIntelligence(IPlayerHooks usercode);

    //public Scenario getScenario();

    public PlayerState getPlayerState();

    public PlayerState move(UnitState unit, Point2i newPosition);

    public PlayerState dig(UnitState unit);
    
    public PlayerState scan(UnitState unit);
    
    public PlayerState pickupResources(UnitState unit, HashMap<BasicResourceType, Integer> desiredResources);
    
    public PlayerState pickupObjects(UnitState unit, HashMap<CraftedObject, Integer> desiredObjects);
    
    public PlayerState dropResources(UnitState unit, HashMap<BasicResourceType, Integer> unwantedResources);
    
    public PlayerState dropObjects(UnitState unit, HashMap<CraftedObject, Integer> unwantedObjects);
    
    public PlayerState craftObject(UnitState unit, CraftedObject target, 
			HashMap<CraftedObject, Integer> usedObjects, HashMap<BasicResourceType, Integer> usedResources);
    
    public PlayerState sellObject(UnitState unit, CraftedObject craftedObject, Integer quantity);
    
    public PlayerState placeTower(UnitState unit);
    
    public PlayerState buyBlueprint(UnitState unit, Blueprint blueprint);
}
