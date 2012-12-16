package org.aimas.craftingquest.user;

import java.util.HashMap;

import org.aimas.craftingquest.state.BasicUnit;
import org.aimas.craftingquest.state.Blueprint;
import org.aimas.craftingquest.state.ICarriable;
import org.aimas.craftingquest.state.PlayerState;
import org.aimas.craftingquest.state.Point2i;
import org.aimas.craftingquest.state.UnitState;
import org.aimas.craftingquest.state.objects.IEquippable;
import org.aimas.craftingquest.state.resources.ResourceType;


public interface IPlayerActions {

    public void addArtificialIntelligence(IPlayerHooks usercode);

    /**
     * Returns the current locally stored player state.
     * @return the locally stored player state that is the result of the last performed transition
     */
    public PlayerState getPlayerState();

    /**
	 * Returns the server stored player state. It is recommended to call this method at the start of each 
	 * turn to get a fresh view of your units' states. They might have been affected by an opponent's 
	 * attack action, traps or tower drains.
	 * Likewise, you will thus determine the remaining strength and the updated sight for
	 * each of your still active towers, as well as any traps that have been sprung.
	 * @return the player state as seen by the server application at the moment of inquiry or null if the 
	 * call is made outside the player's turn
	 */
    public PlayerState requestState();
    
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
 	 * Allows the given unit to pick up the resources contained in the current cell.
 	 * <p> If successful, the {@link UnitState} of the unit performing the action will have
 	 * the specified resources added to its <code>carriedResources</code> field.</p>
 	 * <p>In case of an error, the returned player state will not be different from the current one. 
 	 * It will also contain a <code>TransitionResult</code> which gives the reason for the failure.</p>
 	 * @param unit   the unit performing the operation
 	 * @param desiredResources   the desired resources and their quantity
	 * @return the new player state or null if the player attempts to move outside his turn.
 	 */
    public PlayerState pickupResources(UnitState unit, HashMap<ResourceType, Integer> desiredResources);
    
    /**
	 * Allows the given unit to pick up the crafted objects contained in the current cell.
	 * <p> If successful, the {@link UnitState} of the unit performing the action will have
	 * the specified objects added to its <code>carriedObjects</code> field.</p>
	 * <p>In case of an error, the returned player state will not be different from the current one. 
	 * It will also contain a <code>TransitionResult</code> which gives the reason for the failure.</p>
	 * @param unit   the unit performing the operation
	 * @param desiredObjects	the desired objects and their quantity
	 * @return the new player state or null if the player attempts to move outside his turn.
	 */
    public PlayerState pickupObjects(UnitState unit, HashMap<ICarriable, Integer> desiredObjects);
    
    
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
    public PlayerState dropResources(UnitState unit, HashMap<ResourceType, Integer> unwantedResources);
    
    /**
	 * Allows the given unit to drop the specified quantities of crafted objects.
	 * <p> If successful, the {@link UnitState} of the unit performing the action will have
	 * the specified object quantities subtracted from its <code>carriedObjects</code> field.</p>
	 * <p> The dropped artifacts will now become visible to other units that pass near the current cell</p>
	 * <p>In case of an error, the returned player state will not be different from the current one. 
	 * It will also contain a <code>TransitionResult</code> which gives the reason for the failure.</p>
	 * @param unit   the unit executing the drop action
	 * @param unwantedObjects   the unwanted objects and their quantities
	 * @return the new player state or null if the player attempts to move outside his turn.
	 */
    public PlayerState dropObjects(UnitState unit, HashMap<ICarriable, Integer> unwantedObjects);
    
    /**
 	 * Allows the given unit to craft the object described in the blueprint using the required ingredients. 
 	 * <p> If successful, the target object will be added to the unit's <code>carriedObjects</code> field.
 	 * Also, the unit performing the action will have the ingredient quantities 
 	 * subtracted from its <code>carriedResources</code> / <code>carriedObjects</code> field.</p>
 	 * <p>In case of an error, the returned player state will not be different from the current one. 
 	 * It will also contain a <code>TransitionResult</code> which gives the reason for the failure.</p>
 	 * @param unit   the unit performing the crafting action
 	 * @param blueprint   the blueprint specifying the type of object that is supposed to be built
 	 * @return the new player state or null if the player attempts to move outside his turn.
 	 */
    public PlayerState craftObject(UnitState unit, Blueprint blueprint);
    
    /**
 	 * Allows the given unit to equip the target crafted object. 
 	 * <p> If successful, the target object, a sword or an armour, will be equiped.
 	 * <p>In case of an error, the returned player state will not be different from the current one. 
 	 * It will also contain a <code>TransitionResult</code> which gives the reason for the failure.</p>
 	 * @param unit   the unit performing the crafting action
 	 * @param target   the armour / sword that is supposed to be equiped
	 * @return the new player state or null if the player attempts to equip an invalid object.
 	 */
	public PlayerState equip(UnitState unit, IEquippable target);
	
	/**
 	 * Allows the given unit to place a trap in its current position. The trap must first be built
 	 * using the ingredients specified in the blueprint that describes it.
 	 * <p> If successful, the trap will be placed in the current position of the builder unit. 
 	 * Also, the new trap will be added to the <code>availableTraps</code> field 
 	 * of the returned player state. </p>
 	 * <p>In case of an error, the returned player state will not be different from the current one. 
 	 * It will also contain a <code>TransitionResult</code> which gives the reason for the failure.</p>
 	 * @param unit  		the unit building the trap
 	 * @param blueprint		the blueprint specifying the trap object
	 * @return the new player state or null if the player attempts to move outside his turn.
 	 */
	public PlayerState placeTrap(UnitState unit, Blueprint blueprint);

    
	/**
 	 * Allows the given unit to place a tower in its current position. The tower must first be built
 	 * using the ingredients specified in the blueprint that describes the tower strategic object.
 	 * <p> If successful, the tower will be placed in the current position of the builder unit. 
 	 * Also, the new tower will be added to the <code>availableTowers</code> field 
 	 * of the returned player state. </p>
 	 * <p>In case of an error, the returned player state will not be different from the current one. 
 	 * It will also contain a <code>TransitionResult</code> which gives the reason for the failure.</p>
 	 * @param unit  		the unit building the tower
 	 * @param blueprint		the blueprint specifying the tower object
	 * @return the new player state or null if the player attempts to move outside his turn.
 	 */
    public PlayerState placeTower(UnitState unit, Blueprint blueprint);
    
    /**
	 * Allows the given unit to attack an enemy unit if it is in its
	 * vicinity.
	 * <p>The attack is done by investing as much energy as desired.  Damage
	 * is done by multiplying this energy with the equipped sword's modifier
	 * and enemy's shield modifier: <code>damage = energy * (1 + swordMod) *
	 * (1 - shieldMod)</code>.</p>
	 * <p>If there's no sword then <code>swordMod = 0</code>. Same for
	 * missing armour.</p>
	 * <p>The resulting damage decreases the unit's <code>maxEnergy</code>
	 * value, equivalent to unit's life.</p>
	 * <p>The attacked unit had the possibility of preparing for
	 * retaliation. In this case and if the unit survives the attack and it
	 * has enough energy then a retaliation is done, using the same formula
	 * as above.</p>
	 * <p>In case of an error, the returned player state will not be
	 * different from the current one.  It will also contain a
	 * <code>TransitionResult</code> which gives the reason for the
	 * failure. Otherwise, the returned state will be updated to reflect
	 * attack and retaliation.</p>
	 * @param unit   the unit doing the attack
	 * @param attackedUnit  the enemy unit being attacked
	 * @param energyBackup  the amount of energy points used for damage
	 * @return the new player state or null if the player attempts to move outside his turn.
	 */
    public PlayerState attack(UnitState unit, BasicUnit attackedUnit, int energyBackup);
    
    /**
	 * Allows the given unit to prepare for retaliation in the next round
	 * should it be placed under attack.
	 * <p>For a description of attack and retaliation @see attack
	 * description.</p>
	 * <p>If a unit is attacked multiple times during a round then after
	 * each attack there's a possibility for retaliation, as long as there
	 * is enough energy. Since each retaliation decreases unit energy, it is
	 * desirable to select a minimum energy level after which to stop
	 * retaliating.</p>
	 * <p>This action doesn't fail, it just sets up some variables.</p>
	 * @param unit the unit which prepares for retaliation
	 * @param energy the amount of energy invested in one retaliation
	 * @param threshold the amount of energy at which the retaliation should
	 * stop.
	 */
	public PlayerState prepare(UnitState unit, int energy, int threshold);
    

	/**
	 * Performs an upgrade for the given crafted object described by the <code>blueprint</code> by paying the
	 * required upgrade cost (available by calling <code>blueprint.getUpgradeCost()</code>) in gold nuggets.
	 * <p> If successful, the PlayerState returned will contain an additional blueprint describing the requirements
	 * for the next level of the crafted object.
	 * <p>In case of an error, the returned player state will not be different from the current one. 
	 * It will also contain a <code>TransitionResult</code> which gives the reason for the failure.</p>
	 * @param unit   the unit performing the upgrade
	 * @param blueprint  the blueprint that is upgraded
	 * @return the new player state or null if the player attempts to move outside his turn.
	 */
    public PlayerState upgrade(UnitState unit, Blueprint blueprint);
    
}
