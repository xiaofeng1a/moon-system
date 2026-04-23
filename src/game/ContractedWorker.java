package game;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.displays.Menu;
import edu.monash.fit2099.engine.items.Inventory;
import edu.monash.fit2099.engine.positions.GameMap;

/**
 * This brave soul is capable of performing complex tasks such as picking up trash
 * off the floor, swiping plastic cards at stubborn doors, and drinking mystery
 * fluids to stay alive.
 *
 * <p>Each worker carries a {@link WeightLimitedInventory} with a 50-unit weight cap.
 * Item-specific actions (consuming the flask, using the first aid kit, unlocking doors
 * with the access card) are delegated to the items themselves via
 * {@code Item.allowableActions()}, keeping this class lean.
 */
public class ContractedWorker extends Actor {

    /**
     * Constructor.
     *
     * @param name        the worker's name
     * @param displayChar the character displayed on the map
     * @param hitPoints   starting hit points
     * @param inventory   the worker's inventory
     */
    public ContractedWorker(String name, char displayChar, int hitPoints, Inventory inventory) {
        super(name, displayChar, hitPoints, inventory);
    }

    /**
     * Selects and returns the action to perform this turn.
     *
     * <p>If the worker is unconscious they are removed from the map immediately.
     * Otherwise, the engine-assembled {@code actions} list (which already includes
     * item-carried actions, movement, pick-up/drop, and ground interactions) is
     * presented to the player via the console menu.
     *
     * @param actions    collection of possible Actions for this Actor
     * @param lastAction The Action this Actor took last turn
     * @param map        the map containing the Actor
     * @param display    the I/O object to which messages may be written
     * @return the action chosen for this turn
     */
    @Override
    public Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display) {
        if (!this.isConscious()) {
            this.unconscious(map);
            return new DoNothingAction();
        }

        // Handle multi-turn Actions
        if (lastAction.getNextAction() != null) {
            return lastAction.getNextAction();
        }

        // Present the menu assembled by the engine (items, movement, ground, etc.)
        Menu menu = new Menu(actions);
        return menu.showMenu(this, display);
    }
}