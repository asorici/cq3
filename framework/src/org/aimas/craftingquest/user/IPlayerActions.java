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

    public PlayerState getPlayerState();

    /**
	 * Allows a unit to move to an adjacent cell.
	 * <p>If the transition is successful, the returned player state will 
	 * have the specified unit moved to the required position. </p>
	 * <p>In case of an error, the returned player state will not be different from the current one. 
	 * It will also contain a <code>TransitionResult</code> which gives the reason for the failure.</p>
	 * @param unit   the unit to be moved
	 * @param newPosition - the movement target position
	 * @return the new player state or null if the player attempts to move outside his turn. 
	 */
    public PlayerState move(UnitState unit, Point2i newPosition);

    /**
	 * Allows the given unit to dig for resources in the current cell.
	 * <p> If successful, the {@link UnitState} of the unit performing the action
	 *  will have its <code>currentCellResources</code> updated with the resources 
	 *  contained in the currentCell. </p>
	 * <p>In case of an error, the returned player state will not be different from the current one. 
	 * It will also contain a <code>TransitionResult</code> which gives the reason for the failure.</p>
	 * @param unit   the unit performing the digging operation
	 * @return the new player state or null if the player attempts to move outside his turn.
	 */
    public PlayerState dig(UnitState unit);
    
    /**
	 * Allows the given unit to perform a scan operation which is centered at the current cell.
	 * <p> If successful, the {@link UnitState} of the unit performing the action will have
	 * its <code>scannedResourceAttributes</code> field updated with the scan attributes of the cells
	 * that fall within the scanners radius.</p>
	 * <p>In case of an error, the returned player state will not be different from the current one. 
	 * It will also contain a <code>TransitionResult</code> which gives the reason for the failure.</p>
	 * @param unit   the unit performing the scan
	 * @return the new player state or null if the player attempts to move outside his turn.
	 */
    public PlayerState scan(UnitState unit);
    
    /**
	 * Allows the given unit to pick up the resources contained in the current cell.
	 * <p> If successful, the {@link UnitState} of the unit performing the action will have
	 * the specified resources added to its <code>carriedResources</code> field.</p>
	 * <p>In case of an error, the returned player state will not be different from the current one. 
	 * It will also contain a <code>TransitionResult</code> which gives the reason for the failure.</p>
	 * @param unit   the unit performing the operation
	 * @param desiredResources   the desired resources and their quantity
	 * @return the new player state or null if the player attempts to move outside his turn.
	 */
    public PlayerState pickupResources(UnitState unit, HashMap<BasicResourceType, Integer> desiredResources);
    
    /**
	 * Allows the given unit to drop the specified quantities of basic resources.
	 * <p> If successful, the {@link UnitState} of the unit performing the action will have
	 * the specified resource quantities subtracted from its <code>carriedResources</code> field.</p>
	 * <p> The dropped resources will now become visible to other units that pass near the current cell</p>
	 * <p>In case of an error, the returned player state will not be different from the current one. 
	 * It will also contain a <code>TransitionResult</code> which gives the reason for the failure.</p>
	 * @param unit   the unit executing the drop action
	 * @param unwantedResources   the unwanted resources and their quantities
	 * @return the new player state or null if the player attempts to move outside his turn.
	 */
    public PlayerState pickupObjects(UnitState unit, HashMap<CraftedObject, Integer> desiredObjects);
    
    /**
	 * Allows the given unit to drop the specified quantities of crafted objects.
	 * <p> If successful, the {@link UnitState} of the unit performing the action will have
	 * the specified object quantities subtracted from its <code>carriedObjects</code> field.</p>
	 * <p> The dropped artifacts will now become visible to other units that pass near the current cell</p>
	 * <p>In case of an error, the returned player state will not be different from the current one. 
	 * It will also contain a <code>TransitionResult</code> which gives the reason for the failure.</p>
	 * @param unit   the unit executing the drop action
	 * @param unwantedResources   the unwanted objects and their quantities
	 * @return the new player state or null if the player attempts to move outside his turn.
	 */
    public PlayerState dropResources(UnitState unit, HashMap<BasicResourceType, Integer> unwantedResources);
    
    /**
	 * Allows the given unit to drop the specified quantities of crafted objects.
	 * <p> If successful, the {@link UnitState} of the unit performing the action will have
	 * the specified object quantities subtracted from its <code>carriedObjects</code> field.</p>
	 * <p> The dropped artifacts will now become visible to other units that pass near the current cell</p>
	 * <p>In case of an error, the returned player state will not be different from the current one. 
	 * It will also contain a <code>TransitionResult</code> which gives the reason for the failure.</p>
	 * @param unit   the unit executing the drop action
	 * @param unwantedResources   the unwanted objects and their quantities
	 * @return the new player state or null if the player attempts to move outside his turn.
	 */
    public PlayerState dropObjects(UnitState unit, HashMap<CraftedObject, Integer> unwantedObjects);
    
    /**
	 * Allows the given unit to craft the target object using the specified ingredients. 
	 * <p> If successful, the target object will be added to the unit's <code>carriedObjects</code> field.
	 * Also, the unit performing the action will have the ingredient quantities 
	 * subtracted from its <code>carriedResources</code> / <code>carriedObjects</code> field.</p>
	 * <p>In case of an error, the returned player state will not be different from the current one. 
	 * It will also contain a <code>TransitionResult</code> which gives the reason for the failure.</p>
	 * @param unit   the unit performing the crafting action
	 * @param target   the artifact that is supposed to be built
	 * @param usedObjects   the list of object ingredients, if any. The value of this parameter may be null if the target object is of a simple type 
	 * @param usedResources   the list of basic resource ingredients, if any. The value of this parameter may be null if the target object is of a complex type
	 * @return the new player state or null if the player attempts to move outside his turn.
	 */
    public PlayerState craftObject(UnitState unit, CraftedObject target, 
			HashMap<CraftedObject, Integer> usedObjects, HashMap<BasicResourceType, Integer> usedResources);
    
    /**
	 * Allows the given unit to sell the specified quantity of target artifacts.
	 * <p> If successful, the value of the target artifact will be added to the players's score.
	 * Also, the unit performing the action will have the specified quantity of target artifacts subtracted 
	 * from its <code>carriedObjects</code> field.</p>
	 * <p>In case of an error, the returned player state will not be different from the current one. 
	 * It will also contain a <code>TransitionResult</code> which gives the reason for the failure.</p>
	 * @param unit   the unit performing the selling action
	 * @param craftedObject   the target object to be sold
	 * @param quantity   the number of target objects to be sold
	 * @return the new player state or null if the player attempts to move outside his turn.
	 */
    public PlayerState sellObject(UnitState unit, CraftedObject craftedObject, Integer quantity);
    
    /**
	 * Allows the given unit to place a tower in its current position.
	 * <p> If successful, the tower will be placed in the current position of the builder unit. 
	 * Also, the new tower will be added to the <code>availableTowers</code> field 
	 * of the returned player state. </p>
	 * <p>In case of an error, the returned player state will not be different from the current one. 
	 * It will also contain a <code>TransitionResult</code> which gives the reason for the failure.</p>
	 * @param unit   the unit building the tower
	 * @return the new player state or null if the player attempts to move outside his turn.
	 */
    public PlayerState placeTower(UnitState unit);
    
    /**
	 * Allows the given unit to buy a desired blueprint
	 * <p>If successful, the returned player state will have the required blueprint added to its
	 * <code>boughtBlueprints</code> field. </p>
	 * <p>In case of an error, the returned player state will not be different from the current one. 
	 * It will also contain a <code>TransitionResult</code> which gives the reason for the failure.</p>
	 * @param unit   the unit buying the blueprint
	 * @param blueprint   the desired blueprint
	 * @return the new player state or null if the player attempts to move outside his turn.
	 */
    public PlayerState buyBlueprint(UnitState unit, Blueprint blueprint);
}
